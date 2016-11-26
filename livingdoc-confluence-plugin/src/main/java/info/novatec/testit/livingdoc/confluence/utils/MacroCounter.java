package info.novatec.testit.livingdoc.confluence.utils;

public class MacroCounter {
    private static MacroCounter labelCounter = new MacroCounter();
    private static int counter = 0;

    private MacroCounter() {
    }

    public static MacroCounter instance() {
        return labelCounter;
    }

    public synchronized int getNextCount() {
        if (counter > 999) {
            counter = 0;
        }

        return counter ++ ;
    }
}
