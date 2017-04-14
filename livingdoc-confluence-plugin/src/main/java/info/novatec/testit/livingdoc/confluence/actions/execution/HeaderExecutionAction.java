package info.novatec.testit.livingdoc.confluence.actions.execution;

import java.util.LinkedList;
import java.util.List;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.pages.Page;

import info.novatec.testit.livingdoc.confluence.velocity.LivingDocConfluenceManager;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.Specification;
import info.novatec.testit.livingdoc.server.domain.SystemUnderTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("serial")
public class HeaderExecutionAction extends ChildrenExecutionAction {
    private static Logger log = LoggerFactory.getLogger(HeaderExecutionAction.class);
    private Boolean hasChildren;
    private Boolean doExecuteChildren;
    private boolean enableLivingDoc;
    private boolean retrieveBody;

    public HeaderExecutionAction(){}
    public HeaderExecutionAction(LivingDocConfluenceManager confluenceLivingDoc) {
        super(confluenceLivingDoc);
    }

    public String loadHeader() {
       try {
           log.debug("Loading header ...");
           retrieveReferenceList();
           loadSpecification();
           log.debug("Header data loaded successfully!");
       }catch (Exception e){
           log.error("Error loading header ",e);
       }
        return SUCCESS;
    }

    public String setAsImplemented() {
        log.debug("Setting Specification implemented....");
        getLivingDocConfluenceManager().saveImplementedVersion(getPage(), getPage().getVersion());
        log.debug("Specification set implemented successfully....");
        return loadHeader();
    }

    public String revert() {
        log.debug("Reverting Specification....");
        getLivingDocConfluenceManager().revertImplementation(getPage());
        log.debug("reverting Specification successfull....");

        return loadHeader();
    }

    public String enableLivingDoc() {
        if (enableLivingDoc) {
            log.debug("Making Specification executable....");
            try {
                String pageTitle = getPage().getTitle();
                log.debug("Making Specification executable, title: "+pageTitle);

                specification = getLivingDocConfluenceManager().getSpecification(getPage());
                if(specification == null) {
                    specification = Specification.newInstance(pageTitle);
                    specification.setRepository(getLivingDocConfluenceManager().getHomeRepository(spaceKey));

                    specification = getLivingDocConfluenceManager().getPersistenceService().createSpecification(specification);
                }
                log.debug("Specification made executable and successfully saved " + specification.getName());

                return loadHeader();
            } catch (LivingDocServerException e) {
                log.error("Error making specification executable", e);
                addActionError(e);
            }
        } else {
            try {
                log.debug("Making Specification NOT executable....");
                Specification spec = Specification.newInstance(getPage().getTitle());
                spec.setRepository(getLivingDocConfluenceManager().getHomeRepository(spaceKey));

                // Clean spec
                getLivingDocConfluenceManager().getPersistenceService().removeSpecification(spec);
                getLivingDocConfluenceManager().saveExecuteChildren(page, false);
                getLivingDocConfluenceManager().saveImplementedVersion(getPage(), null);
                getLivingDocConfluenceManager().savePreviousImplementedVersion(getPage(), null);
                specification = null;
                log.debug("Specification NO MORE executable and successfully saved ");

            } catch (LivingDocServerException e) {
                log.error("Error making specification NOT executable", e);
                addActionError(e);
                return loadHeader();
            }
        }

        return SUCCESS;
    }

    public String updateExecuteChildren() {
        getLivingDocConfluenceManager().saveExecuteChildren(page, doExecuteChildren);
        return SUCCESS;
    }

    @Override
    public List<SystemUnderTest> getForcedSystemUnderTests() {
        return null;
    }

    public boolean getCanBeImplemented() {
        return getLivingDocConfluenceManager().canBeImplemented(getPage());
    }

    public boolean getCanBeReverted() {
        return getPreviousImplementedVersion() != null;
    }

    public Integer getImplementedVersion() {
        return getLivingDocConfluenceManager().getImplementedVersion(getPage());
    }

    public Integer getPreviousImplementedVersion() {
        return getLivingDocConfluenceManager().getPreviousImplementedVersion(getPage());
    }

    public String getRenderedContent() {
        String content;
        try {
            content = getLivingDocConfluenceManager().getPageContent(getPage(), implemented);
        } catch (LivingDocServerException e) {
            content = "";
        }

        return getLivingDocConfluenceManager().getViewRenderer().render(content, new DefaultConversionContext(getPage().toPageContext()));
    }

    @Override
    public LinkedList<Page> getExecutableList() {
        if ( ! getDoExecuteChildren())
            return new LinkedList<Page>();

        return super.getExecutableList();
    }

    public boolean getHasChildren() {
        if (hasChildren != null)
            return hasChildren;

        hasChildren = ! implemented && ! super.getExecutableList().isEmpty();
        return hasChildren;
    }

    public boolean getDoExecuteChildren() {
        if (doExecuteChildren != null)
            return doExecuteChildren;

        doExecuteChildren = getHasChildren() && getLivingDocConfluenceManager().getExecuteChildren(page);
        return doExecuteChildren;
    }

    public void setDoExecuteChildren(boolean doExecuteChildren) {
        this.doExecuteChildren = doExecuteChildren;
    }

    @Override
    public String getExecutionUID() {
        return "HEADER";
    }

    @Override
    public boolean getIsSutEditable() {
        return true;
    }

    @Override
    public boolean getAllChildren() {
        return true;
    }

    public boolean getIsSelfIncluded() {
        return true;
    }

    public boolean isEnableLivingDoc() {
        return enableLivingDoc;
    }

    public void setEnableLivingDoc(boolean enableLivingDoc) {
        this.enableLivingDoc = enableLivingDoc;
    }

    public boolean getRetrieveBody() {
        return retrieveBody;
    }

    public void setRetrieveBody(boolean retrieveBody) {
        this.retrieveBody = retrieveBody;
    }

    public boolean isImplementationDue() {
        return getLivingDocConfluenceManager().isImplementationDue(getPage());
    }
}
