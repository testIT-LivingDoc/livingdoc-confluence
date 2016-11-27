package info.novatec.testit.livingdoc.confluence.macros;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;

import info.novatec.testit.livingdoc.confluence.utils.MacroCounter;
import info.novatec.testit.livingdoc.server.LivingDocServerException;


public class LivingDocInclude extends AbstractLivingDocMacro {
    private static final String INCLUDED_PAGE_PARAM_NAME = "ld$included";

    @Deprecated
    @Override
    public boolean isInline() {
        return true;
    }

    @Override
    @SuppressWarnings({ "rawtypes" })
    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        final PageContext context = ( PageContext ) renderContext;
        final boolean isRoot = ( getIncludedPagesParam(context) == null );

        try {
            checkMandatoryPageTitleParameter(parameters);

            final String spaceKey = getSpaceKey(parameters, renderContext, false);
            final String pageTitle = getPageTitle(parameters, renderContext, spaceKey);
            final Page owner = ( Page ) context.getEntity();

            Page page = ldUtil.getPageManager().getPage(spaceKey, pageTitle);

            List<Page> includedPages = getSafeIncludedPagesParam(context, owner);

            if (includedPages.contains(page)) {
                throw new LivingDocServerException("livingdoc.include.recursivitydetection", "");
            }

            try {
                includedPages.add(page);

                return render(parameters, context, pageTitle, page);
            } finally {
                includedPages.remove(page);
            }
        } catch (LivingDocServerException lde) {
            return getErrorView("livingdoc.include.macroid", lde.getId());
        } catch (Exception e) {
            return getErrorView("livingdoc.include.macroid", e.getMessage());
        } finally {
            if (isRoot) {
                cleanIncludedPagesParam(context);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private void checkMandatoryPageTitleParameter(Map parameters) throws LivingDocServerException {
        if ( ! parameters.containsKey("pageTitle")) {
            throw new LivingDocServerException("livingdoc.children.pagenotfound", "");
        }
    }

    @SuppressWarnings("unchecked")
    private List<Page> getIncludedPagesParam(PageContext context) {
        return ( List<Page> ) context.getParam(INCLUDED_PAGE_PARAM_NAME);
    }

    private List<Page> getSafeIncludedPagesParam(PageContext context, Page owner) {
        List<Page> pages = getIncludedPagesParam(context);

        if (pages == null) {
            pages = new ArrayList<Page>();
            pages.add(owner);
            context.addParam(INCLUDED_PAGE_PARAM_NAME, pages);
        }

        return pages;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private String render(Map parameters, PageContext context, String pageTitle, Page page) {
        Map contextMap = MacroUtils.defaultVelocityContext();

        String title = ( String ) parameters.get("title");
        contextMap.put("title", title != null ? title : pageTitle);
        contextMap.put("includeHtml", ldUtil.getViewRenderer().render(page));
        contextMap.put("executionUID", "LD_INCLUDE_" + MacroCounter.instance().getNextCount());
        contextMap.put("expanded", isExpanded(parameters));

        return VelocityUtils.getRenderedTemplate("/templates/livingdoc/confluence/macros/livingDocInclude.vm", contextMap);
    }

    private void cleanIncludedPagesParam(PageContext context) {

        List<Page> pages = getIncludedPagesParam(context);

        if (pages != null) {
            pages.clear();
        }

        context.addParam(INCLUDED_PAGE_PARAM_NAME, null);
    }
}
