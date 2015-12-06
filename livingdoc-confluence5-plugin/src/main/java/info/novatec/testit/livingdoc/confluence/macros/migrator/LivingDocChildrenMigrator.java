package info.novatec.testit.livingdoc.confluence.macros.migrator;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.xhtml.api.MacroDefinition;


public class LivingDocChildrenMigrator implements MacroMigration {

    @Override
    public MacroDefinition migrate(MacroDefinition macroDefinition, ConversionContext context) {

        return macroDefinition;
    }
}
