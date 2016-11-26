//
// ========================================================================
// Copyright (c) 1995-2014 Mort Bay Consulting Pty. Ltd.
// ------------------------------------------------------------------------
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// and Apache License v2.0 which accompanies this distribution.
//
// The Eclipse Public License is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// The Apache License v2.0 is available at
// http://www.opensource.org/licenses/apache2.0.php
//
// You may elect to redistribute this code under either of these licenses.
// ========================================================================
//

package info.novatec.testit.livingdoc.confluence.utils.osgi;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.osgi.framework.Bundle;


/**
 * BundleFileLocatorHelper
 * 
 * 
 * From a bundle to its location on the filesystem. Assumes the bundle is not a
 * jar.
 * 
 * @author hmalphettes
 */
public interface BundleFileLocatorHelper {

    /**
     * The name of the custom implementation for this interface in a fragment.
     */
    public static final String CLASS_NAME = "org.eclipse.jetty.osgi.boot.utils.FileLocatorHelperImpl";

    /** The default instance supports felix and equinox */
    public static BundleFileLocatorHelper DEFAULT = new DefaultFileLocatorHelper();

    /**
     * Works with equinox, felix, nuxeo and probably more. Not exactly in the
     * spirit of OSGi but quite necessary to support self-contained webapps and
     * other situations.
     * <p>
     * Currently only works with bundles that are not jar.
     * </p>
     * 
     * @param bundle The bundle
     * @return Its installation location as a file.
     * @throws Exception
     */
    public File getBundleInstallLocation(Bundle bundle) throws Exception;

    /**
     * Locate a file inside a bundle.
     * 
     * @param bundle
     * @param path
     * @return file object
     * @throws Exception
     */
    public File getFileInBundle(Bundle bundle, String path) throws Exception;

    /**
     * If the bundle is a jar, returns the jar. If the bundle is a folder, look
     * inside it and search for jars that it returns.
     * <p>
     * Good enough for our purpose (TldLocationsCache when it scans for tld
     * files inside jars alone. In fact we only support the second situation for
     * development purpose where the bundle was imported in pde and the classes
     * kept in a jar.
     * </p>
     * 
     * @param bundle
     * @return The jar(s) file that is either the bundle itself, either the jars
     * embedded inside it.
     */
    public File[] locateJarsInsideBundle(Bundle bundle) throws Exception;

    /**
     * Helper method equivalent to Bundle#getEntry(String entryPath) except that
     * it searches for entries in the fragments by using the findEntries method.
     * 
     * @param bundle
     * @param entryPath
     * @return null or all the entries found for that path.
     */
    public Enumeration<URL> findEntries(Bundle bundle, String entryPath);

    /**
     * Only useful for equinox: on felix we get the file:// or jar:// url
     * already. Other OSGi implementations have not been tested
     * <p>
     * Get a URL to the bundle entry that uses a common protocol (i.e. file:
     * jar: or http: etc.).
     * </p>
     * 
     * @return a URL to the bundle entry that uses a common protocol
     */
    public URL getLocalURL(URL url) throws Exception;

    /**
     * Only useful for equinox: on felix we get the file:// url already. Other
     * OSGi implementations have not been tested
     * <p>
     * Get a URL to the content of the bundle entry that uses the file:
     * protocol. The content of the bundle entry may be downloaded or extracted
     * to the local file system in order to create a file: URL.
     * 
     * @return a URL to the content of the bundle entry that uses the file:
     * protocol
     * </p>
     * @throws IOException
     * @throws Exception
     */
    public URL getFileURL(URL url) throws Exception;

}
