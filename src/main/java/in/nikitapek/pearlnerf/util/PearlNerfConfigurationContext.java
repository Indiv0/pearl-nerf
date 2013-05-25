package in.nikitapek.pearlnerf.util;

import com.amshulman.mbapi.MbapiPlugin;
import com.amshulman.mbapi.util.ConfigurationContext;

public final class PearlNerfConfigurationContext extends ConfigurationContext {
    public final int pearlCooldownTime;

    public PearlNerfConfigurationContext(final MbapiPlugin plugin) {
        super(plugin);

        plugin.saveDefaultConfig();

        pearlCooldownTime = plugin.getConfig().getInt("pearlCooldownTime", 0);
    }
}
