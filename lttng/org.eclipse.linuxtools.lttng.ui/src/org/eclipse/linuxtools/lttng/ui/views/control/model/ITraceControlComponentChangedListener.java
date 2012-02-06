/**********************************************************************
 * Copyright (c) 2012 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   Bernd Hufmann - Initial API and implementation
 **********************************************************************/
package org.eclipse.linuxtools.lttng.ui.views.control.model;

/**
 * <b><u>ITraceControlComponentChangedListener</u></b>
 * <p>
 * Listener interface a class can implement to be notified about changes 
 * of components 
 * </p>
 */
public interface ITraceControlComponentChangedListener {
    /**
     * Interface for notifications about the addition of a component.
     * @param parent - the parent where the child was added.
     * @param component - the child that was added.
     */
    public void componentAdded(ITraceControlComponent parent, ITraceControlComponent component);

    /**
     * Interface for notifications about the removal of a child.
     * @param parent - the parent where the child was removed.
     * @param component - the child that was removed.
     */
    public void componentRemoved(ITraceControlComponent parent, ITraceControlComponent component);
    /**
     * NInterface for notifications about the change of a component.
     * @param component - the component that was changed.
     */
    public void componentChanged(ITraceControlComponent component);
}

