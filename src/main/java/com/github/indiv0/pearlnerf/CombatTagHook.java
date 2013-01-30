package com.github.indiv0.pearlnerf;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;

import com.trc202.CombatTag.CombatTag;
import com.trc202.Containers.PlayerDataContainer;

public final class CombatTagHook {

    private final int tagDuration;
    private final CombatTag plugin;

    public CombatTagHook() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, NoSuchMethodException, SecurityException {
        plugin = (CombatTag) Bukkit.getPluginManager().getPlugin("CombatTag");
        tagDuration = plugin.getTagDuration();
    }

    public void tagPlayer(String playerName) {
        PlayerDataContainer damagerData = plugin.getPlayerData(playerName);
        if (damagerData == null) {
            damagerData = plugin.createPlayerData(playerName);
        }

        damagerData.setPvPTimeout(tagDuration);
    }
}
