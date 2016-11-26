package info.novatec.testit.livingdoc.confluence.utils;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;


public class HtmlUtilsTest {
    private String emptyTextArea = "";
    private SortedSet<String> elementSet = new TreeSet<String>();

    @Test
    public void testEmptySetGivesEmptyTextArea() {
        assertEquals(emptyTextArea, HtmlUtils.stringSetToTextArea(new HashSet<String>()));
        assertEquals(emptyTextArea, HtmlUtils.stringSetToTextArea(null));
    }

    @Test
    public void testOneElementInSetGivesOneElementInTextArea() {
        givenElement("foo");
        assertEquals("foo", HtmlUtils.stringSetToTextArea(elementSet));
    }

    @Test
    public void testMultipleElementsInSetGivesElementsInTextAreaSeparatedByLF() {
        givenElement("foo");
        givenElement("bar");
        givenElement("toto");
        assertEquals("bar\nfoo\ntoto", HtmlUtils.stringSetToTextArea(elementSet));
    }

    private void givenElement(String element) {
        elementSet.add(element);
    }
}
