package com.github.indiv0.pearlnerf;

import java.util.Iterator;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.indiv0.pearlnerf.util.PearlNerfConfigurationContext;

public class PearlNerfListener implements Listener {
    public static PearlNerfConfigurationContext configurationContext;

    static double enderPearlDropChance = 0.05;
    static Random random = new Random();

    public PearlNerfListener(PearlNerfConfigurationContext configurationContext) {
        PearlNerfListener.configurationContext = configurationContext;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getType() != EntityType.ENDERMAN)
            return;

        if (event.getDrops().isEmpty())
            return;

        Iterator<ItemStack> itemStackIterator = event.getDrops().iterator();

        while (itemStackIterator.hasNext())
            if (itemStackIterator.next().getType() == Material.ENDER_PEARL)
                if (random.nextDouble() > enderPearlDropChance)
                    itemStackIterator.remove();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (!action.equals(Action.RIGHT_CLICK_AIR) && !action.equals(Action.RIGHT_CLICK_BLOCK))
            return;

        Player player = event.getPlayer();
        ItemStack thrown = player.getItemInHand();

        if (thrown == null || thrown.getType() != Material.ENDER_PEARL)
            return;

        if (configurationContext.applySlownessDebuff)
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, configurationContext.slownessDebuffDuration * 20, 2), true);

        if (!player.hasPermission("pearlnerf.tag"))
            return;

        Bukkit.getServer().getPluginManager().callEvent(new EntityDamageByEntityEvent(player, player, DamageCause.ENTITY_ATTACK, 0));
    }
}
