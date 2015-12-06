package info.novatec.testit.livingdoc.confluence.utils;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;


public class MacroParametersUtilsTest {

    @Test
    public void testReturnsEmptyIfNoParameterExist() {
        Map<String, String> parameters = Maps.newHashMap();

        assertEquals("should return empty string if parameter not set", "", MacroParametersUtils.extractParameter(
            "NonExistingParamName", parameters));
        assertEquals("should return empty array if parameter not set", 0, MacroParametersUtils.extractParameterMultiple(
            "NonExistingParamName", parameters).length);
    }

    @Test
    public void testCanRetrieveAParameterByName() {
        Map<String, String> parameters = Maps.newHashMap();

        String paramName = "zeParam";
        String paramValue = "patate";

        parameters.put(paramName, paramValue);

        assertEquals(paramValue, MacroParametersUtils.extractParameter(paramName, parameters));
    }

    @Test
    public void testPreventXSSIssuesInParameter() {
        Map<String, String> parameters = Maps.newHashMap();

        String xssParamName = "xssParam";
        String xssValue = "<patate>";
        String xssSafeValue = "&lt;patate&gt;";

        parameters.put(xssParamName, xssValue);

        assertEquals(xssSafeValue, MacroParametersUtils.extractParameter(xssParamName, parameters));
    }

    @Test
    public void testMultipleValuesAreSeparatedBySpaces() {
        Map<String, String> parameters = Maps.newHashMap();

        String paramName = "zeParam";
        String paramValue1 = "patateUne";
        String paramValue2 = "<patateDeux>";
        String paramValue2XssSafe = "&lt;patateDeux&gt;";

        parameters.put(paramName, paramValue1 + " " + paramValue2);

        String[] values = MacroParametersUtils.extractParameterMultiple(paramName, parameters);
        assertEquals(2, values.length);
        assertEquals(paramValue1, values[0]);
        assertEquals(paramValue2XssSafe, values[1]);
    }

    @Test
    public void testMultipleValuesCanBeSeparatedByComaTo() {
        Map<String, String> parameters = Maps.newHashMap();

        String paramName = "zeParam";
        String paramValue1 = "un";
        String paramValue2 = "deux";
        String paramValue3 = "trois";

        parameters.put(paramName, paramValue1 + "," + paramValue2 + "," + paramValue3);

        String[] values = MacroParametersUtils.extractParameterMultiple(paramName, parameters);
        assertEquals(3, values.length);
        assertEquals(paramValue1, values[0]);
        assertEquals(paramValue2, values[1]);
        assertEquals(paramValue3, values[2]);
    }

    @Test
    public void testMultipleSeparatorsAreIgnored() {
        Map<String, String> parameters = Maps.newHashMap();

        String paramName = "zeParam";
        String paramValue1 = "un";
        String paramValue2 = "deux";
        String paramValue3 = "trois";
        String paramValue4 = "quatre";

        parameters.put(paramName, paramValue1 + ",," + paramValue2 + " ," + paramValue3 + "   " + paramValue4);

        String[] values = MacroParametersUtils.extractParameterMultiple(paramName, parameters);
        assertEquals(4, values.length);
        assertEquals(paramValue1, values[0]);
        assertEquals(paramValue2, values[1]);
        assertEquals(paramValue3, values[2]);
        assertEquals(paramValue4, values[3]);
    }
}
