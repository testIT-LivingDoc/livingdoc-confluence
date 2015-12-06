package info.novatec.testit.livingdoc.confluence.actions.server;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;

import info.novatec.testit.livingdoc.confluence.LivingDocServerConfigurationActivator;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.database.SupportedDialects;
import info.novatec.testit.livingdoc.util.I18nUtil;


@SuppressWarnings("serial")
public class InstallationAction extends LivingDocServerAction {

    private static final Logger log = LoggerFactory.getLogger(InstallationAction.class);

    private static final String RESOURCE_BUNDLE = InstallationAction.class.getName();
    private final ThreadLocal<Locale> threadLocale = new ThreadLocal<Locale>();
    private ResourceBundle resourceBundle;

    private String installType;

    private String jndiUrl;
    private String hibernateDialect;

    private boolean editMode;

    @Override
    public String getActionName(String fullClassName) {
        return getText("livingdoc.install.title");
    }

    public InstallationAction() {
    }

    public String config() {
        return SUCCESS;
    }

    public List<SupportedDialects> getDialects() {
        return Arrays.asList(SupportedDialects.values());
    }

    // We want to force edit mode if DBMS not ready
    public Boolean getEditMode() {
        return editMode | ! ( isServerReady() );
    }

    public void setEditMode(Boolean editMode) {
        this.editMode = editMode;
    }

    public boolean getIsCustomSetup() {
        return isCustomSetup();
    }

    public boolean isCustomSetup() {
        return getInstallType().equals("customInstall");
    }

    public String getInstallType() {
        return installType == null ? ( getJndiUrl() == null ? "quickInstall" : "customInstall" ) : installType;
    }

    public void setInstallType(String installType) {
        this.installType = installType;
    }

    public String changeInstallationType() {
        return SUCCESS;
    }

    public String getJndiUrl() {
        return jndiUrl == null ? getConfigurationActivator().getConfigJnriUrl() : jndiUrl;
    }

    public void setJndiUrl(String jndiUrl) {
        this.jndiUrl = jndiUrl;
    }

    public String getHibernateDialect() {
        return hibernateDialect == null ? getConfigurationActivator().getConfigDialect() : hibernateDialect;
    }

    public void setHibernateDialect(String hibernateDialect) {
        this.hibernateDialect = hibernateDialect;
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

    public boolean isSetupComplete() {
        return ldUtil.isServerSetupComplete();
    }

    @Override
    public boolean isServerReady() {
        return ldUtil.isServerReady();
    }

    public String editDbmsConfiguration() {
        try {
            if (isCustomSetup()) {
                if (hibernateDialect != null && jndiUrl != null) {
                    if (canConnectToDbms()) {
                        getConfigurationActivator().initCustomInstallConfiguration(hibernateDialect, jndiUrl);
                    } else {
                        addActionError("livingdoc.install.dbms.test.failure");
                        setEditMode(true);
                    }
                }
            } else {
                getConfigurationActivator().initQuickInstallConfiguration();
            }
        } catch (LivingDocServerException ex) {
            addActionError("livingdoc.install.dbms.init.failure");
        }

        return SUCCESS;
    }

    public String testDbmsConnection() {
        if ( ! canConnectToDbms()) {
            addActionError("livingdoc.install.dbms.test.failure");
        }

        return SUCCESS;
    }

    private boolean canConnectToDbms() {
        try {
            InitialContext context = new InitialContext();

            DataSource ds = ( DataSource ) context.lookup(jndiUrl);

            Connection connection = ds.getConnection();

            connection.close();

            return true;
        } catch (Exception ex) {
            log.error("Testing Dbms Connection using jndi (" + jndiUrl + ")", ex);
            return false;
        }
    }

    private LivingDocServerConfigurationActivator getConfigurationActivator() {
        return ldUtil.getLDServerConfigurationActivator();
    }
}
