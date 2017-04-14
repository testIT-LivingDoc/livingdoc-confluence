package info.novatec.testit.livingdoc.confluence.actions;

import java.util.List;

import info.novatec.testit.livingdoc.confluence.velocity.LivingDocConfluenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;

import info.novatec.testit.livingdoc.server.LivingDocServerException;


@SuppressWarnings("serial")
public abstract class AbstractLivingDocAction extends ConfluenceActionSupport {

    private static final Logger log = LoggerFactory.getLogger(AbstractLivingDocAction.class);

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
    private LivingDocConfluenceManager livingDocConfluenceManager;

    public AbstractLivingDocAction(){
        livingDocConfluenceManager = null;
    }
    public AbstractLivingDocAction(LivingDocConfluenceManager livingDocConfluenceManager) {
        this.livingDocConfluenceManager = livingDocConfluenceManager;
    }

    public void setLivingDocConfluenceManager(LivingDocConfluenceManager livingDocConfluenceManager) {
        this.livingDocConfluenceManager = livingDocConfluenceManager;
    }

    protected LivingDocConfluenceManager getLivingDocConfluenceManager() {
        if(livingDocConfluenceManager == null){
            log.error("Bean injection of livingDocConfluenceManager failed");
        }
        return livingDocConfluenceManager;
    }

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
        page = livingDocConfluenceManager.getPageManager().getPage(pageId);
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
        pageConent = livingDocConfluenceManager.getPageContent(page);
        return pageConent;
    }

    public List<Page> getPermittedChildren(Page parentPage) {
        return livingDocConfluenceManager.getContentPermissionManager().getPermittedChildren(parentPage, getAuthenticatedUser());
    }

    @Override
    @HtmlSafe
    public String getText(String key) {
        return livingDocConfluenceManager.getText(key);
    }

    public boolean getCanEdit() {
        if (canEdit != null) {
            return canEdit;
        }
        canEdit = livingDocConfluenceManager.canEdit(page);
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }
    public String getExecutionTimeout() {
        return livingDocConfluenceManager.getLDServerConfigurationActivator().getConfiguration().getProperties().getProperty(
            "executionTimeout");
    }
    
    @Override
    public void addActionError(String msg) {
        if ( ! hasActionErrors()) {
            super.addActionError(msg);
        }
    }

    public void addActionError(LivingDocServerException ldse) {
        super.addActionError(ldse.getId());
        log.error("Error executing action " + getClass().getName(),ldse);
        if (ldse.getCause() != null) {
            log.error("Error in action", ldse.getCause());
        }
    }

   
}
