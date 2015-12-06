package info.novatec.testit.livingdoc.confluence.rpc.xmlrpc;

import java.io.PrintStream;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;

import info.novatec.testit.livingdoc.server.domain.DocumentNode;
import info.novatec.testit.livingdoc.server.rpc.xmlrpc.XmlRpcDataMarshaller;


public class LivingDocConfluenceDebug {
    private static final String URL = "http://localhost:8090/rpc/xmlrpc";
    private static final String HANDLER = "livingdoc1";

    private void testGetHtmlPageBody() {
        try {
            XmlRpcClient xmlrpc = new XmlRpcClient(URL);
            String test = ( String ) xmlrpc.execute(HANDLER + ".getHtmlPageBody", makeParams("THE", "Home", true));
            // inputStream = new ByteArrayInputStream(test.getBytes());
            System.out.println(test);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @SuppressWarnings("unused")
    private void testGetSpaceDetails() {
        try {
            XmlRpcClient xmlrpc = new XmlRpcClient(URL);
            Vector< ? > vector = ( Vector< ? > ) xmlrpc.execute(HANDLER + ".getSpecificationHierarchy", makeParams("THE"));
            DocumentNode hierarchy = XmlRpcDataMarshaller.toDocumentNode(vector);

            printHierarchy(0, hierarchy, System.out);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void printHierarchy(int level, DocumentNode node, PrintStream out) {
        ident(level, out);
        out.println(node.getTitle());

        for (DocumentNode child : node.getChildren()) {
            printHierarchy(level + 1, child, out);
        }
    }

    private void ident(int level, PrintStream out) {
        for (int i = 0; i < level; i ++ ) {
            out.print("   ");
        }
    }

    private static Vector<Object> makeParams(Object... myParams) {
        Vector<Object> params = new Vector<Object>();
        for (Object param : myParams) {
            params.add(param);
        }
        return params;
    }

    public static void main(String[] args) {
        LivingDocConfluenceDebug debug = new LivingDocConfluenceDebug();

        debug.testGetHtmlPageBody();
    }

}
