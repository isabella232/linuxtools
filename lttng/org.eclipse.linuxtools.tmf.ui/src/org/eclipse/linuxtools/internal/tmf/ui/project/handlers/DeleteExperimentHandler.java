/*******************************************************************************
 * Copyright (c) 2009, 2010, 2011 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Francois Chouinard - Initial API and implementation
 *******************************************************************************/

package org.eclipse.linuxtools.internal.tmf.ui.project.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.linuxtools.internal.tmf.ui.Activator;
import org.eclipse.linuxtools.tmf.ui.project.model.TmfExperimentElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

/**
 * <b><u>DeleteExperimentHandler</u></b>
 * <p>
 */
public class DeleteExperimentHandler extends AbstractHandler {

    // ------------------------------------------------------------------------
    // Execution
    // ------------------------------------------------------------------------

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        // Check if we are closing down
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return null;
        }

        // Confirm the operation
        Shell shell = window.getShell();
        MessageBox confirmOperation = new MessageBox(shell, SWT.ICON_QUESTION | SWT.CANCEL | SWT.OK);
        confirmOperation.setText(Messages.DeleteDialog_Title);
        confirmOperation.setMessage(Messages.DeleteExperimentHandler_Message);
        if (confirmOperation.open() != SWT.OK) {
            return null;
        }

        // Get the selection
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IWorkbenchPart part = page.getActivePart();
        if (part == null) {
            return false;
        }
        ISelection selection = part.getSite().getSelectionProvider().getSelection();

        if (selection instanceof TreeSelection) {
            TreeSelection sel = (TreeSelection) selection;
            Iterator<Object> iterator = sel.iterator();
            while (iterator.hasNext()) {
                Object element = iterator.next();
                if (element instanceof TmfExperimentElement) {
                    final TmfExperimentElement experiment = (TmfExperimentElement) element;
                    IResource resource = experiment.getResource();

                    try {
                        // Close the experiment if open
                        IFile file = experiment.getBookmarksFile();
                        FileEditorInput input = new FileEditorInput(file);
                        IWorkbench wb = PlatformUI.getWorkbench();
                        for (IWorkbenchWindow wbWindow : wb.getWorkbenchWindows()) {
                            for (IWorkbenchPage wbPage : wbWindow.getPages()) {
                                for (IEditorReference editorReference : wbPage.getEditorReferences()) {
                                    if (editorReference.getEditorInput().equals(input)) {
                                        wbPage.closeEditor(editorReference.getEditor(false), false);
                                    }
                                }
                            }
                        }

                        // Finally, delete the experiment
                        resource.delete(true, null);

                        // Refresh the project
                        experiment.getProject().refresh();

                    } catch (final CoreException e) {
                        Display.getDefault().asyncExec(new Runnable() {
                            @Override
                            public void run() {
                                final MessageBox mb = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
                                mb.setText(Messages.DeleteTraceHandler_Error + ' ' + experiment.getName());
                                mb.setMessage(e.getMessage());
                                mb.open();
                            }
                        });
                        Activator.getDefault().logError("Error deleting experiment: " + experiment.getName(), e); //$NON-NLS-1$
                    }
                }
            }
        }

        return null;
    }
}
