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

import java.util.ArrayList;
import java.util.List;


public class PhoneBook {

    private final List<PhoneBookEntry> entries = new ArrayList<PhoneBookEntry>();

    public void add(PhoneBookEntry entry) {
        entries.add(entry);
    }

    public List<PhoneBookEntry> getEntries() {
        return entries;
    }

    public String findNumber(String firstName, String lastName) {
        PhoneBookEntry entry = findEntry(firstName, lastName);

        return entry == null ? null : entry.getNumber();
    }

    public void updateNumber(String firstName, String lastName, String number) {
        PhoneBookEntry entry = findEntry(firstName, lastName);

        if (entry == null) {
            throw new IllegalArgumentException("Entry not found");
        }

        entry.setNumber(number);
    }

    private PhoneBookEntry findEntry(String firstName, String lastName) {
        for (PhoneBookEntry entry : entries) {
            if (entry.getFirstName().equalsIgnoreCase(firstName) && entry.getLastName().equalsIgnoreCase(lastName)) {
                return entry;
            }
        }

        return null;
    }
}
