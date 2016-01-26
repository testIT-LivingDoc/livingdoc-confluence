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

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;

import info.novatec.testit.livingdoc.confluence.actions.AbstractLivingDocAction;
import info.novatec.testit.livingdoc.confluence.macros.historic.HistoricParameters;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.Specification;


@SuppressWarnings("serial")
public class ChildrenExecutionResultAction extends AbstractLivingDocAction {

    private Long id;
    private Specification specification;
    private String spaceKey;
    private int width;
    private int height;
    private String sut;
    private boolean showIgnored;
    private int maxResult;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setSut(String sut) {
        this.sut = sut;
    }

    public void setShowIgnored(boolean showIgnored) {
        this.showIgnored = showIgnored;
    }

    public void setMaxResult(int maxResult) {
        this.maxResult = maxResult;
    }

    public String getPageTitle() {
        if (specification == null)
            return "";
        return specification.getName();
    }

    public String show() {
        try {
            specification = confluenceLivingDoc.getLDServerService().getSpecificationById(getId());
        } catch (LivingDocServerException e) {
            addActionError(e.getId());
        }

        return SUCCESS;
    }

    @HtmlSafe
    public String getRenderedContent() {
        Page page = confluenceLivingDoc.getPageManager().getPage(spaceKey, getPageTitle());
        if (page == null)
            return confluenceLivingDoc.getText("livingdoc.children.pagenotfound");

        StringBuilder content = new StringBuilder("{livingdoc-historic:");

        content.append(HistoricParameters.SPACEKEY).append("=").append(spaceKey).append("|").append(
            HistoricParameters.PAGETITLE).append("=").append(page.getTitle().trim()).append("|").append(
                HistoricParameters.WIDTH).append("=").append(width).append("|").append(HistoricParameters.HEIGHT).append("=")
            .append(height).append("|").append(HistoricParameters.SUT).append("=").append(sut).append("|").append(
                HistoricParameters.SHOWIGNORED).append("=").append(showIgnored).append("|").append(
                    HistoricParameters.MAXRESULT).append("=").append(maxResult);

        content.append("}");

        // return
        // confluenceLivingDoc.getWikiStyleRenderer().convertWikiToXHtml(page.toPageContext(),
        // content.toString());
        return confluenceLivingDoc.getViewRenderer().render(page);
    }
}
