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
package info.novatec.testit.livingdoc.confluence.rpc.xmlrpc;

import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcClientLite;


public class LivingDocServerDebug {

    public static final String URI = "http://localhost:8090";
    public static final String RPC_PATH = "/rpc/xmlrpc";
    public static final String USER_NAME = "admin";
    public static final String PASSWORD = "admin";

    // private static final Vector EMPTY = new Vector(0);

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        XmlRpcClient rpcClient = new XmlRpcClientLite(URI + RPC_PATH);

        // Hit the projectDao
        // Vector projects =
        // (Vector)rpcClient.execute(buildRequest("getAllProjects"), EMPTY);
        // System.err.println(projects.toString());
        //
        // //Hit the sutDao
        // Vector runners =
        // (Vector)rpcClient.execute(buildRequest("getAllRunners"), EMPTY);
        // System.err.println(runners.toString());
        //
        // //Hit the repositoryDao
        // Vector repositories =
        // (Vector)rpcClient.execute(buildRequest("getAllSpecificationRepositories"),
        // EMPTY);
        // System.err.println(repositories.toString());

        // Hit the documentDao
        /* @todo : */

        // Hit the old ConfluenceRpc
        Vector params = new Vector(3);
        params.add(USER_NAME);
        params.add(PASSWORD);

        Vector specs = new Vector(2);
        specs.add("LIVINGDOCDEMO");
        specs.add("Bank");
        specs.add(Boolean.TRUE);
        params.add(specs);
        String specification = ( String ) rpcClient.execute(buildRequest("getRenderedSpecification"), params);
        System.err.println(specification);

        // String result =
        // (String)rpcClient.execute(buildRequest("setSpecificationAsImplemented"),
        // params);
        // System.err.println(result);
        //
        // params.clear();
        // params.add(USER_NAME);
        // params.add(PASSWORD);
        //
        // specs.clear();
        // specs.add("LIVINGDOC");
        // params.add(specs);
        // Vector specifications =
        // (Vector)rpcClient.execute(buildRequest("getSpecificationHierarchy"),
        // params);
        // System.err.println(specifications);
    }

    private static String buildRequest(String methodName) {
        return "livingdoc1." + methodName;
    }
}
