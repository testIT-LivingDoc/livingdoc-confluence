package info.novatec.testit.livingdoc.confluence.macros;

import java.util.Map;

import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.MacroException;


public class LivingDocLogo extends AbstractLivingDocMacro {
    @Deprecated
    @Override
    public boolean isInline() {
        return true;
    }

    @Override
    public boolean hasBody() {
        return false;
    }

    @Override
    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    @Override
    @SuppressWarnings({ "rawtypes" })
    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        try {
            Map contextMap = MacroUtils.defaultVelocityContext();
            return VelocityUtils.getRenderedTemplate("/templates/livingdoc/confluence/macros/livingDocLogo.vm", contextMap);
        } catch (Exception e) {
            return getErrorView("livingdoc.logo.macroid", e.getMessage());
        }
    }
}
