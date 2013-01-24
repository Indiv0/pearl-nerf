package com.github.indiv0.pearlnerf;

import ashulman.mbapi.plugin.MbapiPlugin;

import com.github.indiv0.pearlnerf.util.PearlNerfConfigurationContext;

public class PearlNerf extends MbapiPlugin {
    @Override
    public void onEnable() {
        // Initializes the configurationContext.
        PearlNerfConfigurationContext configurationContext = new PearlNerfConfigurationContext(this);
        // Initializes the infoManager.

        // Registers the event handler and the command executor.
        registerEventHandler(new PearlNerfListener(configurationContext));

        super.onEnable();
    }
}
