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
package info.novatec.testit.livingdoc.confluence.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class ConfluenceVersionTest {

    @Test
    public void testCompareToMethod() {
        assertEquals(0, ConfluenceVersion.V28X.compareTo(ConfluenceVersion.V28X));
        assertEquals(1, ConfluenceVersion.V29X.compareTo(ConfluenceVersion.V28X));
        assertEquals( - 1, ConfluenceVersion.V28X.compareTo(ConfluenceVersion.V29X));
        assertEquals(1, ConfluenceVersion.V30X.compareTo(ConfluenceVersion.V28X));
    }

    @Test
    public void testEqualsMethod() {
        assertTrue(ConfluenceVersion.V28X.equals(ConfluenceVersion.V28X));
        assertFalse(ConfluenceVersion.V29X.equals(ConfluenceVersion.V28X));
        assertFalse(ConfluenceVersion.V28X.equals(ConfluenceVersion.V29X));
        assertFalse(ConfluenceVersion.V30X.equals(ConfluenceVersion.V28X));
        assertTrue(ConfluenceVersion.V30X.equals(ConfluenceVersion.V30X));
    }

    @Test
    public void testExtractVersion() {
        assertEquals(ConfluenceVersion.V28X, ConfluenceVersion.extractVersionFrom("2.8"));
        assertEquals(ConfluenceVersion.V28X, ConfluenceVersion.extractVersionFrom("2.8.1"));
        assertEquals(ConfluenceVersion.V29X, ConfluenceVersion.extractVersionFrom("2.9"));
        assertEquals(ConfluenceVersion.V210X, ConfluenceVersion.extractVersionFrom("2.10"));
        assertEquals(ConfluenceVersion.V210X, ConfluenceVersion.extractVersionFrom("2.10.1"));
        assertEquals(ConfluenceVersion.V30X, ConfluenceVersion.extractVersionFrom("3.0"));
        assertEquals(ConfluenceVersion.V30X, ConfluenceVersion.extractVersionFrom("3.0.0"));
    }
}
