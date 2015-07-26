package in.nikitapek.pearlnerf.util;

import net.minelink.ctplus.CombatTagPlus;
import net.minelink.ctplus.TagManager;

import org.bukkit.entity.Player;

import com.amshulman.mbapi.MbapiPlugin;

public class PearlNerfCombatTagBridge {
    private final TagManager tagManager;

    public PearlNerfCombatTagBridge(MbapiPlugin plugin) {
        final CombatTagPlus tempCombatTag = (CombatTagPlus) plugin.getServer().getPluginManager().getPlugin("CombatTagPlus");

        if (tempCombatTag == null) {
            tagManager = null;
            return;
        }

        tagManager = tempCombatTag.getTagManager();
    }

    public boolean isInCombat(Player player) {
        return tagManager.isTagged(player.getUniqueId());
    }

    public void tagPlayer(Player player) {
        tagManager.tag(player, null);
    }
}
