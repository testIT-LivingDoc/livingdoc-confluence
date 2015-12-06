package info.novatec.testit.livingdoc.confluence.tools;

import static info.novatec.testit.livingdoc.util.URIUtil.decoded;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcClientLite;
import org.apache.xmlrpc.XmlRpcException;

import info.novatec.testit.livingdoc.LivingDocCore;
import info.novatec.testit.livingdoc.util.cli.ArgumentMissingException;
import info.novatec.testit.livingdoc.util.cli.CommandLine;
import info.novatec.testit.livingdoc.util.cli.ParseException;


public class ListRepo {

    public static final String URI = "http://localhost:8090";
    public static final String RPC_PATH = "/rpc/xmlrpc";
    @SuppressWarnings("rawtypes")
    private static final Vector EMPTY = new Vector(0);

    private final CommandLine cli;

    public ListRepo() {
        this.cli = new CommandLine();
    }

    public void run(String... args) throws Exception {
        defineCommandLine();

        if ( ! parseCommandLine(args))
            return;

        listRepositories();
    }

    private boolean parseCommandLine(String[] args) throws ParseException {
        cli.parse(args);
        if (optionSpecified("help"))
            return displayUsage();
        if (optionSpecified("version"))
            return displayVersion();
        if (confluenceUrl() == null)
            throw new ArgumentMissingException("confluence_url");
        return true;
    }

    private String confluenceUrl() {
        return cli.getArgument(0) != null ? decoded(cli.getArgument(0)) : null;
    }

    private boolean optionSpecified(String name) {
        return cli.hasOptionValue(name);
    }

    @SuppressWarnings("rawtypes")
    private void listRepositories() {
        try {
            String xmlRpcUrl = getXmlRpcURL();
            System.out.println("Dumping repo list for : " + xmlRpcUrl);

            XmlRpcClient rpcClient = new XmlRpcClientLite(xmlRpcUrl);

            Vector repositories = ( Vector ) rpcClient.execute(buildRequest("getAllSpecificationRepositories"), EMPTY);
            System.out.println(repositories.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (XmlRpcException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getXmlRpcURL() {
        return confluenceUrl() + RPC_PATH;
    }

    private void defineCommandLine() {
        String banner = "info.novatec.testit.livingdoc.confluence.tools.ListRepo [options] confluence_url\n"
            + "List all the repositories found in the specified confluence ";
        cli.setBanner(banner);

        cli.defineOption(cli.buildOption("help", "--help", "Display this help and exit"));
        cli.defineOption(cli.buildOption("version", "--version", "Output version information and exit"));
    }

    private boolean displayVersion() {
        System.out.println(String.format("LivingDoc version \"%s\"", LivingDocCore.VERSION));
        return false;
    }

    private boolean displayUsage() {
        System.out.println(cli.usage());
        return false;
    }

    private static String buildRequest(String methodName) {
        return "livingdoc1." + methodName;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        ListRepo listRepo = new ListRepo();
        try {
            listRepo.run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
