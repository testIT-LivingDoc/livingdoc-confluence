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

import java.util.List;

import info.novatec.testit.livingdoc.reflect.EnterRow;
import info.novatec.testit.livingdoc.reflect.annotation.Alias;
import info.novatec.testit.livingdoc.reflect.annotation.FixtureClass;

@FixtureClass({ "Telefonbuch", "Contacts list" })
public class PhoneBookFixture {

	private final PhoneBook phoneBook;
	private PhoneBookEntry contact;

	public PhoneBookFixture() {
		phoneBook = new PhoneBook();
	}

	public PhoneBookEntry getContact() {
		return contact;
	}

	@Alias("phone book entry")
	public void setContact(PhoneBookEntry contact) {
		this.contact = contact;
	}

	public List<PhoneBookEntry> query() {
		return phoneBook.getEntries();
	}

	@EnterRow
	public void insertNewContact() {
		phoneBook.add(getContact());
	}

	@Alias({"addWithNumber", "insertWithNumber"})
	public boolean addNewPhoneBookEntry(String firstName, String lastName,
			String number) {
		try {
			phoneBook.add(new PhoneBookEntry(firstName, lastName, number));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Alias("thatNumberOfIs")
	public String checkNumberByName(String firstName, String lastName) {
		return phoneBook.findNumber(firstName, lastName);
	}

	@Alias({"changeNumberOfTo", "updateNumberOfTo"})
	public boolean updateNumberByName(String firstName, String lastName,
			String number) {
		try {
			phoneBook.updateNumber(firstName, lastName, number);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
