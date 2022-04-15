package fr.loria.mosel.rodin.plugin.sample.action;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
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
	
	/*
	 * Parse a string into a Predicate object 
	 * according to type environment of the given context
	 * */
	public Predicate parsePredicate(IContextRoot ctx, String s) throws CoreException {
		IParseResult result;
		FormulaFactory ff = ctx.getFormulaFactory();
		result = ff.parsePredicate(s, null);
		Predicate expr = result.getParsedPredicate();
		ITypeEnvironment env = ctx.getSCContextRoot().getTypeEnvironment();
		
		expr.typeCheck(env);
		expr.getSyntaxTree();	
		return expr;
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
			
			for(IContextRoot ctx : contexts(rodin)) {
				s += ctx.getComponentName() + "\n";
				
				for(IAxiom axm : ctx.getAxioms()) {
					String pred_ = axm.getPredicateString();
					Predicate pred = parsePredicate(ctx, pred_);
					s += "\t" + "axm name: " + axm.getLabel() + "\n";
					s += "\t" + "ast tree: \n\t" + pred.getSyntaxTree().toString() + "\n";
					
					// TODO: process the AST
					// See: org.eventb.core.ast.ISimpleVisitor
				}
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
