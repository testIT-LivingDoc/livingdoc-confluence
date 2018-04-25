package info.novatec.testit.livingdoc.confluence.server;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.HtmlEntityEscapeUtil;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.EntityException;
import info.novatec.testit.livingdoc.confluence.macros.LivingDocPage;
import info.novatec.testit.livingdoc.confluence.utils.stylesheet.StyleSheetExtractor;
import info.novatec.testit.livingdoc.confluence.velocity.LivingDocConfluenceManager;
import info.novatec.testit.livingdoc.report.XmlReport;
import info.novatec.testit.livingdoc.server.LivingDocServerErrorKey;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.DocumentNode;
import info.novatec.testit.livingdoc.server.rest.LivingDocRestHelper;
import info.novatec.testit.livingdoc.server.rpc.xmlrpc.XmlRpcDataMarshaller;
import info.novatec.testit.livingdoc.server.transfer.ExecutionResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;


public class ConfluenceLivingDocServiceImpl implements LivingDocRestHelper {

    public static final String SPACE_NOT_FOUND = "livingdoc.rpc.spacenotfound";
    public static final String PAGE_NOT_FOUND = "livingdoc.rpc.pagenotfound";
    public static final String INVALID_SESSION = "livingdoc.rpc.invalidsession";
    public static final String PERMISSION_DENIED = "livingdoc.rpc.permissiondenied";
    public static final String GENERAL_EXCEPTION = "livingdoc.server.generalexeerror";

    private final Logger log = LoggerFactory.getLogger(ConfluenceLivingDocServiceImpl.class);

    private final LivingDocConfluenceManager ldUtil;
    private final StyleSheetExtractor styleSheetExtractor;

    public ConfluenceLivingDocServiceImpl(LivingDocConfluenceManager ldUtil, StyleSheetExtractor styleSheetExtractor) {
        this.ldUtil = ldUtil;
        this.styleSheetExtractor = styleSheetExtractor;
    }

    @Override
    public String getRenderedSpecification(final String username, final String password, final ArrayList<?> args) {
        if (args.size() < 3) {
            return error("Parameters Missing, expecting:[SpaceKey, PageTitle] !");
        }
        final boolean implementedVersion = args.size() < 4 || (Boolean) args.get(3);

        TransactionTemplate txTemplate = ldUtil.getTransactionTemplate();
        return txTemplate.execute(() -> {

            try {
                ConfluenceUser user = login(username, password);

                String spaceName = (String) args.get(0);
                String pageName = (String) args.get(1);
                String decodedPageName = URLDecoder.decode(pageName, "UTF-8");

                Page page = ldUtil.getPageManager().getPage(spaceName, decodedPageName);
                if (page == null) {
                    return error(PAGE_NOT_FOUND);
                }

                checkPermissions(page.getSpace(), user);
                return getRenderedSpecification(page, implementedVersion, (Boolean) args.get(2));

            } catch (NotPermittedException e) {
                return error(PERMISSION_DENIED);
            } catch (UnsupportedEncodingException e) {
                return error(PAGE_NOT_FOUND);
            } catch (EntityException e) {
                return error(GENERAL_EXCEPTION);
            } finally {
                logout();
            }
        });
    }

    @Override
    public List<?> getSpecificationHierarchy(final String username, final String password, final ArrayList<?> args) {
        if (args.isEmpty()) {
            return new DocumentNode("Parameters Missing, expecting:[SpaceKey] !").marshallize();
        }

        TransactionTemplate txTemplate = ldUtil.getTransactionTemplate();
        return txTemplate.execute(() -> {
            try {
                ConfluenceUser user = login(username, password);
                Space space = ldUtil.getSpaceManager().getSpace((String) args.get(0));
                if (space == null) {
                    return new DocumentNode(ldUtil.getText(SPACE_NOT_FOUND)).marshallize();
                }

                checkPermissions(space, user);
                return getSpecificationHierarchy(space);

            } catch (NotPermittedException e) {
                return new DocumentNode(ldUtil.getText(PERMISSION_DENIED)).marshallize();
            } catch (EntityException e) {
                return new DocumentNode(ldUtil.getText(GENERAL_EXCEPTION)).marshallize();
            } finally {
                logout();
            }
        });
    }

    @Override
    public String setSpecificationAsImplemented(final String username, final String password, final ArrayList<?> args) {
        if (args.size() < 3) {
            return error("Parameters Missing, expecting:[SpaceKey, PageTitle] !");
        }

        TransactionTemplate txTemplate = ldUtil.getTransactionTemplate();
        return txTemplate.execute(() -> {
            try {
                ConfluenceUser user = login(username, password);
                Page page = ldUtil.getPageManager().getPage((String) args.get(0), (String) args.get(1));
                if (page == null) {
                    return error(PAGE_NOT_FOUND);
                }
                checkPermissions(page.getSpace(), user);
                ldUtil.saveImplementedVersion(page, page.getVersion());
                return LivingDocServerErrorKey.SUCCESS;

            } catch (NotPermittedException e) {
                return error(PERMISSION_DENIED);
            } catch (EntityException e) {
                return error(GENERAL_EXCEPTION);
            } finally {
                logout();
            }
        });
    }

    @Override
    public String saveExecutionResult(final String username, final String password, final ArrayList<Object> args) {
        if (args.size() < 4) {
            return error("Parameters Missing, expecting:[SpaceKey, PageTitle, SUT, Xml Report Data] !");
        }

        TransactionTemplate txTemplate = ldUtil.getTransactionTemplate();
        return txTemplate.execute(() -> {

            try {
                ConfluenceUser user = login(username, password);
                ExecutionResult executionResult = XmlRpcDataMarshaller.toExecutionResult(args);

                Page page = ldUtil.getPageManager().getPage(executionResult.getSpaceKey(), executionResult.getPageTitle());
                if (page == null) {
                    return error(PAGE_NOT_FOUND);
                }
                checkPermissions(page.getSpace(), user);
                ldUtil.saveExecutionResult(page, executionResult.getSut(), XmlReport.parse(executionResult.getXmlReport()));
                return LivingDocServerErrorKey.SUCCESS;

            } catch (NotPermittedException e) {
                return error(PERMISSION_DENIED);
            } catch (LivingDocServerException e) {
                log.error(ldUtil.getText(e.getId()), e);
                return error(e.getId());
            } catch (Exception e) {
                log.error(ldUtil.getText(GENERAL_EXCEPTION), e);
                return error(GENERAL_EXCEPTION);
            } finally {
                logout();
            }
        });
    }

    private void checkPermissions(Space space, ConfluenceUser user) throws NotPermittedException {
        List<String> permTypes = new ArrayList<>();
        permTypes.add(SpacePermission.VIEWSPACE_PERMISSION);
        if (!ldUtil.getSpacePermissionManager().hasPermissionForSpace(user, permTypes, space)) {
            throw new NotPermittedException();
        }
    }

    private ConfluenceUser login(String username, String password) throws EntityException {

        if (StringUtils.isNotEmpty(username) && ldUtil.isCredentialsValid(username, password)) {
            return (ConfluenceUser) ldUtil.getConfluenceUserManager().getUser(username);
        }
        return null;

    }

    private void logout() {
        AuthenticatedUserThreadLocal.set(null);
    }

    private String getRenderedSpecification(Page page, boolean implementedVersion, boolean includeStyle) {
        try {
            String baseUrl = ldUtil.getBaseUrl();

            StringBuffer basicRenderedPage = new StringBuffer("<html>\n");
            basicRenderedPage.append("<head>\n<title>").append(page.getTitle()).append("</title>\n");
            basicRenderedPage.append("<meta http-equiv=\"content-type\" content=\"text/html;charset=").append(
                    ldUtil.getEncoding()).append("\"/>\n");
            basicRenderedPage.append("<meta name=\"title\" content=\"").append(page.getTitle()).append("\"/>\n");
            basicRenderedPage.append("<meta name=\"external-link\" content=\"").append(baseUrl).append(page.getUrlPath())
                    .append("\"/>\n");

            if (includeStyle) {
                basicRenderedPage.append(styleSheetExtractor.renderStyleSheet(page.getSpace()));
                basicRenderedPage.append("<base href=\"").append(baseUrl).append("\"/>\n");
            }

            basicRenderedPage.append("</head>\n<body>\n");

            if (includeStyle) {
                basicRenderedPage.append("<div id=\"Content\" style=\"text-align:left; padding: 5px;\">\n");
            }

            String content = ldUtil.getPageContent(page, implementedVersion);
            if (content == null) {
                throw new LivingDocServerException();
            }

            // To prevent loops caused by these macro rendering
            content = content.replaceAll("livingdoc-manage", "livingdoc-manage-not-rendered");
            content = content.replaceAll("livingdoc-hierarchy", "livingdoc-hierarchy-not-rendered");
            content = content.replaceAll("livingdoc-children", "livingdoc-children-not-rendered");
            content = content.replaceAll("livingdoc-labels", "livingdoc-labels-not-rendered");
            content = content.replaceAll("livingdoc-group", "livingdoc-group-not-rendered");
            content = content.replaceAll("livingdoc-historic", "livingdoc-historic-not-rendered");
            content = content.replaceAll(LivingDocPage.MACRO_KEY, "livingdoc-page-not-rendered");
            content = StringUtils.replacePattern(content, "<ac:structured-macro ac:macro-id=\"(.{5,50}?)\" ac:name=\"jira\"(.*?)</ac:structured-macro>", "<span>Jira macro removed</span>");

            // This macro breaks the labels/children macro with Javascript error
            // "treeRequests not defined"
            content = content.replaceAll("\\{pagetree", "{pagetree-not-rendered");

            basicRenderedPage.append(ldUtil.getViewRenderer().render(content,
                    new DefaultConversionContext(page.toPageContext())));

            if (includeStyle) {
                basicRenderedPage.append("\n</div>");
            }

            basicRenderedPage.append("\n</body>\n</html>");

            HtmlEntityEscapeUtil.unEscapeHtmlEntities(basicRenderedPage);

            return basicRenderedPage.toString();

        } catch (LivingDocServerException e) {
            return e.getId().equals(LivingDocConfluenceManager.NEVER_IMPLEMENTED) ? warning(e.getId()) : error(e.getId());
        }
    }

    private List<?> getSpecificationHierarchy(Space space) {
        DocumentNode hierarchy = new DocumentNode(space.getName());
        List<Page> pages = ldUtil.getPageManager().getPages(space, true);
        for (Page page : pages) {
            if (page.isRootLevel()) {
                DocumentNode node = buildNodeHierarchy(page, ldUtil);
                hierarchy.addChildren(node);
            }
        }

        return hierarchy.marshallize();
    }

    private DocumentNode buildNodeHierarchy(Page page, LivingDocConfluenceManager util) {
        DocumentNode node = new DocumentNode(page.getTitle());
        node.setCanBeImplemented(util.canBeImplemented(page));

        List<Page> children = page.getChildren();
        for (Page child : children) {
            node.addChildren(buildNodeHierarchy(child, util));
        }

        return node;
    }

    private String error(String errorId) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("  <table style=\"text-align:center; border:1px solid #cc0000; border-spacing:0px; background-color:#ffcccc; padding:0px; margin:5px; width:100%;\">");
        sb.append("    <tr style=\"display:none\"><td>Comment</td></tr>");
        sb.append(
                "    <tr><td id=\"conf_actionError_Msg\" style=\"color:#cc0000; font-size:12px; font-family:Arial, sans-serif; text-align:center; font-weight:bold;\">")
                .append(ldUtil.getText(errorId)).append("</td></tr>");
        sb.append("  </table>");
        sb.append("</html>");

        return sb.toString();
    }

    private String warning(String errorId) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("  <table style=\"text-align:center; border:1px solid #FFD700; border-spacing:0px; background-color:#FFFF66; padding:0px; margin:5px; width:100%;\">");
        sb.append("    <tr style=\"display:none\"><td>Comment</td></tr>");
        sb.append(
                "    <tr><td id=\"conf_actionWarn_Msg\" style=\"font-size:12px; font-family:Arial, sans-serif; text-align:center; font-weight:bold;\">")
                .append(ldUtil.getText(errorId)).append("</td></tr>");
        sb.append("  </table>");
        sb.append("</html>");

        return sb.toString();
    }
}
