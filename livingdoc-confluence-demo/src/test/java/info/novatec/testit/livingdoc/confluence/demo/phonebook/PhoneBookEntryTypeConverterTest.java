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
package info.novatec.testit.livingdoc.confluence.demo.phonebook;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class PhoneBookEntryTypeConverterTest {

    private final PhoneBookEntryTypeConverter converter = new PhoneBookEntryTypeConverter();

    @Test
    public void testParse() throws Exception {
        PhoneBookEntry entry = parse("Fred, Flintstone, (123) 456-7890");
        assertEquals("Fred", entry.getFirstName());
        assertEquals("Flintstone", entry.getLastName());
        assertEquals("(123) 456-7890", entry.getNumber());

        entry = parse("Jean-Paul, II, (999) 123-4567");
        assertEquals("Jean-Paul", entry.getFirstName());
        assertEquals("II", entry.getLastName());
        assertEquals("(999) 123-4567", entry.getNumber());

        entry = parse("Jean-Paul, II-Junior, (999) 123-4567");
        assertEquals("Jean-Paul", entry.getFirstName());
        assertEquals("II-Junior", entry.getLastName());
        assertEquals("(999) 123-4567", entry.getNumber());

        entry = parse("A B C, D E F, (999) 999-9999");
        assertEquals("A B C", entry.getFirstName());
        assertEquals("D E F", entry.getLastName());
        assertEquals("(999) 999-9999", entry.getNumber());

        entry = parse("Great,Gazoo,(999)999-9999");
        assertEquals("Great", entry.getFirstName());
        assertEquals("Gazoo", entry.getLastName());
        assertEquals("(999)999-9999", entry.getNumber());

    }

    private PhoneBookEntry parse(String text) {
        return ( PhoneBookEntry ) converter.parse(text, PhoneBookEntry.class);
    }
}
