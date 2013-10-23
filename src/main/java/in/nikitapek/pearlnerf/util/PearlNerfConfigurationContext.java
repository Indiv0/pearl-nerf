package in.nikitapek.pearlnerf.util;

import com.amshulman.mbapi.MbapiPlugin;
import com.amshulman.mbapi.util.ConfigurationContext;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class PearlNerfConfigurationContext extends ConfigurationContext {
    public final int pearlCooldownTime;
    public final boolean useCombatTag;
    public final boolean requireCombatTagForEffect;
    public final boolean tagOnPearl;
    public final boolean damageOnPearl;
    public final int pearlDamageAmount;
    public final boolean useHumbugCorrection;
    public final int fireDelay;
    public final boolean onlyDamageAutoClickers;
    public final int minimumAutoClickedPearlCount;
    public final boolean printDebugInfo;

    public PearlNerfConfigurationContext(MbapiPlugin plugin) {
        super(plugin);

        plugin.saveDefaultConfig();

        pearlCooldownTime = plugin.getConfig().getInt("pearlCooldownTime", 0);
        useCombatTag = plugin.getConfig().getBoolean("useCombatTag", true);
        requireCombatTagForEffect = plugin.getConfig().getBoolean("requireCombatTagForEffect", true);
        tagOnPearl = plugin.getConfig().getBoolean("tagOnPearl", true);
        damageOnPearl = plugin.getConfig().getBoolean("damageOnPearl", true);
        int tempPearlDamageAmount = plugin.getConfig().getInt("pearlDamageAmount", 1);
        if (tempPearlDamageAmount < 0) {
            pearlDamageAmount = 0;
            Bukkit.getLogger().log(Level.WARNING, "pearlDamageAmount cannot be less than 0. Defaulting to 0.");
        } else {
            pearlDamageAmount = tempPearlDamageAmount;
        }
        useHumbugCorrection = plugin.getConfig().getBoolean("useHumbugCorrection", true);
        fireDelay = plugin.getConfig().getInt("fireDelay", 20);
        onlyDamageAutoClickers = plugin.getConfig().getBoolean("onlyDamageAutoClickers", true);
        minimumAutoClickedPearlCount = plugin.getConfig().getInt("minimumAutoClickedPearlCount", 15);
        printDebugInfo = plugin.getConfig().getBoolean("printDebugInfo", false);
    }
}
