package info.novatec.testit.livingdoc.confluence.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RepositoryUtilsTest {

    @Test
    public void testRepositoryBaseUrl() {
        final String repositoryBaseUrl = "/display/LIVINGDOC";
        assertEquals(repositoryBaseUrl, RepositoryUtils.repositoryBaseUrl("", "LIVINGDOC"));
    }

    @Test
    public void testBaseTestUrl() {
        final String baseTestUrl = "?#LIVINGDOC";
        assertEquals(baseTestUrl, RepositoryUtils.baseTestUrl("", "LIVINGDOC"));
    }
}
