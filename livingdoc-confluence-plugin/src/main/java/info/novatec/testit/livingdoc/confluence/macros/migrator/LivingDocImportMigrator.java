package info.novatec.testit.livingdoc.confluence.macros.migrator;

import java.util.HashMap;
import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.xhtml.api.MacroDefinition;

import info.novatec.testit.livingdoc.confluence.macros.LivingDocImport;
import info.novatec.testit.livingdoc.confluence.velocity.LivingDocConfluenceManager;


public class LivingDocImportMigrator implements MacroMigration {

    @Override
    public MacroDefinition migrate(MacroDefinition macroDefinition, ConversionContext context) {
        final String imports = getV3Imports(macroDefinition);

        Map<String, String> params = new HashMap<String, String>(1) {
            private static final long serialVersionUID = 1L;

            {
                put(LivingDocImport.IMPORTS_PARAM, imports);
            }
        };
        macroDefinition.setParameters(params);

        return macroDefinition;
    }

    private String getV3Imports(MacroDefinition macroDefinition) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(macroDefinition.getDefaultParameterValue());
        int index = 1;
        Map<String, String> parameters = macroDefinition.getParameters();
        String importParam = parameters.get("" + index);

        while (importParam != null) {
            buffer.append(",");
            buffer.append(LivingDocConfluenceManager.clean(importParam));
            importParam = parameters.get("" + ++ index);
        }
        return buffer.toString();
    }
}
