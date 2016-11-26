package info.novatec.testit.livingdoc.confluence;

import com.atlassian.user.GroupManager;

import info.novatec.testit.livingdoc.confluence.utils.osgi.BundleFileLocatorHelper;
import info.novatec.testit.livingdoc.confluence.utils.stylesheet.StyleSheetExtractor;
import info.novatec.testit.livingdoc.confluence.velocity.ConfluenceLivingDoc;


/**
 * Workaround to load components in classes which are not autowired.
 * 
 * https://developer.atlassian.com/display/DOCS/Access+components+statically
 */
public class StaticAccessor {

    private static ConfluenceLivingDoc confluenceLivingDoc;
    private static LivingDocServerConfigurationActivator livingDocServerConfigurationActivator;
    private static BundleFileLocatorHelper bundleFileLocatorHelper;
    private static GroupManager groupManager;
    private static StyleSheetExtractor styleSheetExtractor;

    public StaticAccessor(BundleFileLocatorHelper bundleFileLocatorHelper,
        LivingDocServerConfigurationActivator livingDocServerConfigurationActivator, ConfluenceLivingDoc confluenceLivingDoc,
        GroupManager groupManager, StyleSheetExtractor styleSheetExtractor) {
        StaticAccessor.livingDocServerConfigurationActivator = livingDocServerConfigurationActivator;
        StaticAccessor.bundleFileLocatorHelper = bundleFileLocatorHelper;
        StaticAccessor.confluenceLivingDoc = confluenceLivingDoc;
        StaticAccessor.styleSheetExtractor = styleSheetExtractor;
    }

    public static LivingDocServerConfigurationActivator getLivingDocServerConfigurationActivator() {
        return livingDocServerConfigurationActivator;
    }

    public static BundleFileLocatorHelper getBundleFileLocatorHelper() {
        return bundleFileLocatorHelper;
    }

    public static ConfluenceLivingDoc getConfluenceLivingDoc() {
        return confluenceLivingDoc;
    }

    public static GroupManager getGroupManager() {
        return groupManager;
    }

    public static StyleSheetExtractor getStyleSheetExtractor() {
        return styleSheetExtractor;
    }

}
