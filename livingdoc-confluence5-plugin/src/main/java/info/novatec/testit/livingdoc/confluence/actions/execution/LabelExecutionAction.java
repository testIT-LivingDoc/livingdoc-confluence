package info.novatec.testit.livingdoc.confluence.actions.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.PartialList;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.pages.Page;


@SuppressWarnings("serial")
public class LabelExecutionAction extends AbstractListExecutionAction {
    private String labels;
    private boolean searchQuery;

    @Override
    public void buildExecutableList() {
        for (String labelExp : labels.split(",")) {
            List<Page> pages = getExpLabeledPages(labelExp);
            for (Page page : pages) {
                if (page.getSpaceKey().equals(spaceKey) && ! executableList.contains(page) && confluenceLivingDoc.isExecutable(page)) {
                    executableList.add(page);
                }
            }
        }
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels.replaceAll("&amp;", "&");
    }

    public boolean getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(boolean searchQuery) {
        this.searchQuery = searchQuery;
    }

    private List<Page> getExpLabeledPages(String labelExp) {
        StringTokenizer stk = new StringTokenizer(labelExp, "&");
        int tokens = stk.countTokens();
        if (tokens <= 1) {
            return getLabeledPages(labelExp.trim());
        }

        List<Page> pages = getLabeledPages(stk.nextToken().trim());
        for (int i = 1; i < tokens; i ++ ) {
            pages.retainAll(getLabeledPages(stk.nextToken().trim()));
        }

        return pages;
    }

    private List<Page> getLabeledPages(String label) {
        Label labelObject = confluenceLivingDoc.getLabelManager().getLabel(label.trim());
        List<Page> pageList =  new ArrayList<Page>();
        if (labelObject != null) {
            PartialList<ContentEntityObject> partialList = confluenceLivingDoc.getLabelManager().getContentForLabel(LabelManager.NO_OFFSET,
                LabelManager.NO_MAX_RESULTS, labelObject);
            for (ContentEntityObject ceo : partialList.getList()){
                if(ceo instanceof Page){
                    pageList.add((Page)ceo);
                }
            }
        }

        return pageList;
    }
}
