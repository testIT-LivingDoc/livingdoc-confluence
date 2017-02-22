/* Copyright (c) 2008 Pyxis Technologies inc.
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
 * http://www.fsf.org. */
package info.novatec.testit.livingdoc.confluence.rpc;

import info.novatec.testit.livingdoc.confluence.rest.LivingDocRestServiceImpl;
import info.novatec.testit.livingdoc.server.rest.LivingDocRestHelper;
import info.novatec.testit.livingdoc.server.rpc.RpcServerService;

/**
 * @deprecated The XML-RPC and SOAP APIs are deprecated since Confluence 5.5.
 * More info <a href="https://developer.atlassian.com/confdev/deprecated-apis/confluence-xml-rpc-and-soap-apis">here</a>
 * <br> Use {@link LivingDocRestServiceImpl} instead.
 */
@Deprecated
public interface RpcClientService extends RpcServerService, LivingDocRestHelper {

}
