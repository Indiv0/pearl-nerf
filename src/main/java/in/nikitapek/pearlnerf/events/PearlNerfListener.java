package in.nikitapek.pearlnerf.events;

import in.nikitapek.pearlnerf.util.PearlNerfConfigurationContext;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

        if (EntityType.ENDER_PEARL.equals(item.getType()) && item.getShooter() instanceof Player) {
            Player player = (Player) item.getShooter();

            if (ctAPI.isInCombat(player)) {
                long end = unpackLong(cooldownTimes.get(player.getName()));
                long time = System.currentTimeMillis();

                if (end > time) {
                    String remaining = formatter.format((end - time) / 1000d);
                    if (!remaining.equals("0.0")) {
                        player.sendMessage(ChatColor.GRAY + "Ender pearl is on cooldown. Please wait another " + remaining + " seconds.");
                        event.setCancelled(true);

                        ItemStack inHand = player.getItemInHand();
                        inHand.setAmount(inHand.getAmount() + 1);
                        player.setHealth(player.getHealth() - 1);
                    }
                } else {
                    cooldownTimes.put(player.getName(), time + cooldownMillis);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        if (TeleportCause.ENDER_PEARL.equals(event.getCause()) && ctAPI.isInCombat(event.getPlayer())) {
            ctAPI.tagPlayer(event.getPlayer());
        }
    }

    private static long unpackLong(Long l) {
        return (l == null) ? 0 : l;
    }
}
