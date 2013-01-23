package com.github.indiv0.pearlnerf;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.indiv0.bukkitutils.UtilManager;
import com.trc202.CombatTag.CombatTag;
import com.trc202.CombatTagApi.CombatTagApi;

public class PearlNerf extends JavaPlugin {
    private final UtilManager utilManager = new UtilManager();
    public CombatTagApi combatApi = null;

    @Override
    public void onLoad() {
        // Initialize all utilities.
        utilManager.initialize(this);
    }

    @Override
    public void onEnable() {
        utilManager.getListenerUtil().registerListener(new PearlNerfListener(this));

        if (getServer().getPluginManager().getPlugin("CombatTag") != null) {
            combatApi = new CombatTagApi((CombatTag) getServer().getPluginManager().getPlugin("CombatTag"));
        }
    }
}
