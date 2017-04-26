package info.novatec.testit.livingdoc.confluence.macros;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import info.novatec.testit.livingdoc.confluence.LivingDocServerConfigurationActivator;
import info.novatec.testit.livingdoc.confluence.velocity.LivingDocConfluenceManager;
import org.apache.commons.lang3.StringUtils;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.SpaceComparator;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;

import info.novatec.testit.livingdoc.server.LivingDocServerException;


public abstract class AbstractLivingDocMacro extends BaseMacro implements Macro {
    protected LivingDocConfluenceManager ldUtil;

    protected LivingDocServerConfigurationActivator livingDocServerConfigurationActivator;

    /**
     * Setter for IoC
     */
    public void setConfluenceLivingDoc(LivingDocConfluenceManager confluenceLivingDoc) {
        this.ldUtil = confluenceLivingDoc;
    }

    public void setLivingDocServerConfigurationActivator(LivingDocServerConfigurationActivator livingDocServerConfigurationActivator) {
        this.livingDocServerConfigurationActivator = livingDocServerConfigurationActivator;
    }

    // Macros v4
    @Override
    public BodyType getBodyType() {
        return BodyType.NONE;
    }

    @Override
    public OutputType getOutputType() {
        return OutputType.BLOCK;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
        throws MacroExecutionException {
        try {
            return execute(parameters, body, context.getPageContext());
        } catch (MacroException e) {
            throw new MacroExecutionException(e);
        }
    }

    // End Macros V4

    @Deprecated
    @Override
    public boolean isInline() {
        return false;
    }

    @Override
    public boolean hasBody() {
        return false;
    }

    @Override
    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    protected String getSpaceKey(Map< ? , ? > parameters) throws LivingDocServerException {
        String spaceKey = ( String ) parameters.get("spaceKey");
        if ( ! StringUtils.isEmpty(spaceKey)) {
            spaceKey = spaceKey.trim();

            Space space = ldUtil.getSpaceManager().getSpace(spaceKey);
            if (space == null)
                throw new LivingDocServerException("livingdoc.children.spacenotfound", "");

            checkSpace(space);
        }

        return spaceKey;
    }

    protected String getSpaceKey(Map< ? , ? > parameters, RenderContext renderContext, boolean checkPermission)
        throws LivingDocServerException {
        Space space;
        String spaceKey = ( String ) parameters.get("spaceKey");

        if (StringUtils.isEmpty(spaceKey)) {
            space = getCurrentSpace(renderContext);
        } else {
            spaceKey = spaceKey.trim();
            space = ldUtil.getSpaceManager().getSpace(spaceKey);
            if (space == null)
                throw new LivingDocServerException("livingdoc.children.spacenotfound", "");
        }

        if (checkPermission)
            checkSpace(space);

        return space.getKey();
    }

    protected Space getCurrentSpace(RenderContext renderContext) {
        ContentEntityObject owner = ( ( PageContext ) renderContext ).getEntity();
        return ( ( Page ) owner ).getSpace();
    }

    protected String getPageTitle(Map< ? , ? > parameters, RenderContext renderContext, String spaceKey)
        throws LivingDocServerException {
        return getPage(parameters, renderContext, spaceKey).getTitle().trim();
    }

    protected Page getPage(Map< ? , ? > parameters, RenderContext renderContext, String spaceKey)
        throws LivingDocServerException {
        String pageTitle = ( String ) parameters.get("pageTitle");
        if (StringUtils.isEmpty(pageTitle)) {
            ContentEntityObject owner = ( ( PageContext ) renderContext ).getEntity();
            return ( Page ) owner;
        }

        Page page = ldUtil.getPageManager().getPage(spaceKey, pageTitle);
        if (page == null)
            throw new LivingDocServerException("livingdoc.children.pagenotfound", "");

        return page;
    }

    protected List<Space> getSpaces() throws LivingDocServerException {
        List<Space> spaces = new ArrayList<Space>();
        List<Space> potentialSpaces = ldUtil.getSpaceManager().getAllSpaces();

        for (Space space : potentialSpaces) {
            try {
                if (ldUtil.canView(space) && ldUtil.enable(space.getKey()) == null)
                    spaces.add(space);
            } catch (LivingDocServerException e) {
            }
        }

        if (spaces.isEmpty())
            throw new LivingDocServerException("livingdoc.labels.registeredspacesempty", "No registered repository");

        Collections.sort(spaces, new SpaceComparator());

        return spaces;
    }

    protected String getBulkUID(Map< ? , ? > parameters) {
        String group = ( String ) parameters.get("group");
        return StringUtils.isEmpty(group) ? "PAGE" : group;
    }

    protected boolean isExpanded(Map< ? , ? > parameters) {
        String all = ( String ) parameters.get("expanded");
        return all != null && Boolean.valueOf(all);
    }

    public static String getErrorView(String macroId, String errorId) {
        Map<String, Object> contextMap = MacroUtils.defaultVelocityContext();
        contextMap.put("macroId", macroId);
        contextMap.put("errorId", errorId);
        return VelocityUtils.getRenderedTemplate("/templates/livingdoc/confluence/macros/livingDocMacros-error.vm",
            contextMap);
    }

    private void checkSpace(Space space) throws LivingDocServerException {
        if ( ! ldUtil.canView(space))
            throw new LivingDocServerException("livingdoc.macros.insufficientprivileges", "");

        String msg = ldUtil.enable(space.getKey());
        if (msg != null)
            throw new LivingDocServerException("< " + space.getKey() + " > " + msg, "");
    }
}
