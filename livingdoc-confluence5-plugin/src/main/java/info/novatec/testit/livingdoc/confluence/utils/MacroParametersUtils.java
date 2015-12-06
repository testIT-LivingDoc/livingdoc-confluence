package info.novatec.testit.livingdoc.confluence.utils;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.atlassian.renderer.v2.components.HtmlEscaper;


public class MacroParametersUtils {

    public static String extractParameter(String name, Map< ? , ? > parameters) {
        Object value = parameters.get(name);
        return ( value != null ) ? xssEscape(value.toString()) : "";
    }

    private static String xssEscape(String value) {
        return HtmlEscaper.escapeAll(value, true);
    }

    public static String[] extractParameterMultiple(String name, Map< ? , ? > parameters) {
        String paramValues = extractParameter(name, parameters);
        return StringUtils.split(paramValues, ", ");
    }
}
