package info.novatec.testit.livingdoc.confluence.velocity.doctheme;

import java.io.Serializable;


public class Settings implements Serializable {
    private static final long serialVersionUID = 1L;
    private String header;
    private String footer;
    private String navigation;
    private boolean treeEnabled = true;
    private boolean spaceSearchEnabled = false;

    public Settings() {
    }

    public Settings(String header, String footer) {
        this.header = header;
        this.footer = footer;
    }

    public Settings(String header, String footer, String navigation) {
        this.header = header;
        this.footer = footer;
        this.navigation = navigation;
    }

    public Settings(String header, String footer, String navigation, boolean treeEnabled) {
        this.header = header;
        this.footer = footer;
        this.navigation = navigation;
        this.treeEnabled = treeEnabled;
    }

    public Settings(String header, String footer, String navigation, boolean treeEnabled, boolean spaceSearchEnabled) {
        this.header = header;
        this.footer = footer;
        this.navigation = navigation;
        this.treeEnabled = treeEnabled;
        this.spaceSearchEnabled = spaceSearchEnabled;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public String getNavigation() {
        return navigation;
    }

    public void setNavigation(String navigation) {
        this.navigation = navigation;
    }

    public boolean isTreeEnabled() {
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
