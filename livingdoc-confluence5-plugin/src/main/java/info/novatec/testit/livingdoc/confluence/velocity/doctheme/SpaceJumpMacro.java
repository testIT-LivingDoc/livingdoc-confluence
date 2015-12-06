package info.novatec.testit.livingdoc.confluence.velocity.doctheme;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;


/**
 * The space-jump macro allows you to refer to the current page within a
 * different space. This helps to navigate between different versions (spaces)
 * of documentation.
 * 
 * Example: {@code spacejump:SPACEKEY|alias=LINK_TITLE}}
 */
public class SpaceJumpMacro extends BaseMacro implements Macro {
    private static final Logger log = LoggerFactory.getLogger(SpaceJumpMacro.class);

    private final XhtmlContent xhtmlContent;

    public SpaceJumpMacro(XhtmlContent xhtmlContent) {
        this.xhtmlContent = xhtmlContent;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public TokenType getTokenType(Map parameters, String body, RenderContext context) {
        return TokenType.INLINE;
    }

    @Override
    public boolean hasBody() {
        return false;
    }

    @Override
    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public String execute(Map parameters, String string, RenderContext renderContext) throws MacroException {
        try {
            return execute(parameters, string, new DefaultConversionContext(renderContext));
        } catch (MacroExecutionException e) {
            throw new MacroException(e.getMessage());
        }
    }

    @Override
    public BodyType getBodyType() {
        return BodyType.NONE;
    }

    @Override
    public OutputType getOutputType() {
        return OutputType.INLINE;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext)
        throws MacroExecutionException {
        String pageTitle = StringUtils.defaultString(conversionContext.getPageContext().getPageTitle());
        String spaceKey = StringUtils.defaultString(parameters.get("space"), parameters.get("0"));
        String alias = parameters.get("alias");

        String wiki = "[";

        if (StringUtils.isNotEmpty(alias))
            wiki += alias + "|";

        wiki += spaceKey + ":" + pageTitle + "]";

        List<RuntimeException> exceptions = new ArrayList<RuntimeException>();
        PageContext pageContext = conversionContext.getPageContext();
        String result;

        pageContext.pushRenderMode(RenderMode.INLINE);
        try {
            result = xhtmlContent.convertWikiToView(wiki, conversionContext, exceptions);
        } catch (Exception e) {
            log.debug("Error rendering wiki link: [" + wiki + "]", e);
            result = String.format("<span class=\"error\">%s</span>", GeneralUtil.htmlEncode(wiki));
        } finally {
            pageContext.popRenderMode();
        }

        if ( ! exceptions.isEmpty())
            log.debug("Error thrown attempting to migrate wiki to xhtml", exceptions.get(0));

        return result;
    }
}
