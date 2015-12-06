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

import info.novatec.testit.livingdoc.TypeConversion;
import info.novatec.testit.livingdoc.reflect.Fixture;
import info.novatec.testit.livingdoc.systemunderdevelopment.DefaultSystemUnderDevelopment;


public class PhoneBookSystemUnderDevelopment extends DefaultSystemUnderDevelopment {

    private Fixture phoneBookFixture;

    static {
        TypeConversion.register(new PhoneBookEntryTypeConverter());
    }

    public PhoneBookSystemUnderDevelopment() {
        super();

        addImport(PhoneBookSystemUnderDevelopment.class.getPackage().getName());
    }

    @Override
    public Fixture getFixture(String name, String... params) throws Throwable {
        if (phoneBookFixture == null) {
            phoneBookFixture = super.getFixture(name, params);
        }

        return phoneBookFixture;
    }
}
