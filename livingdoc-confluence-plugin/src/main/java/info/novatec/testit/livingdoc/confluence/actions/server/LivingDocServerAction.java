package info.novatec.testit.livingdoc.confluence.actions.server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import info.novatec.testit.livingdoc.confluence.LivingDocServerConfiguration;
import info.novatec.testit.livingdoc.confluence.LivingDocServerConfigurationActivator;
import info.novatec.testit.livingdoc.confluence.velocity.LivingDocConfluenceManager;
import info.novatec.testit.livingdoc.server.LivingDocPersistenceService;
import info.novatec.testit.livingdoc.server.rpc.RpcServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;

import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.ServerPropertiesManager;
import info.novatec.testit.livingdoc.server.domain.Project;
import info.novatec.testit.livingdoc.server.domain.Repository;
import info.novatec.testit.livingdoc.server.domain.SystemUnderTest;
import info.novatec.testit.livingdoc.util.I18nUtil;


@SuppressWarnings("serial")
public class LivingDocServerAction extends AbstractSpaceAction {
    private static final Logger log = LoggerFactory.getLogger(LivingDocServerAction.class);

    private static final String RESOURCE_BUNDLE = ConfigurationAction.class.getName();
    private final ThreadLocal<Locale> threadLocale = new ThreadLocal<Locale>();
    private LivingDocConfluenceManager livingDocConfluenceManager;
    private LivingDocServerConfigurationActivator livingDocServerConfigurationActivator;
    protected String projectName;
    protected Repository registeredRepository;
    protected Repository homeRepository;
    protected LinkedList<Project> projects;
    private ResourceBundle resourceBundle;
    private List<SystemUnderTest> systemUnderTests;
    private String spaceKey;
    private String url;
    private String handler = RpcServerService.SERVICE_HANDLER;
    private Boolean isRegistered;

    public LivingDocServerAction(LivingDocConfluenceManager confluenceLivingDoc,
                                 LivingDocServerConfigurationActivator livingDocServerConfigurationActivator) {
        this.livingDocConfluenceManager = confluenceLivingDoc;
        this.livingDocServerConfigurationActivator = livingDocServerConfigurationActivator;
    }

    public LivingDocServerAction() {
    }

    /**
     * Setter for IoC
     *
     * @param livingDocConfluenceManager
     */
    public void setLivingDocConfluenceManager(LivingDocConfluenceManager livingDocConfluenceManager) {
        this.livingDocConfluenceManager = livingDocConfluenceManager;
    }

    public void setLivingDocServerConfigurationActivator(LivingDocServerConfigurationActivator livingDocServerConfigurationActivator) {
        this.livingDocServerConfigurationActivator = livingDocServerConfigurationActivator;
    }

    protected LivingDocConfluenceManager getLivingDocConfluenceManager() {
        return livingDocConfluenceManager;
    }

    protected LivingDocPersistenceService getPersistenceService() {
        return livingDocConfluenceManager.getPersistenceService();
    }

    protected LivingDocServerConfigurationActivator getLivingDocServerConfigurationActivator() {
        return livingDocServerConfigurationActivator;
    }

    public LivingDocServerConfiguration getLDServerConfiguration() {
        return getLivingDocServerConfigurationActivator().getConfiguration();
    }

    public boolean isServerSetupComplete() {
        if (getLivingDocServerConfigurationActivator() == null) {
            return false;
        }

        return getLivingDocServerConfigurationActivator().isServerSetupComplete();
    }

    public boolean isServerReady() {
        if (getLivingDocServerConfigurationActivator() == null) {
            return false;
        }

        return getLivingDocServerConfigurationActivator().isReady();
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
        if (url != null) {
            return url;
        }
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
        if (registeredRepository != null) {
            return registeredRepository;
        }
        registeredRepository = getPersistenceService().getRegisteredRepository(getHomeRepository());
        return registeredRepository;
    }

    public void setRegisteredRepository(Repository registeredRepository) {
        this.registeredRepository = registeredRepository;
    }

    public Repository getHomeRepository() throws LivingDocServerException {
        if (homeRepository != null) {
            return homeRepository;
        }
        if (key == null) {
            homeRepository = Repository.newInstance("UNKNOWN_UID");
        } else {
            homeRepository = livingDocConfluenceManager.getHomeRepository(key);
        }

        return homeRepository;
    }

    public List<SystemUnderTest> getSystemUnderTests() {
        if (!isServerSetupComplete()) {
            return new ArrayList<SystemUnderTest>();
        }

        try {
            if (projectName == null) {
                if (systemUnderTests != null) {
                    return systemUnderTests;
                }
            }
            systemUnderTests = getPersistenceService().getSystemUnderTestsOfProject(projectName);
        } catch (LivingDocServerException e) {
            addActionError(e);
        }

        return systemUnderTests;
    }

    public boolean isRegistered() {
        if (isRegistered != null) {
            return isRegistered;
        }
        if (!isServerReady()) {
            return false;
        }

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

    private String getNotNullProperty(String propertyKey) {
        String value = livingDocConfluenceManager.getPageProperty(propertyKey, getIdentifier());
        return value == null ? "" : value;
    }

    @Override
    public String getSpaceKey() {
        if (spaceKey == null) {
            spaceKey = key;
        }
        return spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        key = spaceKey;
        this.spaceKey = spaceKey;
    }

    /**
     * Custom I18n. Based on WebWork i18n.
     *
     * @param propertyKey Key
     * @return the i18nzed message. If none found key is returned.
     */
    @Override
    @HtmlSafe
    public String getText(String propertyKey) {
        String text = super.getText(propertyKey);

        if (text.equals(propertyKey)) {
            text = I18nUtil.getText(propertyKey, getResourceBundle());
        }

        return text;
    }

    @Override
    @HtmlSafe
    public String getText(String propertyKey, Object[] args) {
        String text = super.getText(propertyKey, args);

        if (text.equals(propertyKey)) {
            text = I18nUtil.getText(propertyKey, getResourceBundle(), args);
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
        if (projects != null) {
            return projects;
        }
        try {
            projects = new LinkedList<Project>(getPersistenceService().getAllProjects());
            projectName = projectName == null ? projects.iterator().next().getName() : projectName;

            return projects;
        } catch (LivingDocServerException e) {
            addActionError(e);
        }

        return projects;
    }

    public void addActionError(LivingDocServerException ldse) {
        super.addActionError(ldse.getId());
        if (ldse.getCause() != null) {
            log.error("Error in action", ldse.getCause());
        }
    }


}
