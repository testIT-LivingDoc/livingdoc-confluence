/**
 * Copyright (c) 2008 Pyxis Technologies inc.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */
package info.novatec.testit.livingdoc.confluence.actions.execution;

import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;

import info.novatec.testit.livingdoc.confluence.StaticAccessor;
import info.novatec.testit.livingdoc.confluence.actions.AbstractLivingDocAction;
import info.novatec.testit.livingdoc.confluence.utils.stylesheet.StyleSheetExtractor;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.Execution;


@SuppressWarnings("serial")
public class ShowExecutionResultAction extends AbstractLivingDocAction {
    private final StyleSheetExtractor styleSheetExtractor = StaticAccessor.getStyleSheetExtractor();

    private Long id;
    private Execution execution;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Execution getExecution() {
        return execution;
    }

    public void setExecution(Execution execution) {
        this.execution = execution;
    }

    @HtmlSafe
    public String getTitleHtml() {
        return getTitle(false);
    }

    @HtmlSafe
    public String getTitleWithAnchorHtml() {
        return getTitle(true);
    }

    private String getTitle(boolean useAnchor) {
        if (execution == null)
            return "";

        StringBuilder title = new StringBuilder();

        title.append(useAnchor ? getTitleAnchor() : execution.getSpecification().getName()).append(' ').append(confluenceLivingDoc
            .getText("livingdoc.execution.for")).append(' ').append(confluenceLivingDoc.getText("livingdoc.execution.openbraket")).append(
                execution.getSystemUnderTest().getName()).append(' ').append(confluenceLivingDoc.getText(
                    "livingdoc.execution.closebraket")).append(" - ").append(getDateFormatter().formatDateTime(execution
                        .getExecutionDate()));

        return title.toString();
    }

    private String getTitleAnchor() {
        String resolvedName = null;

        resolvedName = execution.getSpecification().getResolvedName();

        return resolvedName == null ? execution.getSpecification().getName() : String.format(
            "<a href=\"%s\" alt=\"\">%s</a>", resolvedName, execution.getSpecification().getName());
    }

    @HtmlSafe
    public String getStylesheetHtml() {
        Space space = confluenceLivingDoc.getSpaceManager().getSpace(getSpaceKey());
        return String.format("<style>\n%s\n</style>\n<base href=\"%s\"/>\n", styleSheetExtractor.renderStyleSheet(space),
            confluenceLivingDoc.getBaseUrl());
    }

    public boolean getHasException() {
        return execution != null && execution.hasException();
    }

    @HtmlSafe
    public String getExceptionHtml() {
        return execution.getExecutionErrorId();
    }

    public boolean getHasBody() {
        return execution != null && StringUtils.isNotEmpty(execution.getResults());
    }

    @HtmlSafe
    public String getBodyHtml() {
        String body = execution.getResults().trim();

        return body.replaceAll("<html>", "").replaceAll("</html>", "");
    }

    public boolean getHasSections() {
        return execution != null && StringUtils.isNotEmpty(execution.getSections());
    }

    @HtmlSafe
    public String getSectionsHtml() {
        return String.format("%s : %s", confluenceLivingDoc.getText("livingdoc.page.sections"), execution.getSections());
    }

    public String show() {
        try {
            execution = confluenceLivingDoc.getLDServerService().getSpecificationExecution(getId());
        } catch (LivingDocServerException e) {
            addActionError(e.getId());
        }

        return SUCCESS;
    }
}
