/**
 * Copyright (c) 2008 Pyxis Technologies inc.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */
package info.novatec.testit.livingdoc.confluence.demo.calculator;

import info.novatec.testit.livingdoc.reflect.annotation.Alias;
import info.novatec.testit.livingdoc.reflect.annotation.FixtureClass;


@FixtureClass({ "Rechner", "Taschenrechner", "Maths" })
public class Calculator {
    private int x;
    private int y;

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
}
