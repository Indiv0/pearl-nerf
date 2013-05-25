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
import com.amshulman.mbapi.util.LocationUtil;
import com.amshulman.typesafety.TypeSafeMap;
import com.amshulman.typesafety.impl.TypeSafeMapImpl;
import com.trc202.CombatTag.CombatTag;
import com.trc202.CombatTagApi.CombatTagApi;

public final class PearlNerfListener implements Listener {
    private static final DecimalFormat FORMATTER = new DecimalFormat("##0.0");

    private final CombatTagApi ctAPI;
    private final TypeSafeMap<String, Long> cooldownTimes;

    private final int cooldownMillis;

    public PearlNerfListener(final PearlNerfConfigurationContext configurationContext) {
        ctAPI = new CombatTagApi((CombatTag) Bukkit.getPluginManager().getPlugin("CombatTag"));
        cooldownTimes = new TypeSafeMapImpl<String, Long>(new HashMap<String, Long>(), CoreTypes.STRING, CoreTypes.LONG);

        cooldownMillis = configurationContext.pearlCooldownTime * 1000;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEnderPearlThrow(final ProjectileLaunchEvent event) {
        final Projectile item = event.getEntity();

        if (EntityType.ENDER_PEARL.equals(item.getType()) && item.getShooter() instanceof Player) {
            final Player player = (Player) item.getShooter();

            final long end = unpackLong(cooldownTimes.get(player.getName()));
            final long time = System.currentTimeMillis();

            if (end > time) {
                if (ctAPI.isInCombat(player)) {
                    final String remaining = FORMATTER.format((end - time) / 1000d);
                    if (!"0.0".equals(remaining)) {
                        player.sendMessage(ChatColor.GRAY + "Ender pearl is on cooldown. Please wait another " + remaining + " seconds.");
                        event.setCancelled(true);

                        final ItemStack inHand = player.getItemInHand();
                        inHand.setAmount(inHand.getAmount() + 1);
                        player.setHealth(player.getHealth() - 1);
                    }
                }
            } else {
                cooldownTimes.put(player.getName(), time + cooldownMillis);
            }
        }
    }

    /*
     * The following code is originally from Humbug (https://github.com/erocs/Humbug) and is included under the following license:
     * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
     * Neither the name of Erocs nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(final PlayerTeleportEvent event) {
        if (!TeleportCause.ENDER_PEARL.equals(event.getCause())) {
            return;
        }

        if (ctAPI.isInCombat(event.getPlayer())) {
            ctAPI.tagPlayer(event.getPlayer());
        }

        Location destination = event.getTo();
        final World world = destination.getWorld();

        final Block toBlock = world.getBlockAt(destination);
        final Block aboveBlock = world.getBlockAt(destination.getBlockX(), destination.getBlockY() + 1, destination.getBlockZ());
        final Block belowBlock = world.getBlockAt(destination.getBlockX(), destination.getBlockY() - 1, destination.getBlockZ());
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
            final Block aboveHeadBlock = world.getBlockAt(aboveBlock.getX(), aboveBlock.getY() + 1, aboveBlock.getZ());
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

        destination = LocationUtil.center(destination.getWorld(), destination.getX(), destination.getY() + height, destination.getZ(), destination.getPitch(), destination.getYaw());
    }

    private static long unpackLong(final Long l) {
        return (l == null) ? 0 : l;
    }
}
