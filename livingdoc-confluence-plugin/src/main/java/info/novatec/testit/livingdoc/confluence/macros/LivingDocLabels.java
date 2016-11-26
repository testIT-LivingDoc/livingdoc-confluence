package info.novatec.testit.livingdoc.confluence.macros;

import java.util.Map;

import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;

import info.novatec.testit.livingdoc.confluence.actions.execution.LabelExecutionAction;
import info.novatec.testit.livingdoc.confluence.utils.MacroCounter;
import info.novatec.testit.livingdoc.server.LivingDocServerException;


public class LivingDocLabels extends AbstractLivingDocMacro {
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        String view;
        try {
            Map contextMap = MacroUtils.defaultVelocityContext();
            contextMap.put("title", parameters.get("title"));

            boolean openInSameWindow;
            String boolValueOpenInSameWindow = ( String ) parameters.get("openInSameWindow");
            if (boolValueOpenInSameWindow == null) {
                openInSameWindow = false;
            } else {
                openInSameWindow = Boolean.valueOf(boolValueOpenInSameWindow);
            }
            contextMap.put("openInSameWindow", openInSameWindow);

            String spaceKey = getSpaceKey(parameters);
            String labels = ( String ) parameters.get("labels");

            if (spaceKey != null && labels != null) {
                view = "/templates/livingdoc/confluence/macros/livingDocList.vm";
                LabelExecutionAction action = new LabelExecutionAction();
                action.setBulkUID(getBulkUID(parameters));
                action.setExecutionUID("LABEL_" + MacroCounter.instance().getNextCount());
                action.setForcedSuts(( String ) parameters.get("suts"));
                action.setShowList(isExpanded(parameters));
                action.setSpaceKey(spaceKey);
                action.setLabels(labels);
                action.setSortType(( String ) parameters.get("sort"));
                action.setReverse(Boolean.valueOf(( String ) parameters.get("reverse")).booleanValue());

                contextMap.put("action", action);
                contextMap.put("view", "/templates/livingdoc/confluence/execution/label-execution.vm");

            } else {
                view = "/templates/livingdoc/confluence/macros/livingDocLabels-search.vm";
                contextMap.put("executionUID", "LABEL_SEARCH_" + MacroCounter.instance().getNextCount());
                contextMap.put("forcedSuts", parameters.get("suts"));
                contextMap.put("sortType", parameters.get("sort"));
                contextMap.put("bulkUID", getBulkUID(parameters));
                contextMap.put("spaceKey", spaceKey);
                contextMap.put("spaces", getSpaces());
                contextMap.put("currentSpaceKey", getSpaceKey(parameters, renderContext, false));
            }

            return VelocityUtils.getRenderedTemplate(view, contextMap);
        } catch (LivingDocServerException lde) {
            return getErrorView("livingdoc.labels.macroid", lde.getId());
        } catch (Exception e) {
            return getErrorView("livingdoc.labels.macroid", e.getMessage());
        }
    }
}
