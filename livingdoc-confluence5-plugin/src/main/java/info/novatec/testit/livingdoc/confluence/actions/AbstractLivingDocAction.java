package info.novatec.testit.livingdoc.confluence.actions;

import java.util.List;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;

import info.novatec.testit.livingdoc.confluence.StaticAccessor;
import info.novatec.testit.livingdoc.confluence.velocity.ConfluenceLivingDoc;


@SuppressWarnings("serial")
public abstract class AbstractLivingDocAction extends ConfluenceActionSupport {
    protected ConfluenceLivingDoc confluenceLivingDoc = StaticAccessor.getConfluenceLivingDoc();

    protected String bulkUID = "PAGE";
    protected String executionUID;
    protected int fieldId;
    protected String spaceKey;
    protected Long pageId;
    protected Page page;

    protected Boolean canEdit;
    protected boolean refreshAll;
    protected boolean isEditMode;

    private String pageConent;

    public String getBulkUID() {
        return bulkUID;
    }

    public void setBulkUID(String bulkUID) {
        this.bulkUID = bulkUID;
    }

    public String getExecutionUID() {
        return executionUID;
    }

    public void setExecutionUID(String executionUID) {
        this.executionUID = executionUID;
    }

    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public Long getPageId() {
        return this.pageId;
    }

    public void setPageId(Long pageId) {
        page = confluenceLivingDoc.getPageManager().getPage(pageId);
        this.pageId = pageId;
    }

    public boolean getIsEditMode() {
        return this.isEditMode;
    }

    public void setIsEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
    }

    public boolean getRefreshAll() {
        return refreshAll;
    }

    public void setRefreshAll(boolean refreshAll) {
        this.refreshAll = refreshAll;
    }

    public String getPageContent() {
        if (pageConent != null) {
            return pageConent;
        }
        pageConent = confluenceLivingDoc.getPageContent(page);
        return pageConent;
    }

    public List<Page> getPermittedChildren(Page parentPage) {
        return confluenceLivingDoc.getContentPermissionManager().getPermittedChildren(parentPage, getAuthenticatedUser());
    }

    @Override
    @HtmlSafe
    public String getText(String key) {
        return confluenceLivingDoc.getText(key);
    }

    public boolean getCanEdit() {
        if (canEdit != null) {
            return canEdit;
        }
        canEdit = confluenceLivingDoc.canEdit(page);
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    @Override
    public void addActionError(String msg) {
        if ( ! hasActionErrors()) {
            super.addActionError(msg);
        }
    }

    public String getExecutionTimeout() {
        return confluenceLivingDoc.getLDServerConfigurationActivator().getConfiguration().getProperties().getProperty("executionTimeout");
    }
}
