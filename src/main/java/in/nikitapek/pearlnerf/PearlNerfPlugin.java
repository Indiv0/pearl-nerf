package in.nikitapek.pearlnerf;

import com.amshulman.mbapi.MbapiPlugin;
import in.nikitapek.pearlnerf.events.PearlNerfListener;
import in.nikitapek.pearlnerf.util.PearlNerfConfigurationContext;

public class PearlNerfPlugin extends MbapiPlugin {
    @Override
    public void onEnable() {
        registerEventHandler(new PearlNerfListener(new PearlNerfConfigurationContext(this)));
        super.onEnable();
    }
}
