package in.nikitapek.pearlnerf.util;

import com.amshulman.mbapi.MbapiPlugin;
import com.amshulman.mbapi.util.ConfigurationContext;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class PearlNerfConfigurationContext extends ConfigurationContext {
    public final int pearlCooldownTime;
<<<<<<< Updated upstream
=======
    public final boolean useCombatTag;
    public final boolean requireCombatTagForEffect;
    public final boolean tagOnPearl;
    public final boolean damageOnPearl;
    public final int pearlDamageAmount;
>>>>>>> Stashed changes

    public PearlNerfConfigurationContext(MbapiPlugin plugin) {
        super(plugin);

        plugin.saveDefaultConfig();

        pearlCooldownTime = plugin.getConfig().getInt("pearlCooldownTime", 0);
<<<<<<< Updated upstream
=======
        useCombatTag = plugin.getConfig().getBoolean("useCombatTag", true);
        requireCombatTagForEffect = plugin.getConfig().getBoolean("requireCombatTagForEvent", true);
        tagOnPearl = plugin.getConfig().getBoolean("tagOnPearl", true);
        damageOnPearl = plugin.getConfig().getBoolean("damageOnPearl", true);
        int tempPearlDamageAmount = plugin.getConfig().getInt("pearlDamageAmount", 1);
        if (tempPearlDamageAmount < 0) {
            pearlDamageAmount = 0;
            Bukkit.getLogger().log(Level.WARNING, "pearlDamageAmount cannot be less than 0. Defaulting to 0.");
        } else {
            pearlDamageAmount = tempPearlDamageAmount;
        }
>>>>>>> Stashed changes
    }
}
