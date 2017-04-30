package info.novatec.testit.livingdoc.confluence.macros;

import java.util.Map;

import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.renderer.RenderContext;
import info.novatec.testit.livingdoc.confluence.actions.execution.HeaderExecutionAction;
import info.novatec.testit.livingdoc.confluence.utils.MacroCounter;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LivingDocPage extends AbstractLivingDocMacro {

    private static Logger log = LoggerFactory.getLogger(HeaderExecutionAction.class);
    public static final String TITLE_PARAM = "title";

    public static final String MACRO_KEY = "livingdoc-page";

    public static final String MACRO_HTML_CONTENT = "<p><ac:structured-macro ac:name=\"livingdoc-page\"/></p>";

    @Override
    public String execute(Map parameters, String body, RenderContext renderContext) {
        try {
            log.info("Executing page macro ...");
            if(!(renderContext instanceof PageContext)) {
                throw new MacroExecutionException("This macro can only be used in a page");
            }

            String view = "/templates/livingdoc/confluence/macros/livingDocPage.vm";
            Map<String, Object> contextMap = MacroUtils.defaultVelocityContext();
            contextMap.put(TITLE_PARAM, parameters.get(TITLE_PARAM));

            String spaceKey = getSpaceKey(parameters, renderContext, true);
            HeaderExecutionAction action = new HeaderExecutionAction(ldUtil, livingDocServerConfigurationActivator);
            action.setBulkUID(getBulkUID(parameters));
            action.setExecutionUID("LD_PAGE_" + MacroCounter.instance().getNextCount());
            action.setSpaceKey(spaceKey);
            action.setPage(getPage(parameters, renderContext, spaceKey));

            contextMap.put("action", action);

            return VelocityUtils.getRenderedTemplate(view, contextMap);
        } catch (LivingDocServerException lde) {
            log.error("Error executing page macro", lde);
            return getErrorView("livingdoc.page.macroid", lde.getId());
        } catch (Exception e) {
            log.error("Error executing page macro", e);
            return getErrorView("livingdoc.page.macroid", e.getMessage());
        }
    }
}
