package in.nikitapek.pearlnerf.events;

import com.amshulman.mbapi.util.CoreTypes;
import com.amshulman.typesafety.TypeSafeMap;
import com.amshulman.typesafety.impl.TypeSafeMapImpl;
import in.nikitapek.pearlnerf.util.PearlNerfCombatTagBridge;
import in.nikitapek.pearlnerf.util.PearlNerfConfigurationContext;
import in.nikitapek.pearlnerf.util.SupplementaryTypes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.javatuples.Pair;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.logging.Level;

public class PearlNerfListener implements Listener {
    private static final DecimalFormat formatter = new DecimalFormat("##0.0");

    private final TypeSafeMap<String, Pair<Integer, Long>> playerAutoClickInfo = new TypeSafeMapImpl<>(new HashMap<String, Pair<Integer, Long>>(), CoreTypes.STRING, SupplementaryTypes.AUTO_CLICK_TRACKING_INFO);
    private final TypeSafeMap<String, Long> cooldownTimes;

    private final int cooldownMillis;
    private final boolean useCombatTag;
    private final boolean requireCombatTagForEffect;
    private final boolean tagOnPearl;
    private final boolean damageOnPearl;
    private final int pearlDamageAmount;
    private final boolean useHumbugCorrection;
    private final int fireDelay;
    private final boolean onlyDamageAutoClickers;
    private final int minimumAutoClickedPearlCount;
    private final boolean printDebugInfo;

    private PearlNerfCombatTagBridge combatTagBridge;
    private boolean combatTagBridged = false;

    public PearlNerfListener(PearlNerfConfigurationContext configurationContext) {
        cooldownTimes = new TypeSafeMapImpl<>(new HashMap<String, Long>(), CoreTypes.STRING, CoreTypes.LONG);

        // Retrieve configuration options.
        cooldownMillis = configurationContext.pearlCooldownTime * 1000;
        useCombatTag = configurationContext.useCombatTag;
        requireCombatTagForEffect = configurationContext.requireCombatTagForEffect;
        tagOnPearl = configurationContext.tagOnPearl;
        damageOnPearl = configurationContext.damageOnPearl;
        pearlDamageAmount = configurationContext.pearlDamageAmount;
        useHumbugCorrection = configurationContext.useHumbugCorrection;
        fireDelay = configurationContext.fireDelay;
        onlyDamageAutoClickers = configurationContext.onlyDamageAutoClickers;
        minimumAutoClickedPearlCount = configurationContext.minimumAutoClickedPearlCount;
        printDebugInfo = configurationContext.printDebugInfo;

        if (!useCombatTag) {
            return;
        }

        try {
            combatTagBridge = new PearlNerfCombatTagBridge(configurationContext.plugin);
        } catch (final NoClassDefFoundError ex) {
            configurationContext.plugin.getLogger().log(Level.WARNING, "\"useCombatTag\" true but CombatTag not found. CombatTag related features will not be enabled.");
            return;
        }

        combatTagBridged = true;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEnderPearlThrow(ProjectileLaunchEvent event) {
        Projectile item = event.getEntity();

        // Checks if the projectile is an enderpearl launched by a player
        if (!(EntityType.ENDER_PEARL.equals(item.getType()) && item.getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) item.getShooter();

        if (player.hasPermission("pearlnerf.exempt")) {
            return;
        }

        String playerName = player.getName();

        boolean isPlayerAutoClicking = checkForAutoClicking(playerName);

        // Retrieves the time at which the pearl cooldown for the player will be complete, as well as the current time.
        long end = unpackLong(cooldownTimes.get(playerName));
        long time = System.currentTimeMillis();

        // If the cooldown has completed for a player, then the cooldown is once again restarted.
        if (end <= time) {
            cooldownTimes.put(playerName, time + cooldownMillis);
            return;
        }

        // If the player is not combat-tagged, they are unaffected by the pearl cooldown.
        if (combatTagBridged && requireCombatTagForEffect && !combatTagBridge.isInCombat(player)) {
            return;
        }

        String remaining = formatter.format((end - time) / 1000d);
        if (remaining.equals("0.0")) {
            return;
        }

        // The time remaining in the cooldown is sent to the player in a message.
        player.sendMessage(ChatColor.GRAY + "Ender pearl is on cooldown. Please wait another " + remaining + " seconds.");

        // The player fails to teleport with the pearl, and loses one health point.
        event.setCancelled(true);
        ItemStack inHand = player.getItemInHand();
        inHand.setAmount(inHand.getAmount() + 1);

        if (!damageOnPearl) {
            return;
        }

        if (onlyDamageAutoClickers && !isPlayerAutoClicking) {
            return;
        }

        // Use the manual setHealth() method because damage() can't keep up with auto-click rates fast enough to kill the auto-clickers.
        player.setHealth(player.getHealth() - pearlDamageAmount);
    }

    /*
     * The following code is originally from Humbug (https://github.com/erocs/Humbug) and is included under the following license:
     *
     * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
     * Neither the name of Erocs nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        if (!TeleportCause.ENDER_PEARL.equals(event.getCause())) {
            return;
        }

        if (combatTagBridged && tagOnPearl && combatTagBridge.isInCombat(event.getPlayer())) {
            combatTagBridge.tagPlayer(event.getPlayer());
        }

        if (!useHumbugCorrection) {
            return;
        }

        Location destination = event.getTo();
        World world = destination.getWorld();

        Block toBlock = world.getBlockAt(destination);
        Block aboveBlock = world.getBlockAt(destination.getBlockX(), destination.getBlockY() + 1, destination.getBlockZ());
        Block belowBlock = world.getBlockAt(destination.getBlockX(), destination.getBlockY() - 1, destination.getBlockZ());
        boolean lowerBlockBypass = false;
        double height = 0.0;
        switch (toBlock.getType()) {
            case CHEST:
            case ENDER_CHEST:
                // Probably never will get hit directly
                height = 0.875;
                break;
            case STEP:
                lowerBlockBypass = true;
                height = 0.5;
                break;
            case WATER_LILY:
                height = 0.016;
                break;
            case ENCHANTMENT_TABLE:
                lowerBlockBypass = true;
                height = 0.75;
                break;
            case BED:
            case BED_BLOCK:
                // This one is tricky, since even with a height offset of 2.5, it still glitches.
                // Disabling teleporting on top of beds for now by leaving lowerBlockBypass false.
                break;
            case FLOWER_POT:
            case FLOWER_POT_ITEM:
                height = 0.375;
                break;
            case SKULL: // Probably never will get hit directly
                height = 0.5;
                break;
            default:
                break;
        }
        // Check if the below block is difficult
        // This is added because if you face downward directly on a gate, it will teleport your feet INTO the gate, thus bypassing the gate until you leave that block.
        switch (belowBlock.getType()) {
            case FENCE:
            case FENCE_GATE:
            case NETHER_FENCE:
            case COBBLE_WALL:
                height = 0.5;
                break;
            default:
                break;
        }

        boolean upperBlockBypass = false;
        if (height >= 0.5) {
            Block aboveHeadBlock = world.getBlockAt(aboveBlock.getX(), aboveBlock.getY() + 1, aboveBlock.getZ());
            if (!aboveHeadBlock.getType().isSolid()) {
                height = 0.5;
            } else {
                upperBlockBypass = true; // Cancel this event. What's happening is the user is going to get stuck due to the height.
            }
        }

        if (aboveBlock.getType().isSolid() || (toBlock.getType().isSolid() && !lowerBlockBypass) || upperBlockBypass) {
            event.setCancelled(true);
            return;
        }

        destination.setX(Math.floor(destination.getX()) + 0.5d);
        destination.setY(Math.floor(destination.getY()) + height);
        destination.setZ(Math.floor(destination.getZ()) + 0.5d);
    }

    private static long unpackLong(Long l) {
        return (l == null) ? 0 : l;
    }

    private boolean checkForAutoClicking(String playerName) {
        if (playerName == null) {
            return false;
        }

        Pair<Integer, Long> playerInfo = playerAutoClickInfo.get(playerName);

        if (playerInfo == null) {
            playerInfo = new Pair<>(0, new Long(0));
        }

        long currentTime = System.currentTimeMillis();
        // Update the pearl fire time and get the time since the last pearl fire.
        Long deltaT = currentTime - playerInfo.getValue1();
        playerInfo.setAt1(currentTime);

        if (deltaT > fireDelay) {
            // Reset the number of detected auto-clicks for the player.
            playerInfo.setAt0(0);
            return false;
        }

        // Increment the number of detected auto-click pearl fires for the player.
        playerInfo.setAt0(playerInfo.getValue0() + 1);

        boolean isPlayerAutoClicking = playerInfo.getValue0() >= minimumAutoClickedPearlCount;

        if (isPlayerAutoClicking && printDebugInfo) {
            Bukkit.getLogger().log(Level.INFO, playerName + " suspected of auto-clicking.");
        }

        return isPlayerAutoClicking;
    }
}
