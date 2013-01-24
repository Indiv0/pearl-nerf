package com.github.indiv0.pearlnerf.util;

import org.bukkit.configuration.file.YamlConfiguration;

import ashulman.mbapi.plugin.MbapiPlugin;
import ashulman.mbapi.util.ConfigurationContext;

public class PearlNerfConfigurationContext extends ConfigurationContext {
    public final boolean limitPearlDrops;
    public final double pearlDropRate;
    public final boolean applySlownessDebuff;
    public final int slownessDebuffDuration;

    public PearlNerfConfigurationContext(MbapiPlugin plugin) {
        super(plugin);

        plugin.saveDefaultConfig();

        // Tries to load the configuration from the file into configYaml.
        YamlConfiguration configYaml = (YamlConfiguration) plugin.getConfig();

        limitPearlDrops = configYaml.getBoolean("limitPearlDrops", true);
        pearlDropRate = configYaml.getDouble("pearlDropRate", 0.05);
        applySlownessDebuff = configYaml.getBoolean("limitPearlDrops", true);
        slownessDebuffDuration = configYaml.getInt("maxPearlStackSize", 60);
    }
}
