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
package info.novatec.testit.livingdoc.confluence.macros.historic;

import java.io.Serializable;


@SuppressWarnings("serial")
public class ChartLongValue implements Serializable, Comparable<Object> {

    private String value;
    private Long id;

    public ChartLongValue() {
    }

    public ChartLongValue(String value, Long id) {
        this.value = value;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object obj) {
        if ( ! ( obj instanceof ChartLongValue )) {
            return false;
        }

        ChartLongValue o = ( ChartLongValue ) obj;
        return o.value.equals(value);
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof ChartLongValue) {
            return ( ( ChartLongValue ) o ).value.compareTo(value);
        } else if (o instanceof String) {
            return ( ( String ) o ).compareTo(value);
        }

        return 1;
    }
}
