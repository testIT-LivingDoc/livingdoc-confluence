package info.novatec.testit.livingdoc.confluence.velocity.doctheme;

import org.apache.commons.lang.StringUtils;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;


public class DefaultThemeService implements ThemeService {
    private BandanaManager bandanaManager;
    private static final String PLUGIN_KEY = "info.novatec.testit.livingdoc.confluence.velocity.doctheme.doctheme";

    @Override
    public void storeThemeData(String spaceKey, Settings settings) {
        if (StringUtils.isBlank(spaceKey))
            bandanaManager.setValue(new ConfluenceBandanaContext(), PLUGIN_KEY, settings);
        else
            bandanaManager.setValue(new ConfluenceBandanaContext(spaceKey), PLUGIN_KEY, settings);
    }

    @Override
    public Settings retrieveThemeData(String spaceKey) {
        Settings settings = null;
        if (StringUtils.isBlank(spaceKey))
            settings = ( Settings ) bandanaManager.getValue(new ConfluenceBandanaContext(), PLUGIN_KEY, false);
        else
            settings = ( Settings ) bandanaManager.getValue(new ConfluenceBandanaContext(spaceKey), PLUGIN_KEY, false);

        if (settings == null) {
            settings = new Settings();
        }
        return settings;
    }

    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }
}
