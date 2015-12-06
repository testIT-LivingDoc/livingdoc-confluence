package info.novatec.testit.livingdoc.confluence.utils;

import java.util.Set;


public class HtmlUtils {

    private final static String TEXTAREA_ELEMENTS_SEPARATOR = "\n";

    public static String stringSetToTextArea(Set<String> elements) {
        StringBuilder sb = new StringBuilder();
        if (elements != null) {
            for (String element : elements) {
                sb.append(element);
                sb.append(TEXTAREA_ELEMENTS_SEPARATOR);
            }
        }
        int length = sb.length();
        return sb.substring(0, length > 0 ? length - 1 : 0);
    }
}
