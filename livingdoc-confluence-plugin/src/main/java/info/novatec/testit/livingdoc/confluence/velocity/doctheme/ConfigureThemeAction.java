package info.novatec.testit.livingdoc.confluence.velocity.doctheme;

import com.atlassian.confluence.core.FormAware;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;


public class ConfigureThemeAction extends AbstractSpaceAction implements FormAware {
    private static final long serialVersionUID = 1L;
    private String headerText;
    private String footerText;
    private String navigationText;
    private boolean treeEnabled;
    private boolean spaceSearchEnabled;
    private ThemeService themeService;

    @Override
    public String doDefault() throws Exception {
        Settings settings = themeService.retrieveThemeData(getSpaceKey());
        this.headerText = settings.getHeader();
        this.footerText = settings.getFooter();
        this.navigationText = settings.getNavigation();
        this.treeEnabled = settings.isTreeEnabled();
        this.spaceSearchEnabled = settings.isSpaceSearchEnabled();
        return INPUT;
    }

    @Override
    public boolean isPermitted() {
        Object target = ( getSpace() != null ) ? space : PermissionManager.TARGET_APPLICATION;
        return permissionManager.hasPermission(getAuthenticatedUser(), Permission.ADMINISTER, target);
    }

    @Override
    public String execute() throws Exception {
        Settings settings = new Settings(headerText, footerText, navigationText, treeEnabled, spaceSearchEnabled);
        themeService.storeThemeData(getSpaceKey(), settings);
        return super.execute();
    }

    @Override
    public boolean isEditMode() {
        return true;
    }

    public void setThemeService(ThemeService themeService) {
        this.themeService = themeService;
    }

    public String getFooterText() {
        return footerText;
    }

    public void setFooterText(String footerText) {
        this.footerText = footerText;
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public String getNavigationText() {
        return navigationText;
    }

    public void setNavigationText(String navigationText) {
        this.navigationText = navigationText;
    }

    public boolean getTreeEnabled() {
        return treeEnabled;
    }

    public void setTreeEnabled(boolean treeEnabled) {
        this.treeEnabled = treeEnabled;
    }

    public boolean isSpaceSearchEnabled() {
        return spaceSearchEnabled;
    }

    public void setSpaceSearchEnabled(boolean spaceSearchEnabled) {
        this.spaceSearchEnabled = spaceSearchEnabled;
    }
}
