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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.atlassian.confluence.util.GeneralUtil;


public final class ConfluenceVersion implements Comparable<ConfluenceVersion> {

   
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+).(\\d+)(.\\d+)?");

    private static ConfluenceVersion CURRENT;

    private final int major;
    private final int minor;

    private ConfluenceVersion(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    @Override
    public boolean equals(Object obj) {
        if ( ! ( obj instanceof ConfluenceVersion ))
            return false;

        ConfluenceVersion version = ( ConfluenceVersion ) obj;

        return version.getMajor() == getMajor() && version.getMinor() == getMinor();
    }

    @Override
    public int compareTo(ConfluenceVersion version) {
        if (getMajor() > version.getMajor()) {
            return 1;
        }

        if (getMajor() < version.getMajor()) {
            return - 1;
        }

        return new Integer(getMinor()).compareTo(version.getMinor());
    }

    public static ConfluenceVersion getCurrentVersion() {
        if (CURRENT == null) {
            CURRENT = extractVersionFrom(GeneralUtil.getVersionNumber());
        }

        return CURRENT;
    }

    static ConfluenceVersion extractVersionFrom(String version) {
        Matcher m = VERSION_PATTERN.matcher(version);

        if (m.find()) {
            String major = m.group(1);
            String minor = m.group(2);

            return new ConfluenceVersion(Integer.parseInt(major), Integer.parseInt(minor));
        }

        throw new RuntimeException("Cannot detect major version number from '" + GeneralUtil.getVersionNumber() + "'");
    }
}
