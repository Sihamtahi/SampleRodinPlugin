package fr.loria.mosel.rodin.plugin.sample.action;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eventb.core.IContextRoot;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class DialogAction implements IObjectActionDelegate{

	private Shell shell;
	private IProject selectedProject;
	

	/* 
	 * Retrieve all contexts of a rodin project
	 */
	public ArrayList<IContextRoot> contexts(IRodinProject rodin) throws RodinDBException {	
		ArrayList<IContextRoot> contexts = new ArrayList<IContextRoot>();
		for (IRodinElement element : rodin.getChildren()) {
			if (element instanceof IRodinFile) {
				IInternalElement root = ((IRodinFile) element).getRoot();
				if (root instanceof IContextRoot) {
					contexts.add((IContextRoot) root);
				}
			}
		}
		return contexts;
	}
	
	public void run(IAction action) {
		
		IRodinDB db = RodinCore.getRodinDB();
		String name = selectedProject.getProject().getName();
		IRodinProject rodin = db.getRodinProject(name);
		
		if(rodin == null) {
			MessageDialog.openInformation(shell, "Info", "Not a Rodin Project. Code Generation Abort!");
		}else {
			try {
			String s = "";
			
			// Iterate on the contexts within a rodin project
			for(IContextRoot ctx : contexts(rodin)) {
				s += ctx.getComponentName() + "\n";
			}
			
			MessageDialog.openInformation(shell, "Info", s);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if(selection instanceof IStructuredSelection) {    
	        Object element = ((IStructuredSelection)selection).getFirstElement();    
        
	        if (element instanceof IProject) {    
	        	selectedProject = ((IProject) element);    
	        }  
	    }
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}
}
