package info.novatec.testit.livingdoc.confluence.macros;

import java.util.Map;

import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.MacroException;


public class LivingDocInfo extends AbstractLivingDocMacro {
    @Override
    public boolean hasBody() {
        return true;
    }

    @Override
    public RenderMode getBodyRenderMode() {
        return RenderMode.ALL;
    }

    @Override
    public BodyType getBodyType() {
        return BodyType.RICH_TEXT;
    }

    @Override
    public OutputType getOutputType() {
        return OutputType.BLOCK;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        try {
            Map contextMap = MacroUtils.defaultVelocityContext();
            contextMap.put("body", body);
            contextMap.put("title", parameters.get("title"));
            contextMap.put("bgcolor", getHexColor(( String ) parameters.get("bgcolor")));
            return VelocityUtils.getRenderedTemplate("/templates/livingdoc/confluence/macros/livingDocInfo.vm", contextMap);
        } catch (Exception e) {
            return getErrorView("livingdoc.info.macroid", e.getMessage());
        }
    }

    private String getHexColor(String color) {
        if (color == null)
            return null;
        if (color.equals("red"))
            return "#FF0000";
        if (color.equals("blue"))
            return "#0000FF";
        if (color.equals("grey"))
            return "#FFFF00";
        if (color.equals("lightgrey"))
            return "#D8D8D8";
        if (color.equals("yellow"))
            return "#C0C0C0";
        if (color.equals("green"))
            return "#00FF00";
        return color;
    }
}
