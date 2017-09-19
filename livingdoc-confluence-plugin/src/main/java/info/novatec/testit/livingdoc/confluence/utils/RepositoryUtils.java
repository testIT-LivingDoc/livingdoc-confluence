package info.novatec.testit.livingdoc.confluence.utils;

import info.novatec.testit.livingdoc.server.rpc.RpcServerService;

public class RepositoryUtils {

    private static final String BASE_TEST_URL_FORMAT = "%s/rpc/xmlrpc?handler=%s#%s";
    private static final String REPOSITORY_BASE_URL_FORMAT = "%s/display/%s";

    public static String repositoryBaseUrl(String baseUrl, String spaceKey) {
        return String.format(REPOSITORY_BASE_URL_FORMAT, baseUrl, spaceKey);
    }

    public static String baseTestUrl(String baseUrl, String spaceKey) {
        return String.format(BASE_TEST_URL_FORMAT, baseUrl,
                RpcServerService.SERVICE_HANDLER, spaceKey);
    }
}
