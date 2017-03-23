package info.novatec.testit.livingdoc.confluence.demo.calculator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import info.novatec.testit.livingdoc.reflect.AfterTable;
import info.novatec.testit.livingdoc.reflect.BeforeTable;
import info.novatec.testit.livingdoc.reflect.annotation.Alias;
import info.novatec.testit.livingdoc.reflect.annotation.FixtureClass;


@FixtureClass
public class BeforeAfterCalculator {
    private int x;
    private int y;
    private Long now;
    private TableCounter tablecount = TableCounter.INSTANCE;

    @BeforeTable
    public void getStartTime() {
        now = System.currentTimeMillis();
    }

    @AfterTable
    public void countUP() {
        tablecount.incCounter();
    }

    public int getCount() {
        return tablecount.getCount();
    }

    public String starttime() {
        Date date = new Date(now);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
        return formatter.format(date);
    }

    public int getY() {
        return y;
    }

    @Alias({ "variable2", "var2", "second variable" })
    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    @Alias({ "variable1", "var1", "first variable" })
    public void setX(int x) {
        this.x = x;
    }

    @Alias({ "addition", "plus" })
    public int sum() {
        return x + y;
    }

    @Alias({ "subtraction", "minus" })
    public int difference() {
        return x - y;
    }

    @Alias({ "multiplication", "multiply" })
    public int product() {
        return x * y;
    }

    @Alias({ "division", "divide" })
    public int quotient() {
        return x / y;
    }

    /**
     * This method can be called using a new table at the end of a
     * specification.
     * 
     * Note: static fields are cleared by default after a specification run. The
     * confluence demo is in a special OSGI context, so we have to call this
     * method manually.
     */
    public void resetCounter() {
        tablecount.setCount(1);
    }
}
