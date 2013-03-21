package com.github.indiv0.pearlnerf.util;

import org.bukkit.configuration.file.FileConfiguration;

import ashulman.mbapi.MbapiPlugin;
import ashulman.mbapi.util.ConfigurationContext;

public class PearlNerfConfigurationContext extends ConfigurationContext {
    public final int pearlCooldownTime;

    public PearlNerfConfigurationContext(MbapiPlugin plugin) {
        super(plugin);
        
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        
        pearlCooldownTime = config.getInt("pearlCooldownTime", 0);
    }
}
