package info.novatec.testit.livingdoc.confluence.listeners;

import com.atlassian.confluence.event.events.admin.GlobalSettingsChangedEvent;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import info.novatec.testit.livingdoc.confluence.LivingDocServerConfigurationActivator;
import info.novatec.testit.livingdoc.confluence.utils.RepositoryUtils;
import info.novatec.testit.livingdoc.confluence.velocity.LivingDocConfluenceManager;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class LivingDocSettingsListener implements DisposableBean {

    private static Logger log = LoggerFactory.getLogger(LivingDocSettingsListener.class);

    private final LivingDocServerConfigurationActivator livingDocServerConfigurationActivator;
    private final LivingDocConfluenceManager livingDocConfluenceManager;
    private final EventPublisher eventPublisher;

    public LivingDocSettingsListener(EventPublisher eventPublisher, LivingDocConfluenceManager livingDocConfluenceManager,
                                     LivingDocServerConfigurationActivator livingDocServerConfigurationActivator) {
        this.eventPublisher = eventPublisher;
        this.livingDocConfluenceManager = livingDocConfluenceManager;
        this.livingDocServerConfigurationActivator = livingDocServerConfigurationActivator;

        eventPublisher.register(this);
    }

    @EventListener
    public void configurationEvent(GlobalSettingsChangedEvent event) {
        final String oldBaseUrl = event.getOldSettings().getBaseUrl();
        final String newBaseUrl = event.getNewSettings().getBaseUrl();

        if (livingDocServerConfigurationActivator.isServerSetupComplete()
                && livingDocServerConfigurationActivator.isReady()
                && !oldBaseUrl.equals(newBaseUrl)) {
            for (Space space : livingDocConfluenceManager.getSpaceManager().getAllSpaces()) {
                try {
                    Repository repositoryUid = livingDocConfluenceManager.getHomeRepository(space.getKey());

                    Repository repository = livingDocConfluenceManager.getPersistenceService().getRegisteredRepository(repositoryUid);
                    repository.setBaseUrl(newBaseUrl);
                    repository.setBaseRepositoryUrl(RepositoryUtils.repositoryBaseUrl(newBaseUrl, space.getKey()));
                    repository.setBaseTestUrl(RepositoryUtils.baseTestUrl(newBaseUrl, space.getKey()));

                    livingDocConfluenceManager.getPersistenceService().updateRepositoryRegistration(repository);
                    log.debug(String.format("LivingDoc repository %s (%s): baseUrl was updated to %s", repository.getName(),
                            space.getKey(), newBaseUrl));
                } catch (LivingDocServerException ldExc) {
                    log.error(ldExc.getMessage(), ldExc);
                }
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        eventPublisher.unregister(this);
    }
}
