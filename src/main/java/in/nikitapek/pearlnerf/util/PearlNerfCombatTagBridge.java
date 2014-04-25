package in.nikitapek.pearlnerf.util;

import com.amshulman.mbapi.MbapiPlugin;
import com.trc202.CombatTag.CombatTag;
import com.trc202.CombatTagApi.CombatTagApi;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PearlNerfCombatTagBridge {
    private final CombatTagApi combatTagAPI;

    public PearlNerfCombatTagBridge(MbapiPlugin plugin) {
        final Plugin tempCombatTag = plugin.getServer().getPluginManager().getPlugin("CombatTag");

        if (tempCombatTag == null) {
            combatTagAPI = null;
            return;
        }

        combatTagAPI = new CombatTagApi((CombatTag) tempCombatTag);
    }

    public boolean isInCombat(Player player) {
        return combatTagAPI.isInCombat(player);
    }

    public void tagPlayer(Player player) {
        combatTagAPI.tagPlayer(player);
    }
}
