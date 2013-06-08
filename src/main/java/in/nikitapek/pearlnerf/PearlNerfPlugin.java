package in.nikitapek.pearlnerf;

import in.nikitapek.pearlnerf.events.PearlNerfListener;
import in.nikitapek.pearlnerf.util.PearlNerfConfigurationContext;

import com.amshulman.mbapi.MbapiPlugin;

public class PearlNerfPlugin extends MbapiPlugin {
    @Override
    public void onEnable() {
        PearlNerfConfigurationContext configurationContext = new PearlNerfConfigurationContext(this);

        registerEventHandler(new PearlNerfListener(configurationContext));

        super.onEnable();
    }
}
