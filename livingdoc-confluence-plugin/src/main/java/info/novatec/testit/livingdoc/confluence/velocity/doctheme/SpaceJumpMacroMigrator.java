package info.novatec.testit.livingdoc.confluence.velocity.doctheme;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.xhtml.api.MacroDefinition;


public class SpaceJumpMacroMigrator implements MacroMigration {
    @SuppressWarnings("serial")
    @Override
    public MacroDefinition migrate(final MacroDefinition macroDefinition, ConversionContext conversionContext) {
        final String spaceKey = macroDefinition.getDefaultParameterValue();
        if (StringUtils.isNotBlank(spaceKey)) {
            macroDefinition.setParameters(new HashMap<String, String>(macroDefinition.getParameters()) {
                {
                    put("space", spaceKey);
                }
            });
        }
        return macroDefinition;
    }
}
