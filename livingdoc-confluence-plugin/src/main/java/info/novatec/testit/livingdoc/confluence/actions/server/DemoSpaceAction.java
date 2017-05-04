/**
 * Copyright (c) 2008 Pyxis Technologies inc.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */
package info.novatec.testit.livingdoc.confluence.actions.server;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import info.novatec.testit.livingdoc.confluence.LivingDocServerConfigurationActivator;
import info.novatec.testit.livingdoc.confluence.velocity.LivingDocConfluenceManager;
import org.apache.commons.lang3.StringUtils;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.importexport.DefaultImportContext;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.ImportExportManager;
import com.atlassian.confluence.importexport.ImportedObjectPostProcessor;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;

import info.novatec.testit.livingdoc.confluence.demo.phonebook.PhoneBookSystemUnderDevelopment;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.Project;
import info.novatec.testit.livingdoc.server.domain.Repository;
import info.novatec.testit.livingdoc.server.domain.RepositoryType;
import info.novatec.testit.livingdoc.server.domain.Runner;
import info.novatec.testit.livingdoc.server.domain.Specification;
import info.novatec.testit.livingdoc.server.domain.SystemUnderTest;
import info.novatec.testit.livingdoc.server.domain.component.ContentType;
import info.novatec.testit.livingdoc.server.rpc.RpcServerService;
import info.novatec.testit.livingdoc.util.I18nUtil;


@SuppressWarnings("serial")
public class DemoSpaceAction extends LivingDocServerAction {

    private static final String DEMO_NAME = "LivingDoc Demo";
    private static final String DEMO_SUT_NAME = "Demo";
    private static final String DEMO_SPACE_KEY = "LIVINGDOCDEMO";
    private static final String PHONEBOOK_SUT_NAME = DEMO_SUT_NAME + " - PhoneBook";

    private static final String RESOURCE_BUNDLE = InstallationAction.class.getName();
    private final ThreadLocal<Locale> threadLocale = new ThreadLocal<Locale>();
    private ResourceBundle resourceBundle;

    private ImportExportManager importExportManager;

    private String username;
    private String pwd;

    public DemoSpaceAction(LivingDocConfluenceManager confluenceLivingDoc, LivingDocServerConfigurationActivator livingDocServerConfigurationActivator) {
        super(confluenceLivingDoc, livingDocServerConfigurationActivator);
    }

    public DemoSpaceAction(){}

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = StringUtils.trimToNull(pwd);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = StringUtils.trimToNull(username);
    }

    public String doGetDemo() {
        if ( ! getLivingDocConfluenceManager().isServerReady()) {
            addActionError(LivingDocConfluenceManager.SERVER_NOCONFIGURATION);
            return SUCCESS;
        }

        return SUCCESS;
    }

    public boolean isDemoSpaceExist() {
        try {
            return getDemoSpace() != null;
        } catch (Exception ex) {
            return false;
        }
    }

    public String doCreateDemoSpace() {
        try {
            if (getUsername() != null) {
                getLivingDocConfluenceManager().verifyCredentials(getUsername(), getPwd());
            }
            doAddDefaultRunner();

            doImportDemoSite();

            Space demoSpace = getDemoSpace();

            if (demoSpace == null) {
                throw new LivingDocServerException("livingdoc.demo.importfail", "Importing the demo site fail!");
            }

            Repository demoRepository = doRegisterSpace(demoSpace);

            doAddDemoSUT(demoRepository);
            doAddPhoneBookSUT(demoRepository);

            doEnableLivingDocPage(demoSpace, demoRepository);
        } catch (LivingDocServerException ex) {
            addActionError(ex.getId());
            doRemoveDemoSpace();
        } catch (Exception ex) {
            addActionError(ex.getMessage());
            doRemoveDemoSpace();
        }

        return SUCCESS;
    }

    private void doAddDefaultRunner() {
        try {
            getPersistenceService().createDefaultRunner(getLivingDocConfluenceManager().getLDServerConfiguration().getProperties());
        } catch (LivingDocServerException ex) {
            addActionError(ex.getId());
        }
    }

    private Repository doRegisterSpace(Space demoSpace) throws LivingDocServerException {
        Repository demoRepository = getDemoRepository();

        if (demoRepository != null)
            return demoRepository;

        Project demoProject = getDemoProject();

        String uid = getLivingDocConfluenceManager().getSettingsManager().getGlobalSettings().getSiteTitle() + "-" + demoSpace.getKey();

        demoRepository = Repository.newInstance(uid);

        demoRepository.setProject(demoProject);
        demoRepository.setType(RepositoryType.newInstance("CONFLUENCE"));
        demoRepository.setName(DEMO_NAME);
        demoRepository.setContentType(ContentType.TEST);
        demoRepository.setBaseUrl(getLivingDocConfluenceManager().getBaseUrl());
        demoRepository.setUsername(getUsername());
        demoRepository.setPassword(getPwd());

        demoRepository.setBaseRepositoryUrl(getDemoSpaceUrl());

        String baseTestUrl = String.format("%s/rpc/xmlrpc?handler=%s#%s", getLivingDocConfluenceManager().getBaseUrl(),
            RpcServerService.SERVICE_HANDLER, demoSpace.getKey());
        demoRepository.setBaseTestUrl(baseTestUrl);

        return getPersistenceService().registerRepository(demoRepository);
    }

    private void doAddDemoSUT(Repository demoRepository) throws LivingDocServerException {
        SystemUnderTest demoSut = getSUT(demoRepository, DEMO_SUT_NAME);

        if (demoSut == null) {
            demoSut = SystemUnderTest.newInstance(DEMO_SUT_NAME);

            demoSut.setRunner(getJavaRunner());
            demoSut.setProject(getDemoProject());

            getLivingDocConfluenceManager().getPersistenceService().createSystemUnderTest(demoSut, demoRepository);
        }
    }

    private void doAddPhoneBookSUT(Repository demoRepository) throws LivingDocServerException {
        SystemUnderTest phoneBookSut = getSUT(demoRepository, PHONEBOOK_SUT_NAME);

        if (phoneBookSut == null) {
            phoneBookSut = SystemUnderTest.newInstance(PHONEBOOK_SUT_NAME);

            phoneBookSut.setFixtureFactory(PhoneBookSystemUnderDevelopment.class.getName());
            phoneBookSut.setRunner(getJavaRunner());
            phoneBookSut.setProject(getDemoProject());

            getLivingDocConfluenceManager().getPersistenceService().createSystemUnderTest(phoneBookSut, demoRepository);
        }
    }

    private void doEnableLivingDocPage(Space demoSpace, Repository demoRepository) throws LivingDocServerException {
        List<Page> demoPages = getLivingDocConfluenceManager().getPageManager().getPages(demoSpace, true);

        for (Page demoPage : demoPages) {
            if (demoSpace.getHomePage().getId() != demoPage.getId() && ! demoPage.getTitle().endsWith(".java")) {
                doEnableLivingDocPage(demoRepository, demoPage);
            }
        }
    }

    private void doEnableLivingDocPage(Repository demoRepository, Page page) throws LivingDocServerException {
        Specification spec = Specification.newInstance(page.getTitle());
        spec.setRepository(demoRepository);

        spec = getLivingDocConfluenceManager().getPersistenceService().createSpecification(spec);

        if (page.getTitle().equals("PhoneBook")) {
            SystemUnderTest phoneBookSut = getSUT(demoRepository, PHONEBOOK_SUT_NAME);
            getLivingDocConfluenceManager().getPersistenceService().addSpecificationSystemUnderTest(phoneBookSut, spec);

            SystemUnderTest demoSut = getSUT(demoRepository, DEMO_SUT_NAME);
            getLivingDocConfluenceManager().getPersistenceService().removeSpecificationSystemUnderTest(demoSut, spec);
        }
    }

    

    private Space getDemoSpace() {
        return getLivingDocConfluenceManager().getSpaceManager().getSpace(DEMO_SPACE_KEY);
    }

    private SystemUnderTest getSUT(Repository demoRepository, String name) throws LivingDocServerException {
        List<SystemUnderTest> suts = getLivingDocConfluenceManager().getPersistenceService().getSystemUnderTestsOfAssociatedProject(demoRepository
            .getUid());

        for (SystemUnderTest sut : suts) {
            if (sut.getName().equals(name)) {
                return sut;
            }
        }

        return null;
    }

    private Repository getDemoRepository() throws LivingDocServerException {
        List<Repository> repositories = getLivingDocConfluenceManager().getPersistenceService().getAllSpecificationRepositories();

        for (Repository repository : repositories) {
            if (repository.getName().equals(DEMO_NAME)) {
                return repository;
            }
        }

        return null;
    }

    private Project getDemoProject() throws LivingDocServerException {
        List<Project> projects = getLivingDocConfluenceManager().getPersistenceService().getAllProjects();

        for (Project project : projects) {
            if (project.getName().equals(DEMO_NAME)) {
                return project;
            }
        }

        return Project.newInstance(DEMO_NAME);
    }

    private Runner getJavaRunner() throws LivingDocServerException {
        List<Runner> runners = getLivingDocConfluenceManager().getPersistenceService().getAllRunners();

        for (Runner runner : runners) {
            if (runner.getName().startsWith(getText("livingdoc.runners.demospace"))) {
                return runner;
            }
        }

        Runner runner = Runner.newInstance("Java");
        return runner;
    }

    private void doImportDemoSite() throws FileNotFoundException, ImportExportException {
        URL demoSiteZipUrl = DemoSpaceAction.class.getResource(
            "/info/novatec/testit/livingdoc/confluence/demo/demo-site.zip");

        if (demoSiteZipUrl == null) {
            throw new FileNotFoundException("Cannot find demo-site.zip");
        }

        DefaultImportContext ctx = new DefaultImportContext(demoSiteZipUrl, null);
        final Date importStart = new Date();

        ctx.setPostProcessor(new ImportedObjectPostProcessor() {
            @Override
            public boolean process(Object obj) {
                if (obj instanceof ConfluenceEntityObject) {
                    ConfluenceEntityObject entityObject = ( ConfluenceEntityObject ) obj;

                    // Make pages appear in recent updated in preference to
                    // non-pages.
                    if (entityObject instanceof Page) {
                        entityObject.setLastModificationDate(new Date());
                    } else {
                        entityObject.setLastModificationDate(importStart);
                    }

                    return true;
                }

                return false;
            }
        });

        getImportExportManager().doImport(ctx);
    }

    public String doRemoveDemoSpace() {
        try {
            Space demoSpace = getDemoSpace();

            if (demoSpace != null) {
                getLivingDocConfluenceManager().getSpaceManager().removeSpace(demoSpace);
            }

            getLivingDocConfluenceManager().getPersistenceService().removeProject(getDemoProject(), true);
        } catch (Exception ex) {
            addActionError(ex.getMessage());
        }

        return SUCCESS;
    }

    public String getDemoSpaceUrl() {
        Space demoSpace = getDemoSpace();

        return String.format("%s/display/%s", getLivingDocConfluenceManager().getBaseUrl(), demoSpace.getKey());
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

    public ImportExportManager getImportExportManager() {
        return importExportManager;
    }

    /**
     * Setter for IoC
     * 
     * @param importExportManager
     */
    public void setImportExportManager(ImportExportManager importExportManager) {
        this.importExportManager = importExportManager;
    }

    public boolean isAllowRemoteApiAnonymous() {
        return getLivingDocConfluenceManager().getSettingsManager().getGlobalSettings().isAllowRemoteApiAnonymous();
    }

    public String getGeneralConfigSecurityRemoteApiUrl() {
        return String.format("%s/admin/editgeneralconfig.action#security", getLivingDocConfluenceManager().getSettingsManager().getGlobalSettings()
            .getBaseUrl());
    }
}
