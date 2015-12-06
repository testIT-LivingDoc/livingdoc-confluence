package info.novatec.testit.livingdoc.confluence.demo.calculator;

public class TableCounter {
    private int count = 1;

    public static TableCounter INSTANCE = new TableCounter();

    public void incCounter() {
        setCount(getCount() + 1);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
