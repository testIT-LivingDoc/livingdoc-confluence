/* Copyright (c) 2008 Pyxis Technologies inc.
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
 * http://www.fsf.org. */
package info.novatec.testit.livingdoc.confluence;

import java.io.File;
import java.util.Properties;

import org.hibernate.dialect.HSQLDialect;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.event.events.plugin.PluginDisableEvent;
import com.atlassian.confluence.event.events.plugin.PluginEnableEvent;
import com.atlassian.confluence.event.events.plugin.PluginFrameworkStartedEvent;
import com.atlassian.confluence.event.events.plugin.PluginInstallEvent;
import com.atlassian.confluence.event.events.plugin.PluginUninstallEvent;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.google.gson.Gson;

import info.novatec.testit.livingdoc.confluence.utils.osgi.BundleFileLocatorHelper;
import info.novatec.testit.livingdoc.server.LivingDocPersistenceService;
import info.novatec.testit.livingdoc.server.LivingDocServer;
import info.novatec.testit.livingdoc.server.LivingDocServerErrorKey;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.configuration.DefaultServerProperties;
import info.novatec.testit.livingdoc.server.database.SessionService;
import info.novatec.testit.livingdoc.server.database.hibernate.BootstrapData;
import info.novatec.testit.livingdoc.server.database.hibernate.HibernateSessionService;
import info.novatec.testit.livingdoc.server.domain.dao.DocumentDao;
import info.novatec.testit.livingdoc.server.domain.dao.ProjectDao;
import info.novatec.testit.livingdoc.server.domain.dao.RepositoryDao;
import info.novatec.testit.livingdoc.server.domain.dao.SystemUnderTestDao;
import info.novatec.testit.livingdoc.server.domain.dao.hibernate.HibernateDocumentDao;
import info.novatec.testit.livingdoc.server.domain.dao.hibernate.HibernateProjectDao;
import info.novatec.testit.livingdoc.server.domain.dao.hibernate.HibernateRepositoryDao;
import info.novatec.testit.livingdoc.server.domain.dao.hibernate.HibernateSystemUnderTestDao;
import info.novatec.testit.livingdoc.server.rpc.xmlrpc.LivingDocXmlRpcServer;


/**
 * This component is responsible to bootstrap the LivingDoc database
 * and manage it's state and configuration data.
 * <p>
 * Note: The {@link BandanaManager} has some problems storing classes
 * (ClassCastException during a re installation), so you should store objects
 * only as string (e.g. using Gson).
 */
public class LivingDocServerConfigurationActivator implements InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(LivingDocServerConfigurationActivator.class);
    private static final String EXECUTION_TIMEOUT_DEFAULT = "60";

    private final BandanaContext bandanaContext = new ConfluenceBandanaContext("_LIVINGDOC");

    private LivingDocServerConfiguration configuration;
    private HibernateSessionService hibernateSessionService;

    private boolean isPluginEnabled = false;

    /**
     * If you want to check if the database has been created before use the
     * {@code LivingDocServerConfiguration#isSetupComplete()} method of
     * {@link #getConfiguration()}
     */
    private boolean isDatabaseInitialized = false;

    private final BandanaManager bandanaManager;
    private final BootstrapManager bootstrapManager;
    private final LivingDocPersistenceService livingDocPersistenceService;
    private final LivingDocXmlRpcServer xmlRpcServer;
    private final EventPublisher eventPublisher;
    private final BundleFileLocatorHelper bundleFileLocatorHelper;
    private Gson gson;

    public LivingDocServerConfigurationActivator(BootstrapManager bootstrapManager, BandanaManager bandanaManager,
        LivingDocPersistenceService livingDocPersistenceService, LivingDocXmlRpcServer xmlRpcServer,
        EventPublisher eventPublisher, BundleFileLocatorHelper bundleFileLocatorHelper) {
        this.bootstrapManager = bootstrapManager;
        this.bandanaManager = bandanaManager;
        this.livingDocPersistenceService = livingDocPersistenceService;
        this.xmlRpcServer = xmlRpcServer;
        this.eventPublisher = eventPublisher;
        this.bundleFileLocatorHelper = bundleFileLocatorHelper;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.gson = new Gson();
        this.eventPublisher.register(this);
    }

    public boolean isReady() {
        return isPluginEnabled && isDatabaseInitialized;
    }

    /**
     * Note that you should check if the configuration setup was already
     * completed before using this method.
     */
    public void setupDatabaseFromStoredConfiguration() throws LivingDocServerException {
        LivingDocServerConfiguration storedConfiguration = getConfiguration();
        setupDatabaseFromConfiguration(storedConfiguration);
    }

    private void setupDatabaseFromConfiguration(LivingDocServerConfiguration customConfiguration)
        throws LivingDocServerException {
        if (!isPluginEnabled) {
            return;
        }

        try {
            isDatabaseInitialized = false;

            closeSession();

            Properties properties = customConfiguration.getProperties();

            properties.setProperty("confluence.home", getConfluenceHome());
            if (properties.getProperty("executionTimeout") == null) {
                properties.setProperty("executionTimeout", EXECUTION_TIMEOUT_DEFAULT);
            }

            HibernateSessionService sessionService = new HibernateSessionService(properties);

            // Do we really need to bootstrap the data again and again?
            initializeBootstrapData(sessionService, properties);

            initializeXmlRpcServerFromSession(sessionService);

            hibernateSessionService = sessionService;

            customConfiguration.setSetupComplete(true);
            // Override the current configuration.
            this.configuration = customConfiguration;
            storeConfiguration(customConfiguration);

            isDatabaseInitialized = true;
        } catch (Exception ex) {
            log.error("Failed to initialize LivingDoc database.", ex);
            throw new LivingDocServerException(LivingDocServerErrorKey.GENERAL_ERROR, ex);
        }
    }

    private void initializeBootstrapData(SessionService customSessionService, Properties properties) throws Exception {
        // Since we're in a OSGI context, our plugin isn't stored in the
        // WEB-INF folder any more. Therefore we had to implement a
        // new way to detect the livingdoc jar (which is needed as
        // classpath element for the demo runner).
        String currentBundleFilePath = getLivingDocBundleFilePath();

        log.debug("Boostrapping datas");
        properties.setProperty("livingdoc.path", currentBundleFilePath);
        new BootstrapData(customSessionService, properties).execute();
    }

    private void initializeXmlRpcServerFromSession(HibernateSessionService customSessionService) {
        ProjectDao projectDao = new HibernateProjectDao(customSessionService);
        RepositoryDao repositoryDao = new HibernateRepositoryDao(customSessionService);
        SystemUnderTestDao sutDao = new HibernateSystemUnderTestDao(customSessionService);
        DocumentDao documentDao = new HibernateDocumentDao(customSessionService);

        livingDocPersistenceService.setDocumentDao(documentDao);
        livingDocPersistenceService.setProjectDao(projectDao);
        livingDocPersistenceService.setRepositoryDao(repositoryDao);
        livingDocPersistenceService.setSessionService(customSessionService);
        livingDocPersistenceService.setSystemUnderTestDao(sutDao);
        livingDocPersistenceService.setSessionService(customSessionService);

        xmlRpcServer.setService(livingDocPersistenceService);
    }

    private String getLivingDocBundleFilePath() throws Exception {
        Bundle bundle = FrameworkUtil.getBundle(getClass());
        File location = bundleFileLocatorHelper.getBundleInstallLocation(bundle);
        return location.getAbsolutePath();
    }

    private void closeSession() {
        if (hibernateSessionService != null) {
            hibernateSessionService.close();
        }
        hibernateSessionService = null;
        isDatabaseInitialized = false;
    }

    public LivingDocServerConfiguration getConfiguration() {
        if (configuration == null) {
            configuration = getConfigurationFromBandana();
        }

        return configuration;
    }

    public void storeConfiguration(LivingDocServerConfiguration configuration) {
        // @todo : sanity check over the previous configuration
        storeConfigurationToBandana(configuration);
    }

    private LivingDocServerConfiguration getConfigurationFromBandana() {
        LivingDocServerConfiguration configuration = getValue(LivingDocServerConfiguration.class);

        if (configuration == null) {
            configuration = new LivingDocServerConfiguration();
            storeConfigurationToBandana(configuration);
        }

        return configuration;
    }

    private void storeConfigurationToBandana(LivingDocServerConfiguration configuration) {
        setValue(LivingDocServerConfiguration.class, configuration);
    }

    private <T> T getValue(Class<T> classKey) {
        String storedJsonObject = ( String ) bandanaManager.getValue(bandanaContext, classKey.getName());
        return gson.fromJson(storedJsonObject, classKey);
    }

    private void setValue(Class<?> classKey, Object value) {
        String jsobObject = gson.toJson(value);
        bandanaManager.setValue(bandanaContext, classKey.getName(), jsobObject);
    }

    private void removeValue(Class<?> classKey) {
        bandanaManager.removeValue(bandanaContext, classKey.getName());
    }

    public String getConfigJnriUrl() {
        return ( String ) getConfiguration().getProperties().get("config$hibernate.connection.datasource");
    }

    public String getConfigDialect() {
        return ( String ) getConfiguration().getProperties().get("config$hibernate.dialect");
    }

    public void initQuickInstallConfiguration() throws LivingDocServerException {
        LivingDocServerConfiguration customConfiguration = getConfiguration();

        Properties properties = new DefaultServerProperties();

        properties.remove("hibernate.connection.datasource"); // direct jdbc
        properties.put("hibernate.connection.driver_class", "org.hsqldb.jdbc.JDBCDriver");
        properties.put("hibernate.connection.url", "jdbc:hsqldb:file:" + getConfluenceHome() + "/database/ldsdb");
        properties.put("hibernate.connection.username", "sa");
        properties.put("hibernate.connection.password", "");
        properties.put("hibernate.dialect", HSQLDialect.class.getName());

        configuration.setProperties(properties);

        setupDatabaseFromConfiguration(customConfiguration);
    }

    public void initCustomInstallConfiguration(String hibernateDialect, String jndiUrl) throws LivingDocServerException {
        LivingDocServerConfiguration customConfiguration = getConfiguration();

        Properties properties = new DefaultServerProperties();

        properties.put("hibernate.connection.datasource", jndiUrl);
        properties.put("config$hibernate.connection.datasource", jndiUrl);
        properties.put("hibernate.dialect", hibernateDialect);
        properties.put("config$hibernate.dialect", hibernateDialect);

        if (hibernateDialect.contains("Oracle")) {
            // The Oracle JDBC driver doesn't like prepared statement caching
            // very much.
            properties.put("hibernate.statement_cache.size", "0");
            // or baching with BLOBs very much.
            properties.put("hibernate.jdbc.batch_size", "0");
            // http://www.jroller.com/dashorst/entry/hibernate_3_1_something_performance1
            properties.put("hibernate.jdbc.wrap_result_sets", "true");
        }

        configuration.setProperties(properties);

        setupDatabaseFromConfiguration(customConfiguration);
    }

    private String getConfluenceHome() {
        return bootstrapManager.getSharedHome().getAbsolutePath();
    }

    @EventListener
    public void pluginFrameworkStartedEvent(PluginFrameworkStartedEvent event) throws LivingDocServerException {
        log.info(getClass().getName() + "#" + event.getClass().getName());
        log.info(String.format("*** Starting LivingDoc-Confluence-Plugin (v%s) ***", LivingDocServer.VERSION));
        enableLivingDocPlugin();
    }

    @EventListener
    public void pluginInstallEvent(PluginInstallEvent event) throws LivingDocServerException {
        enableLivingDocPlugin();
    }

    @EventListener
    public void pluginUninstallEvent(PluginUninstallEvent event) {
        disableLivingDocPlugin();
        // Remove the stored configuration.
        removeValue(LivingDocServerConfiguration.class);
    }

    @EventListener
    public void pluginEnableEvent(PluginEnableEvent event) throws LivingDocServerException {
        enableLivingDocPlugin();
    }

    @EventListener
    public void pluginDisableEvent(PluginDisableEvent event) {
        disableLivingDocPlugin();
    }

    private void enableLivingDocPlugin() throws LivingDocServerException {
        isPluginEnabled = true;
        if (getConfiguration().isSetupComplete()) {
            setupDatabaseFromStoredConfiguration();
        }
    }

    private void disableLivingDocPlugin() {
        isPluginEnabled = false;
        closeSession();
    }

    @Override
    public void destroy() throws Exception {
        eventPublisher.unregister(this);
    }

    public boolean isServerSetupComplete() {
        return getConfiguration().isSetupComplete();
    }
}
