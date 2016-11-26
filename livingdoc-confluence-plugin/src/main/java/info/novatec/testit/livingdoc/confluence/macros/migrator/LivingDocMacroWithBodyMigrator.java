package info.novatec.testit.livingdoc.confluence.macros.migrator;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.definition.MacroBody;
import com.atlassian.confluence.content.render.xhtml.definition.RichTextMacroBody;
import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.renderer.WikiStyleRenderer;


public class LivingDocMacroWithBodyMigrator implements MacroMigration {

    private WikiStyleRenderer wikiStyleRenderer;

    /**
     * Setter for IoC
     * 
     * @param wikiStyleRenderer
     */
    public void setWikiStyleRenderer(WikiStyleRenderer wikiStyleRenderer) {
        this.wikiStyleRenderer = wikiStyleRenderer;
    }

    @Override
    public MacroDefinition migrate(MacroDefinition macroDefinition, ConversionContext context) {
        MacroBody body = macroDefinition.getBody();

        String content = wikiStyleRenderer.convertWikiToXHtml(new PageContext(context.getEntity()), body.getBody());

        MacroBody newBody = RichTextMacroBody.withStorage(Streamables.from(content));
        macroDefinition.setBody(newBody);

        return macroDefinition;
    }

}
