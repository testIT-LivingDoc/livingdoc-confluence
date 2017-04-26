package info.novatec.testit.livingdoc.confluence.actions.execution;

import java.util.List;

import com.atlassian.confluence.pages.Page;
import info.novatec.testit.livingdoc.confluence.LivingDocServerConfigurationActivator;
import info.novatec.testit.livingdoc.confluence.velocity.LivingDocConfluenceManager;


@SuppressWarnings("serial")
public class ChildrenExecutionAction extends AbstractListExecutionAction {
    protected boolean allChildren;

    public ChildrenExecutionAction(){}
    public ChildrenExecutionAction(LivingDocConfluenceManager confluenceLivingDoc,
                                   LivingDocServerConfigurationActivator livingDocServerConfigurationActivator) {
        super(confluenceLivingDoc, livingDocServerConfigurationActivator);
    }

    @Override
    public void buildExecutableList() {
        fillExecutableList(page);
    }

    public boolean getAllChildren() {
        return allChildren;
    }

    public void setAllChildren(boolean allChildren) {
        this.allChildren = allChildren;
    }

    private void fillExecutableList(Page page) {
        List<Page> pageChildren = getPermittedChildren(page);
        for (Page child : pageChildren) {
            if (getLivingDocConfluenceManager().isExecutable(child)) {
                executableList.add(child);
            }
            if (getAllChildren()) {
                fillExecutableList(child);
            }
        }
    }
}
