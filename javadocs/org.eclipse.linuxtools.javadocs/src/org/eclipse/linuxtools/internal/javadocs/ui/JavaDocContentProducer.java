/*******************************************************************************
 * Copyright (c) 2011-2015 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat Inc. - Initial implementation
 * Eric Williams <ericwill@redhat.com> - modification for Javadocs
 *******************************************************************************/
package org.eclipse.linuxtools.internal.javadocs.ui;

import java.io.InputStream;
import java.util.Locale;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.help.IHelpContentProducer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.linuxtools.internal.javadocs.ui.preferences.PreferenceConstants;


/**
 * The content producer for the plugin. Provides an input stream to the Eclipse
 * help system in order to render HTML in the help browser.
 */
public class JavaDocContentProducer implements IHelpContentProducer {


	/**
	 * Returns an input stream for the requested file. This method will be
	 * called by the Eclipse help system to serve up content to the internal
	 * web server, which will then be displayed in the help browser.
	 *
	 * @param  pluginID  the plugin ID as set in the manifest
	 * @param  href      the link to the file generated by the help system
	 * @param  locale    the locale of the file requested (as set in
	 * 					 JavaDocTocProvider)
	 * @return           the input stream to the file requested
	 */
    @Override
    public InputStream getInputStream(String pluginID, String href,
            Locale locale) {

    	// Eclipse help system adds parameters to the href but this breaks our
    	// path creation so we just strip them.
        if (href.contains("?")) { //$NON-NLS-1$
            href = href.substring(0, href.indexOf('?'));
        }

        // Eclipse help system appends additional plugin ID, so we strip this
        // as well.
        String pathToFile = href.replace("org.eclipse.linuxtools.javadocs", ""); //$NON-NLS-1$ //$NON-NLS-2$

        // Get path from preferences store, attempt to open input stream to the
        // file being requested.
        IPreferenceStore ps = JavaDocPlugin.getDefault().getPreferenceStore();
        IPath javadocLocation = new Path(ps.getString(PreferenceConstants.
        		JAVADOCS_DIRECTORY)).append(pathToFile);
        IFileSystem fs = EFS.getLocalFileSystem();
        IFileStore localLocation = fs.getStore(javadocLocation);
        InputStream stream = null;

        if (!localLocation.fetchInfo().exists()) {

            return null;
        }

        try {
            stream = localLocation.openInputStream(EFS.NONE, new
            		NullProgressMonitor());
        }

        catch (CoreException e) {
            ILog eLog = JavaDocPlugin.getDefault().getLog();
            eLog.log(e.getStatus());
        }

        return stream;
    }
}
