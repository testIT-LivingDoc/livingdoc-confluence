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
package info.novatec.testit.livingdoc.confluence.demo.collection;

import java.util.Set;

import info.novatec.testit.livingdoc.reflect.CollectionProvider;
import info.novatec.testit.livingdoc.reflect.EnterRow;
import info.novatec.testit.livingdoc.reflect.annotation.Alias;
import info.novatec.testit.livingdoc.reflect.annotation.FixtureClass;

@FixtureClass("KanadaProvinzCodes")
public class CanadaProvinceCodesFixture {
	private static Country country = new Country("CANADA");

	private String name;
	private String code;

	public CanadaProvinceCodesFixture() {
	}

	public String getName() {
		return name;
	}

	@Alias("province name")
	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	@Alias("province code")
	public void setCode(String code) {
		this.code = code;
	}

	@Alias("addProvinceWithCode")
	public void insertProvinceWithCode(String name, String code) {
		country.addProvince(name, code);
	}

	@EnterRow
	public void insertProvince() {
		country.addProvince(name, code);
	}

	@CollectionProvider
	public Set<Province> getListOfProvinces() {
		return country.provinces();
	}

	/**
	 * This method can be called using a new table at the end of a
	 * specification.
	 * 
	 * Note: static fields are cleared by default after a specification run. The
	 * confluence demo is in a special OSGI context, so we have to call this
	 * method manually.
	 */
	public static void teardown() {
		country = new Country("CANADA");
	}
}
