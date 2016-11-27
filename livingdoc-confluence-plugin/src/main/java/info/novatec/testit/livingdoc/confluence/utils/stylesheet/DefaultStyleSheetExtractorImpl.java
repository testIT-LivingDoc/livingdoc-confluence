package info.novatec.testit.livingdoc.confluence.utils.stylesheet;

import com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;


/**
 * Stylesheet extractor for version from 2.8+
 */
public class DefaultStyleSheetExtractorImpl implements StyleSheetExtractor {

    private final SettingsManager settingsManager;
    private final BootstrapManager bootstrapManager;
    private final ConfluenceWebResourceManager webResourceManager;

    /**
     * Constructor for IoC
     */
    public DefaultStyleSheetExtractorImpl(BootstrapManager bootstrapManager, SettingsManager settingsManager,
        ConfluenceWebResourceManager webResourceManager) {
        this.settingsManager = settingsManager;
        this.bootstrapManager = bootstrapManager;
        this.webResourceManager = webResourceManager;
    }

    @Override
    public String renderStyleSheet(Space space) {
        String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
        String serverUrl = baseUrl.replace(bootstrapManager.getWebAppContextPath(), "");
        String spaceCssResources = webResourceManager.getEditorCssResources(space.getKey());
        String absolutSpaceCssResources = spaceCssResources.replaceAll("href=\"", "href=\"" + serverUrl);
        return absolutSpaceCssResources;
    }

}
