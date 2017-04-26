package info.novatec.testit.livingdoc.confluence.macros;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;

import info.novatec.testit.livingdoc.confluence.utils.MacroParametersUtils;
import info.novatec.testit.livingdoc.confluence.velocity.LivingDocConfluenceManager;


public class LivingDocImport extends AbstractLivingDocMacro {
    public static final String IMPORTS_PARAM = "imports";

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        try {
            Map contextMap = MacroUtils.defaultVelocityContext();
            contextMap.put(IMPORTS_PARAM, getImportList(parameters));
            return VelocityUtils.getRenderedTemplate("/templates/livingdoc/confluence/macros/livingDocImport.vm",
                contextMap);
        } catch (Exception e) {
            return getErrorView("livingdoc.import.macroid", e.getMessage());
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String getErrorView(String macroId, String errorId) {
        Map contextMap = MacroUtils.defaultVelocityContext();
        contextMap.put("macroId", macroId);
        contextMap.put("errorId", errorId);
        return VelocityUtils.getRenderedTemplate("/templates/livingdoc/confluence/macros/livingDocMacros-error.vm",
            contextMap);
    }

    @SuppressWarnings("rawtypes")
    private List<String> getImportList(Map parameters) {

        List<String> imports = new ArrayList<String>();

        // v4
        String[] values = MacroParametersUtils.extractParameterMultiple(IMPORTS_PARAM, parameters);
        for (String value : values) {
            if (value != null) {
                imports.add(LivingDocConfluenceManager.clean(value));
            }
        }

        return imports;
    }

}
