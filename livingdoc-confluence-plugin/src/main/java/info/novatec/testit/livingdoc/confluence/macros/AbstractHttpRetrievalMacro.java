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

import info.novatec.testit.livingdoc.server.LivingDocServerErrorKey;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.Repository;
import info.novatec.testit.livingdoc.server.rpc.xmlrpc.XmlRpcDataMarshaller;
import info.novatec.testit.livingdoc.server.rpc.xmlrpc.XmlRpcMethodName;
import info.novatec.testit.livingdoc.util.CollectionUtil;

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
import com.atlassian.renderer.v2.macro.MacroException;


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
            String cmdLine =
                new StringBuffer(handler).append(".").append(XmlRpcMethodName.getRegisteredRepository).toString();
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
