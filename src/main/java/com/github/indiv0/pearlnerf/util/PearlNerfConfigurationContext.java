package com.github.indiv0.pearlnerf.util;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.configuration.file.YamlConfiguration;

import ashulman.mbapi.plugin.MbapiPlugin;
import ashulman.mbapi.util.ConfigurationContext;

import com.github.indiv0.pearlnerf.CombatTagHook;

public class PearlNerfConfigurationContext extends ConfigurationContext {
    public final boolean limitPearlDrops;
    public final double pearlDropRate;
    public final boolean applySlownessDebuff;
    public final int slownessDebuffDuration;

    public final CombatTagHook ctHook;

    public PearlNerfConfigurationContext(MbapiPlugin plugin) {
        super(plugin);
        plugin.saveDefaultConfig();

        ctHook = ctHookWrapper();

        // Tries to load the configuration from the file into configYaml.
        YamlConfiguration configYaml = (YamlConfiguration) plugin.getConfig();

        limitPearlDrops = configYaml.getBoolean("limitPearlDrops", true);
        pearlDropRate = configYaml.getDouble("pearlDropRate", 0.05);
        applySlownessDebuff = configYaml.getBoolean("limitPearlDrops", true);
        slownessDebuffDuration = configYaml.getInt("maxPearlStackSize", 60);
    }

    private static CombatTagHook ctHookWrapper() {
        try {
            return new CombatTagHook();
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException | NoSuchMethodException | SecurityException e) {
            return null;
        }
    }
}
