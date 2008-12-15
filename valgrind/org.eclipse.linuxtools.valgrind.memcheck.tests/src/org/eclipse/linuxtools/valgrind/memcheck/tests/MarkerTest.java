package org.eclipse.linuxtools.valgrind.memcheck.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.core.model.IBinary;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.linuxtools.valgrind.memcheck.MemcheckPlugin;
import org.eclipse.linuxtools.valgrind.memcheck.MemcheckViewPart;
import org.eclipse.linuxtools.valgrind.memcheck.ValgrindError;
import org.eclipse.linuxtools.valgrind.memcheck.ValgrindStackFrame;
import org.eclipse.linuxtools.valgrind.ui.ValgrindUIPlugin;

public class MarkerTest extends AbstractTest {
	@Override
	protected void setUp() throws Exception {
		proj = createProject("basicTest"); //$NON-NLS-1$
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject(proj);
	}

	public void testMarkers() throws Exception {
		IBinary bin = proj.getBinaryContainer().getBinaries()[0];
		ILaunchConfiguration config = createConfiguration(bin);
		config.launch(ILaunchManager.PROFILE_MODE, null, true);

		MemcheckViewPart view = (MemcheckViewPart) ValgrindUIPlugin.getDefault().getView().getDynamicView();
		ValgrindError[] errors = view.getErrors();

		ArrayList<IMarker> markers = new ArrayList<IMarker>(Arrays.asList(proj.getProject().findMarkers(MemcheckPlugin.MARKER_TYPE, true, IResource.DEPTH_INFINITE)));
		for (ValgrindError error : errors) {
			ValgrindStackFrame frame = getTopWorkspaceFrame(error.getFrames());

			int ix = -1;
			for (int i = 0; i < markers.size(); i++) {
				IMarker marker = markers.get(i);
				if (marker.getAttribute(IMarker.MESSAGE).equals(error.getWhat())
						&& marker.getResource().getName().equals(frame.getFile())
						&& marker.getAttribute(IMarker.LINE_NUMBER).equals(frame.getLine())) {
					ix = i;
				}
			}
			if (ix < 0) {
				fail();
			}
			markers.remove(ix);
		}
		assertEquals(0, markers.size());
	}
	
	private ValgrindStackFrame getTopWorkspaceFrame(List<ValgrindStackFrame> frames) throws Exception {
		ValgrindStackFrame result = null;
		for (int i = 0; result == null && i < frames.size(); i++) {
			ValgrindStackFrame frame = frames.get(i);
			String strpath = frame.getDir() + Path.SEPARATOR + frame.getFile();
			File file = new File(strpath);
			Path path = new Path(file.getCanonicalPath());
			
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IFile resource = root.getFileForLocation(path);
			if (resource != null && resource.exists()) {
				result = frame;
			}
		}
		return result;
	}
}
