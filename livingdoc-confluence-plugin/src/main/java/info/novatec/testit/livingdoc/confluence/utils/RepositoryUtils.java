package info.novatec.testit.livingdoc.confluence.utils;

public class RepositoryUtils {

    private static final String BASE_TEST_URL_FORMAT = "%s/rpc/xmlrpc?handler=livingdoc1#%s";
    private static final String REPOSITORY_BASE_URL_FORMAT = "%s/display/%s";

    public static String repositoryBaseUrl(String baseUrl, String spaceKey) {
        return String.format(REPOSITORY_BASE_URL_FORMAT, baseUrl, spaceKey);
    }

    public static String baseTestUrl(String baseUrl, String spaceKey) {
        return String.format(BASE_TEST_URL_FORMAT, baseUrl, spaceKey);
    }
}
