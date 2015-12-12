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

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.GroupManager;
import com.atlassian.user.User;
import com.atlassian.user.search.page.Pager;


public class LivingDocUserGroup {
    private static final Logger log = LoggerFactory.getLogger(LivingDocUserGroup.class);

    private static final String LIVINGDOC_USERS = "livingdoc-users";

    private final GroupManager groupManager;

    public LivingDocUserGroup(GroupManager groupManager) {
        this.groupManager = groupManager;
    }

    public void createIfNeeded() {
        Group group = null;

        try {
            group = getLivingDocUserGroup();
        } catch (Exception ex) {
            log.warn("No 'livingdoc-users' group defined.  Will be created");
        }

        if (group == null) {
            try {
                getGroupManager().createGroup(LIVINGDOC_USERS);
            } catch (Exception ex) {
                log.warn("Creating 'livingdoc-users' group fail", ex);
            }
        }
    }

    public int getNumberOfUserForGroup() {
        try {
            final long start = System.currentTimeMillis();
            Iterator<String> itr = getMembers().iterator();
            int count = 0;

            while (itr.hasNext()) {
                itr.next();
                count ++ ;
            }

            log.debug("Number of user (member of 'livingdoc-users') = " + count + " (" + ( System.currentTimeMillis()
                - start ) + " ms.)");
            return count;
        } catch (Exception ex) {
            log.error("Getting user-count for group", ex);
            throw new RuntimeException("Getting user-count for group", ex);
        }
    }

    public boolean hasMembership(User user) {
        try {
            Group group = getLivingDocUserGroup();

            return getGroupManager().hasMembership(group, user);
        } catch (Exception ex) {
            log.error("Verifying membership of  user '" + user.getName() + "'", ex);
            return false;
        }
    }

    public boolean addMembership(User user) {
        try {
            Group group = getLivingDocUserGroup();

            getGroupManager().addMembership(group, user);

            return true;
        } catch (Exception ex) {
            log.error("Adding membership of  user '" + user.getName() + "'", ex);
            return false;
        }
    }

    private Pager<String> getMembers() throws EntityException {
        Group group = getLivingDocUserGroup();

        return getGroupManager().getMemberNames(group);
    }

    private Group getLivingDocUserGroup() throws EntityException {
        return getGroupManager().getGroup(LIVINGDOC_USERS);
    }

    private GroupManager getGroupManager() {
        return groupManager;
    }
}
