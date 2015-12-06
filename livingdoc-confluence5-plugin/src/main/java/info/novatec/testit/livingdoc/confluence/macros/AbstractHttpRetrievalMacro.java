/**
 * Copyright (c) 2009 Pyxis Technologies inc.
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
package info.novatec.testit.livingdoc.confluence.macros;

import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.renderer.v2.macros.BaseHttpRetrievalMacro;
import com.atlassian.confluence.util.http.HttpResponse;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;

import info.novatec.testit.livingdoc.server.LivingDocServerErrorKey;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.Repository;
import info.novatec.testit.livingdoc.server.rpc.xmlrpc.XmlRpcDataMarshaller;
import info.novatec.testit.livingdoc.server.rpc.xmlrpc.XmlRpcMethodName;
import info.novatec.testit.livingdoc.util.CollectionUtil;


public abstract class AbstractHttpRetrievalMacro extends BaseHttpRetrievalMacro implements Macro {
    private final Logger log = LoggerFactory.getLogger(AbstractHttpRetrievalMacro.class);

    public AbstractHttpRetrievalMacro() {
        super();
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

    @SuppressWarnings("rawtypes")
    @Override
    public String successfulResponse(Map parameters, RenderContext context, String url, HttpResponse response)
        throws MacroException {
        // InputStream is = null;

        // try
        // {
        // is = response.getResponse();
        // return IOUtils.toString(is);
        return "BYOB";
        // return
        // "<link rel=\"stylesheet\"
        // href=\"/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/css/jira-livingdoc.css\"
        // type=\"text/css\"/><script language=\"JavaScript\"
        // type=\"text/javascript\"
        // src=\"/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/js/pyxis-util-1.1.js\"></script><script
        // language=\"JavaScript\" type=\"text/javascript\"
        // src=\"/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/js/statusbar-2.8m2-SNAPSHOT.js\"></script><script
        // language=\"JavaScript\" type=\"text/javascript\"
        // src=\"/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/js/JiraActions-2.8m2-SNAPSHOT.js\"></script><script
        // language=\"JavaScript\" type=\"text/javascript\"
        // src=\"/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/js/jira-livingdoc-2.8m2-SNAPSHOT.js\"></script><table
        // class=\"jira_bulkExtern\"
        // id=\"jira_bulkManageView_LivingDoc_Jira-BANK_ALL\"> <tr
        // style=\"display:none\"><td>Begin Info</td></tr> <tr> <td
        // align=\"center\"> <span class=\"ldTitle\"> LivingDoc Bulk References
        // management for ALL Versions of Project Bank (For demo) </span> </td>
        // </tr> <tr> <td align=\"center\" class=\"jira_bulkLinkAll\"> <span
        // id=\"jira_bulk_link_all_LivingDoc_Jira-BANK_ALL\"> <a
        // href=\"#jira_bulkManageView_LivingDoc_Jira-BANK_ALL\"
        // onclick=\"livingDoc.getBulk('LivingDoc_Jira-BANK_ALL').runAllIssues();\">Run
        // all Issues References</a> | <a
        // href=\"#jira_bulkManageView_LivingDoc_Jira-BANK_ALL\"
        // onclick=\"livingDoc.getBulk('LivingDoc_Jira-BANK_ALL').showAllIssues();\">View
        // all Issues results</a> | <a
        // href=\"#jira_bulkManageView_LivingDoc_Jira-BANK_ALL\"
        // onclick=\"livingDoc.getBulk('LivingDoc_Jira-BANK_ALL').hideAllIssues();\">Hide
        // all Issues results</a> </span> <span
        // id=\"jira_bulk_link_all_shadow_LivingDoc_Jira-BANK_ALL\"
        // style=\"display:none\"> Run all Issues References | View all Issues
        // results | Hide all Issues results </span> </td> </tr>
        // <tr><td>&nbsp;</td></tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-68_BANK-68\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-68', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-68');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-68').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-38_BANK-38\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-38', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-38');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-38').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-50_BANK-50\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-50', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-50');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-50').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-35_BANK-35\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-35', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-35');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-35').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-78_BANK-78\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-78', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-78');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-78').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-82_BANK-82\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-82', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-82');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-82').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-74_BANK-74\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-74', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-74');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-74').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-33_BANK-33\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-33', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-33');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-33').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-16_BANK-16\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-16', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-16');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-16').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-57_BANK-57\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-57', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-57');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-57').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-77_BANK-77\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-77', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-77');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-77').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-37_BANK-37\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-37', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-37');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-37').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-34_BANK-34\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-34', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-34');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-34').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-18_BANK-18\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-18', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-18');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-18').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-30_BANK-30\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-30', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-30');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-30').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-75_BANK-75\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-75', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-75');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-75').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-55_BANK-55\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-55', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-55');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-55').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-24_BANK-24\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-24', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-24');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-24').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-71_BANK-71\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-71', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-71');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-71').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-39_BANK-39\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-39', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-39');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-39').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-67_BANK-67\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-67', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-67');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-67').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-73_BANK-73\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-73', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-73');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-73').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-76_BANK-76\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-76', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-76');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-76').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-69_BANK-69\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-69', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-69');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-69').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-51_BANK-51\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-51', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-51');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-51').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-6_BANK-6\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-6', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-6');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-6').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-70_BANK-70\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-70', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-70');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-70').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-36_BANK-36\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-36', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-36');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-36').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-56_BANK-56\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-56', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-56');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-56').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-20_BANK-20\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-20', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-20');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-20').loadReferencesPane();\");</script> </tr> <tr> <td
        // id=\"jira_referencesPane_display_LivingDoc_Jira-BANK_ALL_BANK-72_BANK-72\"
        // style=\"padding:5px; width:100%\"></td> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingDoc.registerList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-72', '/jira', 'bulkView',
        // '/jira/download/resources/info.novatec.testit.livingdoc.jira.plugin:livingdoc.webactions/images/',
        // JiraLD.actions, 'BANK-72');\");</script> <script
        // language=\"JavaScript\"
        // type=\"text/javascript\">addLoadEvent(\"livingdoc.getList('LivingDoc_Jira-BANK_ALL',
        // 'BANK-72').loadReferencesPane();\");</script> </tr> </table><table
        // style=\"display:none\"><tr><td>End Info</td></tr></table>";
        // }
        // catch (IOException ex)
        // {
        // throw new MacroException(ex);
        // }
        // finally
        // {
        // IOUtils.closeQuietly(is);
        // }
    }

    @SuppressWarnings("rawtypes")
    protected String getParameter(Map parameters, String name) {
        String parameter = ( String ) parameters.get(name);
        return StringUtils.isBlank(parameter) ? null : parameter.trim();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Repository getRepository(String url, String handler, String jiraUid) throws LivingDocServerException {
        Vector<Object> response;

        try {
            Repository repository = Repository.newInstance(jiraUid);
            Vector params = CollectionUtil.toVector(repository.marshallize());
            XmlRpcClient xmlrpc = new XmlRpcClient(url + "/rpc/xmlrpc");
            String cmdLine = new StringBuffer(handler).append(".").append(XmlRpcMethodName.getRegisteredRepository)
                .toString();
            response = ( Vector<Object> ) xmlrpc.execute(cmdLine, params);
        } catch (XmlRpcException e) {
            log.error(e.getMessage(), e);
            throw new LivingDocServerException(LivingDocServerErrorKey.CALL_FAILED, e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new LivingDocServerException(LivingDocServerErrorKey.CONFIGURATION_ERROR, e.getMessage());
        }

        XmlRpcDataMarshaller.checkForErrors(response);

        return XmlRpcDataMarshaller.toRepository(response);
    }
}
