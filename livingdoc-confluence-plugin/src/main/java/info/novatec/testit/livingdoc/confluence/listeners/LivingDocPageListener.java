package info.novatec.testit.livingdoc.confluence.listeners;

import com.atlassian.confluence.event.events.content.page.PageRemoveEvent;
import com.atlassian.confluence.event.events.content.page.PageTrashedEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.event.events.space.SpaceRemoveEvent;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import info.novatec.testit.livingdoc.confluence.macros.LivingDocPage;
import info.novatec.testit.livingdoc.confluence.velocity.LivingDocConfluenceManager;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.Repository;
import info.novatec.testit.livingdoc.server.domain.Specification;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;


public class LivingDocPageListener implements DisposableBean {

    private static Logger log = LoggerFactory.getLogger(LivingDocPageListener.class);
    private final LivingDocConfluenceManager ld;

    protected EventPublisher eventPublisher;

    public LivingDocPageListener(EventPublisher eventPublisher, LivingDocConfluenceManager ld) {
        this.eventPublisher = eventPublisher;
        this.ld = ld;
        eventPublisher.register(this);
    }

    @EventListener
    public void spaceRemoveEvent(SpaceRemoveEvent event) throws LivingDocServerException {
        String spaceKey = event.getSpace().getKey();
        try{
            Repository repository = ld.getHomeRepository(spaceKey);
            ld.getPersistenceService().removeRepository(repository.getUid());
        }catch(LivingDocServerException ldse){
            log.error("error removing repository für space "+ spaceKey,ldse);
        }
    }

    @EventListener
    public void pageRemoveEvent(PageRemoveEvent event) {
        removeSpecificationSafe(event.getPage());
    }

    @EventListener
    public void pageTrashedEvent(PageTrashedEvent event) {
        removeSpecificationSafe(event.getPage());
    }

    @EventListener
    public void pageUpdateEvent(PageUpdateEvent event) throws LivingDocServerException {
        log.debug("Updating specification");

        Page oldPage = (Page)event.getOriginalPage();
        Page newPage = event.getPage();
        if (newPage != null && oldPage != null && (!newPage.getTitle().equals(oldPage.getTitle()) ||
                !newPage.getBodyAsString().equals(oldPage.getBodyAsString()))) {
            boolean oldPageIsExecutable = containsPageMacro(oldPage);
            boolean newPageIsExecutable = containsPageMacro(newPage);
            try {
                Specification oldSpecification = Specification.newInstance(oldPage.getTitle());
                oldSpecification.setRepository(ld.getHomeRepository(newPage.getSpace().getKey()));
                Specification newSpecification = Specification.newInstance(newPage.getTitle());
                newSpecification.setRepository(ld.getHomeRepository(newPage.getSpace().getKey()));

                if (!newPageIsExecutable) {
                   removeSpecification(newPage);
                } else if (!oldPageIsExecutable) {
                   ld.getPersistenceService().createSpecification(newSpecification);
                } else {
                   ld.getPersistenceService().updateSpecification(oldSpecification, newSpecification);
                }
                log.debug("Successfully updated specification");

            } catch (LivingDocServerException e) {
                removeSpecificationSafe(oldPage);
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        eventPublisher.unregister(this);
    }

    private boolean containsPageMacro(AbstractPage page) {
        String content = page.getBodyAsString();
        return StringUtils.contains(content, LivingDocPage.MACRO_KEY);
    }
    private void removeSpecificationSafe(Page page) {
        try {
            removeSpecification(page);
        }catch(LivingDocServerException ldse){
            log.error("error saving specification ", ldse);
        }
    }
    private void removeSpecification(Page page) throws LivingDocServerException {
        log.debug("Removing specification");
        Specification specification = Specification.newInstance(page.getTitle());
        specification.setRepository(ld.getHomeRepository(page.getSpaceKey()));

        ld.getPersistenceService().removeSpecification(specification);

        ld.saveExecuteChildren(page, null);
        ld.saveImplementedVersion(page, null);
        ld.savePreviousImplementedVersion(page, null);
        log.debug("Successfully removed specification");
    }
}
