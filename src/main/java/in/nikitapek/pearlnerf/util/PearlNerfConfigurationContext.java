package in.nikitapek.pearlnerf.util;

import com.amshulman.mbapi.MbapiPlugin;
import com.amshulman.mbapi.util.ConfigurationContext;

public class PearlNerfConfigurationContext extends ConfigurationContext {
    public final int pearlCooldownTime;
    public final boolean useCombatTag;
    public final boolean requireCombatTagForEffect;
    public final boolean tagOnPearl;

    public PearlNerfConfigurationContext(MbapiPlugin plugin) {
        super(plugin);

        plugin.saveDefaultConfig();

        pearlCooldownTime = plugin.getConfig().getInt("pearlCooldownTime", 0);
        useCombatTag = plugin.getConfig().getBoolean("useCombatTag", true);
        requireCombatTagForEffect = plugin.getConfig().getBoolean("requireCombatTagForEvent", true);
        tagOnPearl = plugin.getConfig().getBoolean("tagOnPearl", true);
    }
}
