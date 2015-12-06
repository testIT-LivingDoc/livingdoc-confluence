package info.novatec.testit.livingdoc.confluence.velocity.doctheme;

public interface ThemeService {
    void storeThemeData(String spaceKey, Settings settings);

    Settings retrieveThemeData(String spaceKey);
}
