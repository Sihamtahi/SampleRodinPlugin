package fr.loria.mosel.rodin.plugin.sample.action;

import java.util.ArrayList;

import org.eclipse.core.commands.IParameter;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
 
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IVariable;
import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ISimpleVisitor;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SimplePredicate;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.builder.IGraph;
 

public class DialogAction implements IObjectActionDelegate{

	private Shell shell;
	private IProject selectedProject;
	
	@Override
	public void selectionChanged(org.eclipse.jface.action.IAction  action, ISelection selection) {
		if(selection instanceof IStructuredSelection) {    
	        Object element = ((IStructuredSelection)selection).getFirstElement();    
        
	        if (element instanceof IProject) {    
	        	selectedProject = ((IProject) element);    
	        }  
	    }
	}

	@Override
	public void setActivePart(org.eclipse.jface.action.IAction  action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}
	
	
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
	 * Retrieve all machines of a rodin project
	 */
	public ArrayList<IMachineRoot> machines(IRodinProject rodin) throws RodinDBException {	
		ArrayList<IMachineRoot> machines = new ArrayList<IMachineRoot>();
		
		for (IRodinElement element : rodin.getChildren()) {
			if (element instanceof IRodinFile) {
				IInternalElement root = ((IRodinFile) element).getRoot();
				if (root instanceof IMachineRoot) {
					machines.add((IMachineRoot) root);
				}
			}
		}
		
		return machines;
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
	/*
	 * Parse a string into a Predicate object 
	 * according to type environment of the given machine
	 * */
	public Predicate parsePredicateMachine(IMachineRoot machine, String s) throws CoreException {
		IParseResult result;
		FormulaFactory ff = machine.getFormulaFactory();
		result = ff.parsePredicate(s, null);
		Predicate expr = result.getParsedPredicate();
		
		ITypeEnvironment env = machine.getSCMachineRoot().getTypeEnvironment();
		
		expr.typeCheck(env);
		expr.getSyntaxTree();	
		return expr;
	}
	
	public void run(org.eclipse.jface.action.IAction action) {
		
		IRodinDB db = RodinCore.getRodinDB();
		String name = selectedProject.getProject().getName();
		IRodinProject rodin = db.getRodinProject(name);
		
		if(rodin == null) {
			MessageDialog.openInformation(shell, "Info", "Not a Rodin Project. Code Generation Abort!");
		}else {
			try {
			String s = "**** Contexts : ";
			
			for(IContextRoot ctx : contexts(rodin)) {
				s += ctx.getComponentName() + "\n";
				
			     
				for(IAxiom axm : ctx.getAxioms()) {
					String pred_ = axm.getPredicateString();
					Predicate pred = parsePredicate(ctx, pred_);
					s += "\t" + "axm name: " + axm.getLabel() + "\n";
					s += "\t" + "ast tree here: \n\t" + pred.getSyntaxTree().toString() + "\n";	
					 
					 
					// TODO: process the AST
					// See: org.eventb.core.ast.ISimpleVisitor
					
					
				}
			}
			s+= "*****Machines ";
			
			for(IMachineRoot machine : machines(rodin)) {
				s += machine.getElementName() + "\n";
				
				for(IVariable var : machine.getVariables()) {
					s+= "\n le nom de la variable  "+ var.getIdentifierString();
					
				}
				
				for(IInvariant invar : machine.getInvariants()) {
					String pred_ = invar.getPredicateString();
					
					Predicate pred = parsePredicateMachine(machine, pred_);
					//System.out.println("22222222222222222222222222222222222222222222222222222222222222222222");
					s += "\n pred_  : "+pred_;
					s += "\n \t " + "invar name: " + machine.getComponentName() + "\n";
					s += "\n\t " + "ast tree here: \n\t" + pred.getSyntaxTree().toString() + "\n";					 
					
					/*ISimpleVisitor visitor = (ISimpleVisitor) new DefaultVisitor();
					pred.accept(visitor);
					visitor.visitSimplePredicate((SimplePredicate) pred);*/
				 
					
					
					// TODO: process the AST
					// See: org.eventb.core.ast.ISimpleVisitor
					
				}
                s+= " Events : \n";
				
				for(IEvent event : machine.getEvents())
				{
					
					 s+= " \n name  Event  :  "+event.getLabel();
				for (org.eventb.core.IParameter para : event.getParameters())
	                   {
	                	  s+="\n\t les paramètres "+para.getIdentifierString();
	                   }
						
                   for (IGuard guar : event.getGuards() )
                   {
                	   s+="\n\t la guarde : " +guar.getPredicateString();
                   }
                   
                   for(org.eventb.core.IAction act : event.getActions())
                   {
                	   s+="\n\t l'action :  " +act.getAssignmentString();
                   }
                  
				}
				
			}
			
			MessageDialog.openInformation(shell, "Info", s);
				
			} catch (Exception e) {
				e.printStackTrace();;
				
			}
		}
		
	}

 
 


}
