package info.novatec.testit.livingdoc.confluence.actions.execution;

import java.util.LinkedList;
import java.util.List;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.pages.Page;

import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.Specification;
import info.novatec.testit.livingdoc.server.domain.SystemUnderTest;


@SuppressWarnings("serial")
public class HeaderExecutionAction extends ChildrenExecutionAction {
    private Boolean hasChildren;
    private Boolean doExecuteChildren;
    private boolean enableLivingDoc;
    private boolean retrieveBody;

    public String loadHeader() {
        retrieveReferenceList();
        loadSpecification();
        return SUCCESS;
    }

    public String setAsImplemented() {
        confluenceLivingDoc.saveImplementedVersion(getPage(), getPage().getVersion());
        return loadHeader();
    }

    public String revert() {
        confluenceLivingDoc.revertImplementation(getPage());
        return loadHeader();
    }

    public String enableLivingDoc() {
        if (enableLivingDoc) {
            try {
                Specification spec = Specification.newInstance(getPage().getTitle());
                spec.setRepository(confluenceLivingDoc.getHomeRepository(spaceKey));

                specification = confluenceLivingDoc.getLDServerService().createSpecification(spec);
                return loadHeader();
            } catch (LivingDocServerException e) {
                addActionError(e.getId());
            }
        } else {
            try {
                Specification spec = Specification.newInstance(getPage().getTitle());
                spec.setRepository(confluenceLivingDoc.getHomeRepository(spaceKey));

                // Clean spec
                confluenceLivingDoc.getLDServerService().removeSpecification(spec);
                confluenceLivingDoc.saveExecuteChildren(page, false);
                confluenceLivingDoc.saveImplementedVersion(getPage(), null);
                confluenceLivingDoc.savePreviousImplementedVersion(getPage(), null);
                specification = null;
            } catch (LivingDocServerException e) {
                addActionError(e.getId());
                return loadHeader();
            }
        }

        return SUCCESS;
    }

    public String updateExecuteChildren() {
        confluenceLivingDoc.saveExecuteChildren(page, doExecuteChildren);
        return SUCCESS;
    }

    @Override
    public List<SystemUnderTest> getForcedSystemUnderTests() {
        return null;
    }

    public boolean getCanBeImplemented() {
        return confluenceLivingDoc.canBeImplemented(getPage());
    }

    public boolean getCanBeReverted() {
        return getPreviousImplementedVersion() != null;
    }

    public Integer getImplementedVersion() {
        return confluenceLivingDoc.getImplementedVersion(getPage());
    }

    public Integer getPreviousImplementedVersion() {
        return confluenceLivingDoc.getPreviousImplementedVersion(getPage());
    }

    public String getRenderedContent() {
        String content;

        try {
            content = confluenceLivingDoc.getPageContent(getPage(), implemented);
        } catch (LivingDocServerException e) {
            content = "";
        }

        return confluenceLivingDoc.getViewRenderer().render(content, new DefaultConversionContext(getPage().toPageContext()));
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

        doExecuteChildren = getHasChildren() && confluenceLivingDoc.getExecuteChildren(page);
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
        return confluenceLivingDoc.isImplementationDue(getPage());
    }
}
