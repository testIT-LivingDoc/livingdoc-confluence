package info.novatec.testit.livingdoc.confluence.velocity.doctheme;

import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.themes.ThemeHelper;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.confluence.util.PlainTextToHtmlConverter;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.opensymphony.module.sitemesh.Page;


public class DocThemeHelper {
    private ThemeService themeService;
    private ThemeManager themeManager;
    static final String THEME_MODULE_KEY = "info.novatec.testit.livingdoc.confluence.velocity.doctheme:documentation";

    @HtmlSafe
    public String getThemeHeader(ThemeHelper helper) {
        Settings settings = themeService.retrieveThemeData(helper.getSpaceKey());
        if (StringUtils.isEmpty(settings.getHeader()))
            settings = themeService.retrieveThemeData(null);
        return helper.renderConfluenceMacro(settings.getHeader());
    }

    @HtmlSafe
    public String getThemeFooter(ThemeHelper helper) {
        String spaceKey = helper.getSpaceKey();
        Settings settings = themeService.retrieveThemeData(helper.getSpaceKey());
        if ( ! hasSpaceTheme(spaceKey) || StringUtils.isEmpty(settings.getFooter()))
            settings = themeService.retrieveThemeData(null);
        return helper.renderConfluenceMacro(settings.getFooter());
    }

    @HtmlSafe
    public String getThemeNavigation(ThemeHelper helper) {
        String spaceKey = helper.getSpaceKey();
        Settings settings = themeService.retrieveThemeData(helper.getSpaceKey());
        if ( ! hasSpaceTheme(spaceKey) || StringUtils.isEmpty(settings.getNavigation()))
            settings = themeService.retrieveThemeData(null);
        return helper.renderConfluenceMacro(settings.getNavigation());
    }

    public boolean isTreeEnabled(String spaceKey) {
        Settings settings = themeService.retrieveThemeData(spaceKey);
        if ( ! hasSpaceTheme(spaceKey) || settings == null)
            settings = themeService.retrieveThemeData(null);
        return settings.isTreeEnabled();
    }

    public boolean isSpaceSearchEnabled(String spaceKey) {
        Settings settings = themeService.retrieveThemeData(spaceKey);
        if ( ! hasSpaceTheme(spaceKey) || settings == null)
            settings = themeService.retrieveThemeData(null);
        return settings.isSpaceSearchEnabled();
    }

    @HtmlSafe
    public String addSpaceName(String separator, Page sitemeshPage) {
        String spaceName = sitemeshPage.getProperty("page.spacename");
        if (StringUtils.isBlank(spaceName))
            return "";

        // the XSS handling in Confluence for the page.spacename has moved
        // around.
        // It *should* already be html when it's coming out of a sitemesh
        // property,
        // but it's best to be safe and escape if the string appears to require
        // it.
        // (<,>,' and " cannot appear in something that has already been
        // escaped).
        String spaceNameHtml = StringUtils.containsAny(spaceName, "<>\"'") ? PlainTextToHtmlConverter.encodeHtmlEntities(
            spaceName) : spaceName;
        return separator + spaceNameHtml;
    }

    public void setThemeService(ThemeService themeService) {
        this.themeService = themeService;
    }

    private boolean hasSpaceTheme(String spaceKey) {
        return THEME_MODULE_KEY.equals(themeManager.getSpaceThemeKey(spaceKey));
    }

    public void setThemeManager(ThemeManager themeManager) {
        this.themeManager = themeManager;
    }

}
