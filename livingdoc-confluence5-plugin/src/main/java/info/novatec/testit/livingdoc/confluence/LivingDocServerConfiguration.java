/* Copyright (c) 2008 Pyxis Technologies inc.
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
 * http://www.fsf.org. */
package info.novatec.testit.livingdoc.confluence;

import java.util.Properties;

import info.novatec.testit.livingdoc.server.configuration.DefaultServerProperties;


public class LivingDocServerConfiguration {
    private boolean isSetupComplete;
    private Properties properties = new DefaultServerProperties();

    public LivingDocServerConfiguration() {
    }

    /**
     * Is completed if the database setup was OK. Does not tell you whether the
     * database is available.
     */
    public boolean isSetupComplete() {
        return isSetupComplete;
    }

    public void setSetupComplete(boolean setupComplete) {
        isSetupComplete = setupComplete;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("LivingDocServerConfiguration:");

        sb.append("{isSetupComplete=").append(isSetupComplete()).append(";properties=").append(properties).append("}");

        return sb.toString();
    }
}
