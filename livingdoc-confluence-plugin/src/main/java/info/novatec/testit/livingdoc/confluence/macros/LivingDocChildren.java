package info.novatec.testit.livingdoc.confluence.macros;

import java.util.Map;

import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.renderer.RenderContext;

import info.novatec.testit.livingdoc.confluence.actions.execution.ChildrenExecutionAction;
import info.novatec.testit.livingdoc.confluence.utils.MacroCounter;
import info.novatec.testit.livingdoc.server.LivingDocServerException;


public class LivingDocChildren extends AbstractLivingDocMacro {
    /* all: Default --> false If set to true, the complete hierarchy of the
     * parent page will be included in the execution list. Otherwise only the
     * first-level children will be included.
     * 
     * spaceKey: Default --> Macro's residing space You can specify a specific
     * space for your parent page.
     * 
     * pageTitle: Default --> Macro's residing page You can specify a specific
     * title for the parent page.
     * 
     * title: Default --> blank You can specify your own title.
     * 
     * expanded: Default --> false You can ask for the list to be expanded on
     * page load.
     * 
     * suts: Default --> All You can narrow the list to specific Systems Under
     * Test (1 or more; if more than one they must be separated by a comma).
     * 
     * group: Default --> PAGE You can regroup all your macros in a page under a
     * group name. (See Group Macro.)
     * 
     * sort Default --> Hierarchical sort You can configure the way children are
     * sorted. Choose Creation to sort by content creation dates, Title to sort
     * alphabetically by titles and Modified to sort by modification dates (last
     * date first).
     * 
     * reverse Default --> false You can reverse the sorting sequence.
     * 
     * openInSameWindow Default --> false NEW in v2.1 You can control whether or
     * not the link to the specification will open in the same window. */
    public static final String TITLE_PARAM = "title";

    @Deprecated
    @Override
    public boolean isInline() {
        return false;
    }

    @Override
    public BodyType getBodyType() {
        return BodyType.NONE;
    }

    @Override
    public OutputType getOutputType() {
        return OutputType.BLOCK;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public String execute(Map parameters, String body, RenderContext renderContext) {
        try {
            boolean openInSameWindow;
            String boolValueOpenInSameWindow = ( String ) parameters.get("openInSameWindow");
            if (boolValueOpenInSameWindow == null) {
                openInSameWindow = false;
            } else {
                openInSameWindow = Boolean.valueOf(boolValueOpenInSameWindow);
            }
            String view = "/templates/livingdoc/confluence/macros/livingDocList.vm";
            Map contextMap = MacroUtils.defaultVelocityContext();
            contextMap.put(TITLE_PARAM, parameters.get(TITLE_PARAM));

            String spaceKey = getSpaceKey(parameters, renderContext, true);
            boolean allChildren = withAllChildren(parameters);
            ChildrenExecutionAction action = new ChildrenExecutionAction(ldUtil);
            action.setBulkUID(getBulkUID(parameters));
            action.setExecutionUID("CHILDREN_" + ( allChildren ? "ALL_" : "" ) + MacroCounter.instance().getNextCount());
            action.setSpaceKey(spaceKey);
            action.setPage(getPage(parameters, renderContext, spaceKey));
            action.setShowList(isExpanded(parameters));
            action.setForcedSuts(( String ) parameters.get("suts"));
            action.setAllChildren(allChildren);
            action.setSortType(( String ) parameters.get("sort"));
            action.setReverse(Boolean.valueOf(( String ) parameters.get("reverse")).booleanValue());

            contextMap.put("openInSameWindow", openInSameWindow);
            contextMap.put("action", action);
            contextMap.put("view", "/templates/livingdoc/confluence/execution/children-execution.vm");

            return VelocityUtils.getRenderedTemplate(view, contextMap);
        } catch (LivingDocServerException lde) {
            return getErrorView("livingdoc.children.macroid", lde.getId());
        } catch (Exception e) {
            return getErrorView("livingdoc.children.macroid", e.getMessage());
        }
    }

    private boolean withAllChildren(Map< ? , ? > parameters) {
        String all = ( String ) parameters.get("all");
        return all != null && Boolean.valueOf(all);
    }
}
