package com.github.indiv0.pearlnerf;

import com.amshulman.mbapi.MbapiPlugin;
import com.github.indiv0.pearlnerf.events.PearlNerfListener;
import com.github.indiv0.pearlnerf.util.PearlNerfConfigurationContext;

public class PearlNerfPlugin extends MbapiPlugin {
    @Override
    public void onEnable() {
        // Initializes the configurationContext.
        PearlNerfConfigurationContext configurationContext = new PearlNerfConfigurationContext(this);

        // Registers the event handler and the command executor.
        registerEventHandler(new PearlNerfListener(configurationContext));

        super.onEnable();
    }
}
