package in.nikitapek.pearlnerf.events;

import in.nikitapek.pearlnerf.util.PearlNerfConfigurationContext;

import java.text.DecimalFormat;
import java.util.HashMap;

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

import com.amshulman.mbapi.util.CoreTypes;
import com.amshulman.typesafety.TypeSafeMap;
import com.amshulman.typesafety.impl.TypeSafeMapImpl;
import com.trc202.CombatTag.CombatTag;
import com.trc202.CombatTagApi.CombatTagApi;

public class PearlNerfListener implements Listener {

    private static DecimalFormat formatter = new DecimalFormat("##0.0");

    private final CombatTagApi ctAPI;
    private final TypeSafeMap<String, Long> cooldownTimes;

    private final int cooldownMillis;

    public PearlNerfListener(PearlNerfConfigurationContext configurationContext) {
        ctAPI = new CombatTagApi((CombatTag) Bukkit.getPluginManager().getPlugin("CombatTag"));
        cooldownTimes = new TypeSafeMapImpl<String, Long>(new HashMap<String, Long>(), CoreTypes.STRING, CoreTypes.LONG);

        cooldownMillis = configurationContext.pearlCooldownTime * 1000;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEnderPearlThrow(ProjectileLaunchEvent event) {
        Projectile item = event.getEntity();

        // Checks if the projectile is an enderpearl launched by a player
        if (!(EntityType.ENDER_PEARL.equals(item.getType()) && item.getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) item.getShooter();

        // Retrieves the time at which the pearl cooldown for the player will be complete.
        long end = unpackLong(cooldownTimes.get(player.getName()));
        long time = System.currentTimeMillis();

        if (end <= time) {
            // If the cooldown has completed for a player, then the cooldown is once again restarted.
            cooldownTimes.put(player.getName(), time + cooldownMillis);
        } else {
            // If the player is combat tagged, then the time remaining in the cooldown is sent to the player in a message.
            // The player also fails to teleport with the pearl, and loses one health point.
            if (ctAPI.isInCombat(player)) {
                String remaining = formatter.format((end - time) / 1000d);
                if (!remaining.equals("0.0")) {
                    player.sendMessage(ChatColor.GRAY + "Ender pearl is on cooldown. Please wait another " + remaining + " seconds.");
                    event.setCancelled(true);

                    ItemStack inHand = player.getItemInHand();
                    inHand.setAmount(inHand.getAmount() + 1);
                    player.setHealth(player.getHealth() - 1);
                }
            }
        }
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

        if (ctAPI.isInCombat(event.getPlayer())) {
            ctAPI.tagPlayer(event.getPlayer());
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
}
