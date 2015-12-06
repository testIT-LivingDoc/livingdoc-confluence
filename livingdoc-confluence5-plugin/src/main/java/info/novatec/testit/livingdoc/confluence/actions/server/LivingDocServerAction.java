package info.novatec.testit.livingdoc.confluence.actions.server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;

import info.novatec.testit.livingdoc.confluence.velocity.ConfluenceLivingDoc;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.LivingDocServerService;
import info.novatec.testit.livingdoc.server.ServerPropertiesManager;
import info.novatec.testit.livingdoc.server.domain.Project;
import info.novatec.testit.livingdoc.server.domain.Repository;
import info.novatec.testit.livingdoc.server.domain.SystemUnderTest;
import info.novatec.testit.livingdoc.server.rpc.RpcServerService;
import info.novatec.testit.livingdoc.util.I18nUtil;


@SuppressWarnings("serial")
public class LivingDocServerAction extends AbstractSpaceAction {
    private static final String RESOURCE_BUNDLE = ConfigurationAction.class.getName();
    private final ThreadLocal<Locale> threadLocale = new ThreadLocal<Locale>();
    private ResourceBundle resourceBundle;

    protected ConfluenceLivingDoc ldUtil;
    private List<SystemUnderTest> systemUnderTests;

    protected String projectName;
    protected Repository registeredRepository;
    protected Repository homeRepository;
    private String spaceKey;

    private String url;
    private String handler = RpcServerService.SERVICE_HANDLER;
    private Boolean isRegistered;
    protected LinkedList<Project> projects;

    /**
     * Setter for IoC
     * 
     * @param confluenceLivingDoc
     */
    public void setConfluenceLivingDoc(ConfluenceLivingDoc confluenceLivingDoc) {
        this.ldUtil = confluenceLivingDoc;
    }

    protected LivingDocServerService getService() {
        return ldUtil.getLDServerService();
    }

    public boolean isServerSetupComplete() {
        return ldUtil.isServerSetupComplete();
    }

    public boolean isServerReady() {
        return ldUtil.isServerReady();
    }

    public boolean getIsServerReady() {
        return isServerReady();
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName.trim();
    }

    public String getUrl() {
        if (url != null)
            return url;
        url = getNotNullProperty(ServerPropertiesManager.URL);
        return url;
    }

    public void setUrl(String url) {
        this.url = url.trim();
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler.trim();
    }

    public String getIdentifier() {
        return key;
    }

    public Repository getRegisteredRepository() throws LivingDocServerException {
        if (registeredRepository != null)
            return registeredRepository;
        registeredRepository = getService().getRegisteredRepository(getHomeRepository());
        return registeredRepository;
    }

    public void setRegisteredRepository(Repository registeredRepository) {
        this.registeredRepository = registeredRepository;
    }

    public Repository getHomeRepository() throws LivingDocServerException {
        if (homeRepository != null)
            return homeRepository;
        if (key == null) {
            homeRepository = Repository.newInstance("UNKNOWN_UID");
        } else {
            homeRepository = ldUtil.getHomeRepository(key);
        }

        return homeRepository;
    }

    public List<SystemUnderTest> getSystemUnderTests() {
        if ( ! isServerSetupComplete())
            return new ArrayList<SystemUnderTest>();

        try {
            if (projectName == null)
                if (systemUnderTests != null)
                    return systemUnderTests;
            systemUnderTests = ldUtil.getLDServerService().getSystemUnderTestsOfProject(projectName);
        } catch (LivingDocServerException e) {
            addActionError(e.getId());
        }

        return systemUnderTests;
    }

    public boolean isRegistered() {
        if (isRegistered != null)
            return isRegistered;
        if ( ! isServerReady())
            return false;

        try {
            getRegisteredRepository();
            isRegistered = true;
        } catch (LivingDocServerException e) {
            isRegistered = false;
        }

        return isRegistered;
    }

    /**
     * @deprecated use {@link #isServerReady}
     */
    @Deprecated
    public boolean getCanConnect() {
        return isServerReady();
    }

    public boolean isWithNewProject() {
        return projectName != null && projectName.equals(projectCreateOption());
    }

    public String getUID(SystemUnderTest sut) {
        return sut.getName().replaceAll(" ", "_");
    }

    protected String projectCreateOption() {
        return getText("livingdoc.registration.newproject");
    }

    private String getNotNullProperty(String key) {
        String value = ldUtil.getPageProperty(key, getIdentifier());
        return value == null ? "" : value;
    }

    @Override
    public String getSpaceKey() {
        if (spaceKey == null)
            spaceKey = key;
        return spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        key = spaceKey;
        this.spaceKey = spaceKey;
    }

    /**
     * Custom I18n. Based on WebWork i18n.
     * 
     * @param key Key
     * @return the i18nzed message. If none found key is returned.
     */
    @Override
    @HtmlSafe
    public String getText(String key) {
        String text = super.getText(key);

        if (text.equals(key)) {
            text = I18nUtil.getText(key, getResourceBundle());
        }

        return text;
    }

    @Override
    @HtmlSafe
    public String getText(String key, Object[] args) {
        String text = super.getText(key, args);

        if (text.equals(key)) {
            text = I18nUtil.getText(key, getResourceBundle(), args);
        }

        return text;
    }

    private ResourceBundle getResourceBundle() {

        if (resourceBundle == null) {
            Locale locale = threadLocale.get();
            if (locale == null) {
                locale = getLocale();
                threadLocale.set(locale == null ? Locale.ENGLISH : locale);
            }

            resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE, locale);
        }

        return resourceBundle;
    }

    public LinkedList<Project> getProjects() {
        if (projects != null)
            return projects;
        try {
            projects = new LinkedList<Project>(getService().getAllProjects());
            projectName = projectName == null ? projects.iterator().next().getName() : projectName;

            return projects;
        } catch (LivingDocServerException e) {
            addActionError(e.getId());
        }

        return projects;
    }
}
