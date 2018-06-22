package info.novatec.testit.livingdoc.confluence.velocity;

import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.*;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.login.LoginManager;
import com.atlassian.confluence.security.login.LoginResult;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUserManager;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.User;
import com.opensymphony.webwork.ServletActionContext;
import info.novatec.testit.livingdoc.confluence.actions.SpecificationAction;
import info.novatec.testit.livingdoc.report.XmlReport;
import info.novatec.testit.livingdoc.server.*;
import info.novatec.testit.livingdoc.server.domain.*;
import info.novatec.testit.livingdoc.util.I18nUtil;
import info.novatec.testit.livingdoc.util.Period;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


public class LivingDocConfluenceManager {
    public static final String EXECUTION_KEY = "livingdoc.executionKey";
    public static final String EXECUTE_CHILDREN = "livingdoc.executeChildren";
    public static final String IMPLEMENTED_VERSION = "livingdoc.implementedversion";
    public static final String PREVIOUS_IMPLEMENTED_VERSION = "livingdoc.previous.implementedversion";
    public static final String NEVER_IMPLEMENTED = "livingdoc.page.neverimplemented";
    public static final String SERVER_NOCONFIGURATION = "livingdoc.server.noconfiguration";
    public static final String ANONYMOUS_ACCESS_DENIED = "livingdoc.anonymous.accessdenied";
    public static final String USER_NOTMEMBEROF_LIVINGDOCUSERS_GROUP = "livingdoc.notmemberof.livingdocusers.group";
    public static final String REPOSITORY_BASEURL_OUTOFSYNC = "livingdoc.server.repourloutofsync";

    private static Logger log = LoggerFactory.getLogger(LivingDocConfluenceManager.class);
    private static final String RESOURCE_BUNDLE = SpecificationAction.class.getName();
    private static final int CRITICAL_PERIOD = 29;

    private final LivingDocPersistenceService livingDocPersistenceService;
    private final LoginManager loginManager;
    private final ConfluenceUserManager confluenceUserManager;
    private final TransactionTemplate transactionTemplate;
    private final SettingsManager settingsManager;
    private AtlassianBootstrapManager bootstrapManager;
    private final ContentPropertyManager contentPropertyManager;
    private final ContentPermissionManager contentPermissionManager;
    private final WikiStyleRenderer wikiStyleRenderer;
    private final PageManager pageManager;
    private final SpaceManager spaceManager;
    private final SpacePermissionManager spacePermissionManager;
    private final LabelManager labelManager;
    private final UserAccessor userAccessor;
    private final FormatSettingsManager formatSettingsManager;
    private final LocaleManager localeManager;
    private final Renderer viewRenderer;

    private final ThreadLocal<Locale> threadLocale = new ThreadLocal<Locale>();
    private ResourceBundle resourceBundle;

    /**
     * Constructor for IoC
     * 
     * Note: The qualifier for {@link PageManager} is needed because
     * there are multiple implementations.
     */
    public LivingDocConfluenceManager(LivingDocPersistenceService livingDocServerService, LoginManager loginManager,
                                      ConfluenceUserManager confluenceUserManager, TransactionTemplate transactionTemplate,
                                      SettingsManager settingsManager, AtlassianBootstrapManager bootstrapManager,
                                      ContentPropertyManager contentPropertyManager, ContentPermissionManager contentPermissionManager,
                                      WikiStyleRenderer wikiStyleRenderer, PageManager pageManager,
                                      SpaceManager spaceManager, SpacePermissionManager spacePermissionManager, LabelManager labelManager,
                                      UserAccessor userAccessor, FormatSettingsManager formatSettingsManager, LocaleManager localeManager,
                                      Renderer viewRenderer) {
        this.livingDocPersistenceService = livingDocServerService;
        this.loginManager = loginManager;
        this.confluenceUserManager = confluenceUserManager;
        this.transactionTemplate = transactionTemplate;
        this.settingsManager = settingsManager;
        this.bootstrapManager = bootstrapManager;
        this.contentPropertyManager = contentPropertyManager;
        this.contentPermissionManager = contentPermissionManager;
        this.wikiStyleRenderer = wikiStyleRenderer;
        this.pageManager = pageManager;
        this.spaceManager = spaceManager;
        this.spacePermissionManager = spacePermissionManager;
        this.labelManager = labelManager;
        this.userAccessor = userAccessor;
        this.formatSettingsManager = formatSettingsManager;
        this.localeManager = localeManager;
        this.viewRenderer = viewRenderer;
    }

    public String getVersion() {
        return LivingDocServer.VERSION;
    }

    /**
     * Custom I18n. Based on WebWork i18n.
     * 
     * @return the i18nzed message. If none found key is returned.
     */
    @HtmlSafe
    public String getText(String key) {
        return I18nUtil.getText(key, getResourceBundle());
    }

    @HtmlSafe
    public String getText(String key, Object... arguments) {
        return I18nUtil.getText(key, getResourceBundle(), arguments);
    }

    private ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            Locale locale = threadLocale.get();
            if (locale == null) {
                locale = new ConfluenceActionSupport().getLocale();
                threadLocale.set(locale == null ? Locale.ENGLISH : locale);
            }

            resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE, locale);
        }

        return resourceBundle;
    }

    /**
     * Retrieves the home repository of the confluence space.
     * <p></p>
     * 
     * @return the home repository of the confluence space.
     * @throws LivingDocServerException
     */
    public Repository getHomeRepository(String spaceKey) throws LivingDocServerException {
        String uid = getSettingsManager().getGlobalSettings().getSiteTitle() + "-" + spaceKey;
        Repository repository = Repository.newInstance(uid);
        return repository;
    }

    

    /**
     * Returns a message if an exception occurs.
     * <p></p>
     * 
     * @param spaceKey Space Key
     * @return a message if an exception occurs.
     * @throws LivingDocServerException
     */
    public String enable(String spaceKey) throws LivingDocServerException {
        try {
            Repository repository = getHomeRepository(spaceKey);
            getPersistenceService().getRegisteredRepository(repository);
            return null;
        } catch (LivingDocServerException e) {
            log.info(e.getMessage());
            return getText(e.getId());
        }
    }

    /**
     * Retrieves the specification
     * <p></p>
     * 
     * @param page
     * @return the specification.
     * @throws LivingDocServerException
     */
    public Specification getSpecification(Page page) throws LivingDocServerException {
        return getSpecification(page.getSpaceKey(), page.getTitle());
    }

    /**
     * Retrieves the specification
     * <p></p>
     * 
     * @param spaceKey Space Key
     * @param pageTitle String pageTitle
     * @return the specification.
     * @throws LivingDocServerException
     */
    public Specification getSpecification(String spaceKey, String pageTitle) throws LivingDocServerException {
        Specification specification = Specification.newInstance(pageTitle);
        specification.setRepository(getHomeRepository(spaceKey));
        return getPersistenceService().getSpecification(specification);
    }

    /**
     * True if the specification exists
     * <p></p>
     * 
     * @param page
     * @return if the specification exists
     */
    public boolean isExecutable(Page page) {
        try {
            return getSpecification(page) != null;
        } catch (LivingDocServerException e) {
            return false;
        }
    }

    /**
     * Get the repositories from the LivingDoc Server wich are requirements
     * dedicated
     * <p>
     * 
     * @param spaceKey Space Key
     * @return List of requirement repositories
     * @throws LivingDocServerException
     */
    public List<Repository> getRepositories(String spaceKey) throws LivingDocServerException {
        Repository repository = getHomeRepository(spaceKey);
        List<Repository> repositories = getPersistenceService().getRequirementRepositoriesOfAssociatedProject(repository
            .getUid());
        return repositories;
    }

    /**
     * Get the systems under test associated with the space specified
     * <p>
     * 
     * @param spaceKey Space Key
     * @return List of systems under test
     * @throws LivingDocServerException
     */
    public List<SystemUnderTest> getSystemsUnderTests(String spaceKey) throws LivingDocServerException {
        Repository repository = getHomeRepository(spaceKey);
        return getPersistenceService().getSystemUnderTestsOfAssociatedProject(repository.getUid());
    }

    /**
     * Get the systems under test list associated with the page specified
     * <p>
     * 
     * @param page Page
     * @return the systems under test list associated with the page specified
     * @throws LivingDocServerException
     */
    public Set<SystemUnderTest> getPageSystemsUnderTests(Page page) throws LivingDocServerException {
        return getPageSystemsUnderTests(page.getSpaceKey(), page.getTitle());
    }

    /**
     * Get the systems under test list associated with the page specified
     * <p>
     * 
     * @param spaceKey Space Key
     * @param pageTitle Page Title
     * @return the systems under test list associated with the page specified
     * @throws LivingDocServerException
     */
    public Set<SystemUnderTest> getPageSystemsUnderTests(String spaceKey, String pageTitle) throws LivingDocServerException {
        Specification specification = Specification.newInstance(pageTitle);
        specification.setRepository(getHomeRepository(spaceKey));
        return getPersistenceService().getSpecification(specification).getTargetedSystemUnderTests();
    }

    /**
     * Get the References with the specified page.
     * <p>
     * 
     * @param page Page
     * @return List of References
     * @throws LivingDocServerException
     */
    public List<Reference> getReferences(Page page) throws LivingDocServerException {
        return getReferences(page.getSpaceKey(), page.getTitle());
    }

    /**
     * Get the References with the specified page and space.
     * <p>
     * 
     * @param spaceKey Space Key
     * @param pageTitle Page Title
     * @return List of References
     * @throws LivingDocServerException
     */
    public List<Reference> getReferences(String spaceKey, String pageTitle) throws LivingDocServerException {
        Specification specification = Specification.newInstance(pageTitle);
        specification.setRepository(getHomeRepository(spaceKey));
        List<Reference> references = getPersistenceService().getSpecificationReferences(specification);
        return getUniqueReferences(references);
    }

    public String getPageContent(Page currentPage, Boolean implementedVersion) throws LivingDocServerException {
        AbstractPage page = currentPage;
        if (implementedVersion) {
            page = getImplementedPage(currentPage);
        }

        return page.getBodyAsString();
    }

    /**
     * Retrieves the body of a page in HTML rendering.
     * 
     * @param page
     * @return the body of a page in HTML rendering.
     */
    public String getPageContent(Page page) {
        try {
            return getPageContent(page, false);
        } catch (LivingDocServerException e) {
            return e.getMessage();
        }
    }

    /**
     * Retrieves from the page propeties the selectedSystemUnderTestInfo. If
     * none registered the default seeds execution will be saved and returned.
     * 
     * @param spaceKey
     * @param pageTitle
     * @return the selectedSystemUnderTestInfo.
     */
    public String getSelectedSystemUnderTestInfo(String spaceKey, String pageTitle) {
        return getSelectedSystemUnderTestInfo(pageManager.getPage(spaceKey, pageTitle));
    }

    /**
     * Retrieves from the page propeties the selectedSystemUnderTestInfo. If
     * none registered the default sut will be saved and returned. The key must
     * correspond to an excisting SystemUnderTest else the default one will be
     * saved and returned.
     * 
     * @param page
     * @return the selectedSystemUnderTestInfo.
     */
    public String getSelectedSystemUnderTestInfo(Page page) {
        SystemUnderTest selectedSut = getSavedSelectedSystemUnderTest(page);
        SystemUnderTest defaultSut = null;

        try {
            Set<SystemUnderTest> suts = getPageSystemsUnderTests(page.getSpaceKey(), page.getTitle());
            for (SystemUnderTest sut : suts) {
                if (selectedSut != null && selectedSut.equalsTo(sut)) {
                    // enougth said return the key now !
                    return buildSelectedSystemUnderTestInfo(selectedSut);
                }
                if (sut.isDefault()) {
                    defaultSut = sut;
                }
            }

            // else if no default pick first.
            if (defaultSut == null && ! suts.isEmpty()) {
                defaultSut = suts.iterator().next();
            }
        } catch (LivingDocServerException e) {
        }

        String key = buildSelectedSystemUnderTestInfo(defaultSut);
        saveSelectedSystemUnderTestInfo(page, key);
        return key;
    }

    public SystemUnderTest getSelectedSystemUnderTest(Page page) {
        return buildSelectedSystemUnderTest(getSelectedSystemUnderTestInfo(page));
    }

    /**
     * Saves the execution key into the page properties.
     * 
     * @param page
     * @param value
     */
    public void saveSelectedSystemUnderTestInfo(Page page, String value) {
        ContentEntityObject entityObject = getPageManager().getById(page.getId());
        getContentPropertyManager().setStringProperty(entityObject, EXECUTION_KEY, value);
    }

    /**
     * Sets the implemented version to the previous implemented version.
     * 
     * @param page
     */
    public void revertImplementation(Page page) {
        Integer previousImplementedVersion = getPreviousImplementedVersion(page);
        if (previousImplementedVersion != null) {
            saveImplementedVersion(page, previousImplementedVersion);
            savePreviousImplementedVersion(page, null);
        }
    }

    /**
     * Retrieves the previous implemented version of the specification.
     * 
     * @param page
     * @return the previous implemented version of the specification.
     */
    public Integer getPreviousImplementedVersion(Page page) {
        ContentEntityObject entityObject = getPageManager().getById(page.getId());
        String value = getContentPropertyManager().getStringProperty(entityObject, PREVIOUS_IMPLEMENTED_VERSION);
        return value == null ? null : Integer.valueOf(value);
    }

    /**
     * Saves the sprecified version as the Previous implemented version
     * 
     * @param page
     * @param version
     */
    public void savePreviousImplementedVersion(Page page, Integer version) {
        String value = version != null ? String.valueOf(version) : null;
        ContentEntityObject entityObject = getPageManager().getById(page.getId());
        getContentPropertyManager().setStringProperty(entityObject, PREVIOUS_IMPLEMENTED_VERSION, value);
    }

    /**
     * Verifies if the specification can be Implemented.
     * 
     * @param page
     * @return true if the specification can be Implemented.
     */
    public boolean canBeImplemented(Page page) {
        Integer implementedVersion = getImplementedVersion(page);
        return implementedVersion == null || page.getVersion() != implementedVersion;
    }

    /**
     * Retrieves the implemented version of the specification.
     * 
     * @param page
     * @return the implemented version of the specification.
     */
    public Integer getImplementedVersion(Page page) {
        ContentEntityObject entityObject = getPageManager().getById(page.getId());
        String value = getContentPropertyManager().getStringProperty(entityObject, IMPLEMENTED_VERSION);
        return value == null ? null : Integer.valueOf(value);
    }

    /**
     * Saves the sprecified version as the Iimplemented version
     * 
     * @param page
     * @param version
     */
    public void saveImplementedVersion(Page page, Integer version) {
        Integer previousImplementedVersion = getImplementedVersion(page);
        if (previousImplementedVersion != null && version != null && previousImplementedVersion == version)
            return;

        if (previousImplementedVersion != null)
            savePreviousImplementedVersion(page, previousImplementedVersion);

        String value = version != null ? String.valueOf(version) : null;
        ContentEntityObject entityObject = getPageManager().getById(page.getId());
        getContentPropertyManager().setStringProperty(entityObject, IMPLEMENTED_VERSION, value);
    }

    /**
     * Retrieves the content of the specification at the implemented version.
     * 
     * @param page
     * @return the content of the specification at the implemented version.
     * @throws LivingDocServerException
     */
    public AbstractPage getImplementedPage(Page page) throws LivingDocServerException {
        Integer version = getImplementedVersion(page);
        if (version == null)
            throw new LivingDocServerException(NEVER_IMPLEMENTED, "Never Implemented");

        return getPageManager().getPageByVersion(page, version);
    }

    /**
     * Verifies if the Specification has stayed to long in the WORKING state.
     * 
     * @param page
     * @return true if the Specification has stayed to long in the WORKING
     * state.
     */
    public boolean isImplementationDue(Page page) {
        int version = page.getVersion();

        Integer implementedVersion = getImplementedVersion(page);
        if (implementedVersion != null)
            version = page.getVersion() == implementedVersion ? implementedVersion : implementedVersion + 1;

        Date date = getPageManager().getPageByVersion(page, version).getLastModificationDate();
        Period period = Period.fromTo(date, new Date(System.currentTimeMillis()));
        return period.daysCount() > CRITICAL_PERIOD;
    }

    /**
     * Retrieves from the page propeties the Execute childs boolean. If none
     * registered false is returned.
     * 
     * @param page
     * @return the Execute childs boolean.
     */
    public boolean getExecuteChildren(Page page) {
        ContentEntityObject entityObject = getPageManager().getById(page.getId());
        String value = getContentPropertyManager().getStringProperty(entityObject, EXECUTE_CHILDREN);
        return value == null ? false : Boolean.valueOf(value);
    }

    public void saveExecuteChildren(Page page, Boolean doExecuteChildren) {
        ContentEntityObject entityObject = getPageManager().getById(page.getId());
        getContentPropertyManager().setStringProperty(entityObject, LivingDocConfluenceManager.EXECUTE_CHILDREN,
            doExecuteChildren != null ? String.valueOf(doExecuteChildren) : null);
    }

    /**
     * Verifies if the the selectedSystemUnderTestInfo matches the specified key
     * <p></p>
     * 
     * @param selectedSystemUnderTestInfo
     * @param key
     * @return true if the the selectedSystemUnderTestInfo matches the specified
     * key.
     */
    public boolean isSelected(String selectedSystemUnderTestInfo, String key) {
        return selectedSystemUnderTestInfo != null ? selectedSystemUnderTestInfo.equals(key) : false;
    }

    public String getBaseUrl() {
        // DEPRECATION WARNING we should use "Settings.getBaseUrl()" instead
        // Have to wait until we do not support version under confluence 2.3
        // return getBootstrapManager().getBaseUrl();
        return getSettingsManager().getGlobalSettings().getBaseUrl();
    }

    public String getEncoding() {
        return getSettingsManager().getGlobalSettings().getDefaultEncoding();
    }

    public String getPageUrl(Page page) {
        return getBaseUrl() + page.getUrlPath();
    }

    public boolean canEdit(Page page) {
        List<String> permTypes = new ArrayList<String>();
        permTypes.add(SpacePermission.CREATEEDIT_PAGE_PERMISSION);
        return getSpacePermissionManager().hasPermissionForSpace(getRemoteUser(), permTypes, page.getSpace());
    }

    public boolean canView(Page page) {
        return canView(page.getSpace());
    }

    public boolean canView(Space space) {
        List<String> permTypes = new ArrayList<String>();
        permTypes.add(SpacePermission.VIEWSPACE_PERMISSION);
        return getSpacePermissionManager().hasPermissionForSpace(getRemoteUser(), permTypes, space);
    }

    public String getHeader() {
        return "/doctheme/decorators/livingdoc-header.vm";
    }

    public String getBody() {
        return "/doctheme/decorators/livingdoc-body.vm";
    }

    public boolean isInSutList(SystemUnderTest sut, Collection<SystemUnderTest> sutList) {
        for (SystemUnderTest aSut : sutList) {
            if (aSut.equalsTo(sut)) {
                return true;
            }
        }

        return false;
    }

    public static String clean(String text) {
        if (text == null)
            return "";

        text = text.trim();
        text = text.replace("\"", "\\\"");
        text = text.replace("\'", "\\\'");
        text = text.replace("\n", "");
        text = text.replace("\r", "");
        return text;
    }

    public User getRemoteUser() {
        HttpServletRequest request = ServletActionContext.getRequest();

        if (request != null) {
            String remoteUserName = request.getRemoteUser();

            if (remoteUserName != null) {
                return getUserAccessor().getUserByName(remoteUserName);
            }
        }

        return AuthenticatedUserThreadLocal.get();
    }

    public String getPageProperty(String key, String identifier) {
        Space space = getSpaceManager().getSpace(identifier);
        if (space == null)
            return null;

        ContentEntityObject entityObject = getPageManager().getById(space.getHomePage().getId());
        return getContentPropertyManager().getStringProperty(entityObject, ServerPropertiesManager.SEQUENCE + key);
    }

    public void setPageProperty(String key, String value, String identifier) {
        Space space = getSpaceManager().getSpace(identifier);
        ContentEntityObject entityObject = getPageManager().getById(space.getHomePage().getId());
        getContentPropertyManager().setStringProperty(entityObject, ServerPropertiesManager.SEQUENCE + key, value);
    }

    public void verifyCredentials(String username, String password) throws LivingDocServerException {
        if (username != null && ! isCredentialsValid(username, password)) {
            throw new LivingDocServerException("livingdoc.confluence.badcredentials",
                "The username and password are incorrect.");
        }
    }

    public boolean isCredentialsValid(String username, String password) {
        return loginManager.authenticate(username, password) == LoginResult.OK ? true : false;
    }

    public DateFormatter getUserPreferencesDateFormatter() {
        return getCustomizedPreferencesDateFormatter(getFormatSettingsManager());
    }

    public DateFormatter getCustomizedPreferencesDateFormatter(FormatSettingsManager formatSettingsManager) {
        ConfluenceUserPreferences preferences = getUserAccessor().getConfluenceUserPreferences(getRemoteUser());

        return preferences.getDateFormatter(formatSettingsManager, getLocaleManager());
    }

    /*************************************************************************************************/

    /**************************
     * Access to container services
     *************************************/
    /*************************************************************************************************/

    public LivingDocPersistenceService getPersistenceService() {
        return livingDocPersistenceService;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public AtlassianBootstrapManager getBootstrapManager() {
        if (bootstrapManager != null) {
            return bootstrapManager;
        }
        bootstrapManager = BootstrapUtils.getBootstrapManager();
        return bootstrapManager;
    }

    public ContentPermissionManager getContentPermissionManager() {
        return contentPermissionManager;
    }

    public ContentPropertyManager getContentPropertyManager() {
        return contentPropertyManager;
    }

    

    public WikiStyleRenderer getWikiStyleRenderer() {
        return wikiStyleRenderer;
    }

    public Renderer getViewRenderer() {
        return viewRenderer;
    }

    public PageManager getPageManager() {
        return pageManager;
    }

    public SpaceManager getSpaceManager() {
        return spaceManager;
    }

    public SpacePermissionManager getSpacePermissionManager() {
        return spacePermissionManager;
    }

    public LabelManager getLabelManager() {
        return labelManager;
    }

    public LoginManager getLoginManager() {
        return loginManager;
    }

    public ConfluenceUserManager getConfluenceUserManager() {
        return confluenceUserManager;
    }

    public TransactionTemplate getTransactionTemplate() {
        return transactionTemplate;
    }

    public UserAccessor getUserAccessor() {
        return userAccessor;
    }

    public FormatSettingsManager getFormatSettingsManager() {
        return formatSettingsManager;
    }

    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    /*************************************************************************************************/

    private String buildSelectedSystemUnderTestInfo(SystemUnderTest sut) {
        return sut.getProject().getName() + "@" + sut.getName();
    }

    private SystemUnderTest getSavedSelectedSystemUnderTest(Page page) {
        ContentEntityObject entityObject = getPageManager().getById(page.getId());
        String key = getContentPropertyManager().getStringProperty(entityObject, EXECUTION_KEY);
        return buildSelectedSystemUnderTest(key);
    }

    private SystemUnderTest buildSelectedSystemUnderTest(String selectedSystemUnderTestInfo) {
        if (StringUtils.isBlank(selectedSystemUnderTestInfo)) {
            return null;
        }
        StringTokenizer stk = new StringTokenizer(selectedSystemUnderTestInfo, "@");
        Project project = Project.newInstance(stk.nextToken());
        SystemUnderTest sut = SystemUnderTest.newInstance(stk.nextToken());
        sut.setProject(project);
        return sut;
    }

    private List<Reference> getUniqueReferences(List<Reference> references) {
        Map<String, Reference> uniqueReferences = new HashMap<String, Reference>();

        for (Reference reference : references) {
            Reference ref = uniqueReferences.get(reference.getRequirement().getUUID());

            if (ref == null) {
                uniqueReferences.put(reference.getRequirement().getUUID(), reference);
            }
        }

        return new ArrayList<Reference>(uniqueReferences.values());
    }

    public void saveExecutionResult(Page page, String sut, XmlReport xmlReport) throws LivingDocServerException {
        Specification specification = getSpecification(page);

        List<SystemUnderTest> systemUnderTests = getSystemsUnderTests(page.getSpaceKey());

        SystemUnderTest systemUnderTest = null;

        for (SystemUnderTest s : systemUnderTests) {
            if (s.getName().equals(sut)) {
                systemUnderTest = s;
                break;
            }
        }

        if (systemUnderTest == null) {
            throw new LivingDocServerException(LivingDocServerErrorKey.SUT_NOT_FOUND, sut);
        }

        getPersistenceService().createExecution(systemUnderTest, specification, xmlReport);
    }

}
