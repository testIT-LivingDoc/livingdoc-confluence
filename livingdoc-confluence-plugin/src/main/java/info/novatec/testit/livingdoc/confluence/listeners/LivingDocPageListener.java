package info.novatec.testit.livingdoc.confluence.listeners;

import com.atlassian.confluence.event.events.content.page.PageEvent;
import com.atlassian.confluence.event.events.content.page.PageRemoveEvent;
import com.atlassian.confluence.event.events.content.page.PageTrashedEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.event.events.space.SpaceRemoveEvent;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import info.novatec.testit.livingdoc.confluence.velocity.ConfluenceLivingDoc;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.Specification;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;


public class LivingDocPageListener implements DisposableBean {
    private final ConfluenceLivingDoc ld;

    protected EventPublisher eventPublisher;

    public LivingDocPageListener(EventPublisher eventPublisher, ConfluenceLivingDoc ld) {
        this.eventPublisher = eventPublisher;
        this.ld = ld;
        eventPublisher.register(this);
    }

    @EventListener
    public void pageUpdateEvent(PageUpdateEvent event) throws LivingDocServerException {
        updateSpecification(event);
    }

    @EventListener
    public void spaceRemoveEvent(SpaceRemoveEvent event) throws LivingDocServerException {
        removeRepository(event);
    }

    @EventListener
    public void pageRemoveEvent(PageRemoveEvent event) throws LivingDocServerException {
        removeSpecification(event);
    }

    @EventListener
    public void pageTrashedEvent(PageTrashedEvent event) throws LivingDocServerException {
        removeSpecification(event);
    }

    private void removeSpecification(PageEvent pageEvt) throws LivingDocServerException {
        Page page = pageEvt.getPage();

        Specification specification = Specification.newInstance(page.getTitle());
        specification.setRepository(ld.getHomeRepository(page.getSpaceKey()));

        ld.getLDServerService().removeSpecification(specification);

        ld.saveExecuteChildren(page, null);
        ld.saveImplementedVersion(page, null);
        ld.savePreviousImplementedVersion(page, null);
    }

    private void removeRepository(SpaceRemoveEvent spaceEvt) throws LivingDocServerException {
        ld.getLDServerService().removeRepository(ld.getHomeRepository(spaceEvt.getSpace().getKey()).getUid());
    }

    private void updateSpecification(PageUpdateEvent pageEvt) throws LivingDocServerException {
        AbstractPage oldPage = pageEvt.getOriginalPage();
        Page newPage = pageEvt.getPage();
        if (newPage != null && oldPage != null && (!newPage.getTitle().equals(oldPage.getTitle()) ||
                !newPage.getBodyAsString().equals(oldPage.getBodyAsString()))) {
            Specification oldSpecification = Specification.newInstance(oldPage.getTitle());
            oldSpecification.setRepository(ld.getHomeRepository(newPage.getSpace().getKey()));
            boolean oldPageIsExecutable = containsPageMacro(oldPage);
            boolean newPageIsExecutable = containsPageMacro(newPage);
            try {
                Specification newSpecification = Specification.newInstance(newPage.getTitle());
                newSpecification.setRepository(ld.getHomeRepository(newPage.getSpace().getKey()));
               if(!newPageIsExecutable){
                   removeSpecification(pageEvt);
               }else if(!oldPageIsExecutable) {
                   ld.getLDServerService().createSpecification(newSpecification);
               }else{
                   ld.getLDServerService().updateSpecification(oldSpecification, newSpecification);
               }
            } catch (LivingDocServerException e) {
                ld.getLDServerService().removeSpecification(oldSpecification);
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        eventPublisher.unregister(this);
    }

    private boolean containsPageMacro(AbstractPage page){
        String content = page.getBodyAsString();
        return StringUtils.contains(content, "livingdoc-page");
    }
}
