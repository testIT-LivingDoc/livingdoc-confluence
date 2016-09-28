package info.novatec.testit.livingdoc.server.database.hibernate;

import static org.junit.Assert.assertNotNull;

import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import info.novatec.testit.livingdoc.server.configuration.ServerConfiguration;
import info.novatec.testit.livingdoc.server.database.hibernate.hsqldb.AbstractDBUnitHibernateMemoryTest;
import info.novatec.testit.livingdoc.server.domain.SystemInfo;
import info.novatec.testit.livingdoc.server.domain.dao.SystemInfoDao;
import info.novatec.testit.livingdoc.server.domain.dao.hibernate.HibernateSystemInfoDao;

public class BootstrapDataTest extends AbstractDBUnitHibernateMemoryTest {
    private URL configURL = BootstrapDataTest.class.getResource("configuration-test.xml");
    private BootstrapData boot;
    private SystemInfoDao systemDao;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        ServerConfiguration config = ServerConfiguration.load(configURL);
        config.getProperties().setProperty("baseUrl", "no directories");

        boot = new BootstrapData(this, config.getProperties());
        systemDao = new HibernateSystemInfoDao(this);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
        // overrides tearDown from extended class
    }

    @Test
    public void testWhileRegistratingTheRunnersIfAnErrorOccuresTheBootstrapProcessWillContinue() throws Exception {
        boot.execute();
        assertNotNull(systemDao.getSystemInfo());
    }

    private void setupSystemInfo(String serverVersion) {
        SystemInfo sysForServerOfCurrentVersion = new SystemInfo();
        sysForServerOfCurrentVersion.setVersion(0);
        sysForServerOfCurrentVersion.setServerVersion(serverVersion);
        systemDao.store(sysForServerOfCurrentVersion);
    }
}
