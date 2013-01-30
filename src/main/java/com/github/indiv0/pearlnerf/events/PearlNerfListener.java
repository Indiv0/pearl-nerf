package com.github.indiv0.pearlnerf.events;

import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import com.github.indiv0.pearlnerf.CombatTagHook;
import com.github.indiv0.pearlnerf.util.PearlNerfConfigurationContext;

public class PearlNerfListener implements Listener {
    private final double enderPearlDropChance;
    private final CombatTagHook ctHook;

    public PearlNerfListener(PearlNerfConfigurationContext configurationContext) {
        enderPearlDropChance = configurationContext.pearlDropRate;
        ctHook = configurationContext.ctHook;

        assert (enderPearlDropChance >= 0d);
        assert (enderPearlDropChance <= 1d);
        assert (ctHook != null);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (EntityType.ENDERMAN.equals(event.getEntity().getType())) {
            for (Iterator<ItemStack> itemStackIterator = event.getDrops().iterator(); itemStackIterator.hasNext();) {
                if (Material.ENDER_PEARL.equals(itemStackIterator.next().getType())) {
                    if (Math.random() > enderPearlDropChance) {
                        itemStackIterator.remove();
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        if (TeleportCause.ENDER_PEARL.equals(event.getCause()) && !event.getPlayer().hasPermission("pearlnerf.tag")) {
            ctHook.tagPlayer(event.getPlayer().getName());
        }
    }
}
