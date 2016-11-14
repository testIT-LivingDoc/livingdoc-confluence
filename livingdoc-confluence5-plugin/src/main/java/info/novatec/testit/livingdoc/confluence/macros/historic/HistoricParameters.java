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
package info.novatec.testit.livingdoc.confluence.macros.historic;

import java.util.List;
import java.util.Map;

import com.atlassian.confluence.pages.Page;

import info.novatec.testit.livingdoc.confluence.StaticAccessor;
import info.novatec.testit.livingdoc.confluence.velocity.ConfluenceLivingDoc;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.Repository;
import info.novatec.testit.livingdoc.server.domain.SystemUnderTest;


public class HistoricParameters {

    public static final String PAGETITLE = "pagetitle";
    public static final String SPACEKEY = "spacekey";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String BORDER = "border";
    public static final String CHILDREN = "children";
    public static final String MAXRESULT = "maxresult";
    public static final String SUT = "sut";
    public static final String SHOWIGNORED = "showignored";
    public static final String TITLE = "title";
    public static final String SUBTITLE = "subtitle";
    public static final String SUBTITLE2 = "subtitle2";
    public static final String LABELS = "labels";
    public static final String POPUP_WIDTH = "popupwidth";
    public static final String POPUP_HEIGHT = "popupheight";

    public static final int DEFAULT_WIDTH = 500;
    public static final int DEFAULT_HEIGHT = 500;
    public static final int DEFAULT_MAXRESULT = 30;
    public static final int DEFAULT_POPUP_WIDTH = 800;
    public static final int DEFAULT_POPUP_HEIGHT = 600;

    public enum Children {
        None, First, All;

        public static Children toChildren(String id) {
            if (id.equalsIgnoreCase("First")) {
                return First;
            } else if (id.equalsIgnoreCase("All")) {
                return All;
            } else {
                return None;
            }
        }
    }

    private final ConfluenceLivingDoc ldUtil = StaticAccessor.getConfluenceLivingDoc();

    private final String spaceKey;
    private final Page page;
    private final String executionUID;
    private final int width;
    private final int height;
    private final boolean border;
    private final Children children;
    private final int maxResult;
    private final SystemUnderTest targetedSystemUnderTest;
    private final boolean showIgnored;
    private final String title;
    private final String subTitle;
    private final String subTitle2;
    private final String labels;
    private final int popupWidth;
    private final int popupHeight;

    public HistoricParameters(Map< ? , ? > parameters, String spaceKey, Page page, String executionUID)
        throws LivingDocServerException {
        this.spaceKey = spaceKey;
        this.page = page;
        this.executionUID = executionUID;

        width = getParameters(parameters, WIDTH, DEFAULT_WIDTH);
        height = getParameters(parameters, HEIGHT, DEFAULT_HEIGHT);
        border = getParameters(parameters, BORDER, false);

        children = getParameters(parameters, CHILDREN, Children.None);

        maxResult = getParameters(parameters, MAXRESULT, DEFAULT_MAXRESULT);
        targetedSystemUnderTest = getSutParameter(parameters, SUT, page);
        showIgnored = getParameters(parameters, SHOWIGNORED, false);

        title = getParameters(parameters, TITLE, ( String ) null);
        subTitle = getParameters(parameters, SUBTITLE, ( String ) null);
        subTitle2 = getParameters(parameters, SUBTITLE2, ( String ) null);

        labels = getParameters(parameters, LABELS, ( String ) null);

        popupWidth = getParameters(parameters, POPUP_WIDTH, isNoChildren() ? DEFAULT_POPUP_WIDTH : width);
        popupHeight = getParameters(parameters, POPUP_HEIGHT, isNoChildren() ? DEFAULT_POPUP_HEIGHT : height);
    }

    public Page getPage() {
        return page;
    }

    public String getSpaceKey() {
        return spaceKey;
    }

    public String getExecutionUID() {
        return executionUID;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isBorder() {
        return border;
    }

    public Children getChildren() {
        return children;
    }

    public boolean isNoChildren() {
        return Children.None == children;
    }

    public boolean isAllChildren() {
        return Children.All == children;
    }

    public boolean isFirstChildren() {
        return Children.First == children;
    }

    public int getMaxResult() {
        return maxResult;
    }

    public SystemUnderTest getTargetedSystemUnderTest() {
        return targetedSystemUnderTest;
    }

    public String getSut() {
        return getTargetedSystemUnderTest().getName();
    }

    public boolean isShowIgnored() {
        return showIgnored;
    }

    public boolean getIsShowIgnored() {
        return isShowIgnored();
    }

    public String getTitle() {
        return title == null ? getDefaultTitle() : title;
    }

    public String getSubTitle() {
        return subTitle == null ? getDefaultSubTitle() : subTitle;
    }

    public String getSubTitle2() {
        return subTitle2 == null ? getDefaultSubTitle2() : subTitle2;
    }

    public String getLabels() {
        return labels;
    }

    public int getPopupHeight() {
        return popupHeight;
    }

    public int getPopupWidth() {
        return popupWidth;
    }

    private String getDefaultTitle() {
        return ldUtil.getText("livingdoc.historic.chart.title");
    }

    private String getDefaultSubTitle() {
        StringBuilder subTitle = new StringBuilder();

        if (getLabels() != null) {
            subTitle.append(ldUtil.getText("livingdoc.execution.forlabels")).append(' ').append(ldUtil.getText(
                "livingdoc.execution.openbraket")).append(' ').append(getLabels()).append(' ').append(ldUtil.getText(
                    "livingdoc.execution.closebraket")).append(' ').append(ldUtil.getText("livingdocepper.execution.and"))
                .append(' ');
        }

        subTitle.append(ldUtil.getText("livingdoc.execution.for"));

        if ( ! isNoChildren()) {
            subTitle.append(isAllChildren() ? ldUtil.getText("livingdoc.children.all") : ldUtil.getText(
                "livingdoc.children.firstlvl")).append(' ').append(ldUtil.getText("livingdoc.children.childrenof")).append(
                    ' ');
        }

        subTitle.append(ldUtil.getText("livingdoc.execution.openbraket")).append(' ').append(getPage().getTitle().trim())
            .append(' ').append(ldUtil.getText("livingdoc.execution.closebraket"));

        subTitle.append(' ').append(ldUtil.getText("livingdoc.execution.on")).append(' ').append(getSpaceKey()).append(' ')
            .append(ldUtil.getText("livingdoc.execution.space"));

        return subTitle.toString();
    }

    private String getDefaultSubTitle2() {
        return ldUtil.getText("livingdoc.historic.chart.subtitle2", getSut(), getMaxResult());
    }

    private String getParameters(Map< ? , ? > parameters, String parameterName, String defaultValue) {
        String value = ( String ) parameters.get(parameterName);
        return value == null ? defaultValue : value;
    }

    private int getParameters(Map< ? , ? > parameters, String parameterName, int defaultValue) {
        String value = ( String ) parameters.get(parameterName);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    private boolean getParameters(Map< ? , ? > parameters, String parameterName, boolean defaultValue) {
        String value = ( String ) parameters.get(parameterName);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    private Children getParameters(Map< ? , ? > parameters, String parameterName, Children defaultValue) {
        String value = ( String ) parameters.get(parameterName);
        return value == null ? defaultValue : Children.toChildren(value);
    }

    private SystemUnderTest getSutParameter(Map< ? , ? > parameters, String parameterName, Page page)
        throws LivingDocServerException {
        String sut = ( String ) parameters.get(parameterName);

        if (sut == null) {
            if (ldUtil.isExecutable(page)) {
                String selectedSut = ldUtil.getSelectedSystemUnderTestInfo(getPage());
                sut = selectedSut.substring(selectedSut.indexOf('@') + 1);
            } else {
                Repository repository = ldUtil.getHomeRepository(getSpaceKey());

                List<SystemUnderTest> allSuts = ldUtil.getLDServerService().getSystemUnderTestsOfAssociatedProject(repository
                    .getUid());

                for (SystemUnderTest s : allSuts) {
                    if (s.isDefault()) {
                        return s;
                    }
                }
            }
        }

        return findTargetSystemUnderTest(sut, getSpaceKey());
    }

    private SystemUnderTest findTargetSystemUnderTest(String sut, String spaceKey) throws LivingDocServerException {
        SystemUnderTest targetedSystemUnderTest = null;
        List<SystemUnderTest> suts = ldUtil.getSystemsUnderTests(spaceKey);

        for (SystemUnderTest s : suts) {
            if (s.getName().equals(sut)) {
                targetedSystemUnderTest = s;
                break;
            }
        }

        if (targetedSystemUnderTest == null) {
            throw new LivingDocServerException("livingdoc.historic.sutnotinselection", sut);
        }

        return targetedSystemUnderTest;
    }
}
