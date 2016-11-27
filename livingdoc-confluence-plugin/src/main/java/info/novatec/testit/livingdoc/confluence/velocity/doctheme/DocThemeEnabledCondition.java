package info.novatec.testit.livingdoc.confluence.velocity.doctheme;

import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.themes.ThemeManager;


/**
 * Condition that reports true if the doc theme is enabled in the space provided
 * by the webInterfaceContext
 * 
 */
public class DocThemeEnabledCondition extends BaseConfluenceCondition {
    private ThemeManager themeManager;

    public DocThemeEnabledCondition(ThemeManager mgr) {
        themeManager = mgr;
    }

    @Override
    protected boolean shouldDisplay(WebInterfaceContext context) {
        Space space = context.getSpace();
        String spaceKey = space != null ? space.getKey() : null;
        if (spaceKey == null)
            return false;

        String themeKey = themeManager.getSpaceThemeKey(spaceKey);
        if (StringUtils.isBlank(themeKey)) {
            themeKey = themeManager.getGlobalThemeKey();

        }
        return DocThemeHelper.THEME_MODULE_KEY.equals(themeKey);
    }
}
