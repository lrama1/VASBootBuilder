package bsbuilder.wizards.backbone.actions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import bsbuilder.wizards.site.BackboneProjectWizardPageThree;
import bsbuilder.wizards.site.NewBackboneSpringProjectWizard;

public class AddNewJSLibAction implements IObjectActionDelegate {

	private Shell shell;
	IWorkbenchPart part;
	ISelection selection;
	
	/**
	 * Constructor for Action1.
	 */
	public AddNewJSLibAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
		this.part = part;
		System.out.println();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	    if (window != null)
	    {
	        IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
	        Object firstElement = selection.getFirstElement();
	        if (firstElement instanceof IAdaptable)
	        {
//	            IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
//	            IPath path = project.getFullPath();
//	            System.out.println(path);
//	            IContainer container = (IContainer) project;
//	            IFolder srcFolder = container.getFolder(new Path("src"));
//	            IFolder mainFolder = srcFolder.getFolder(new Path("main"));
//	            IFolder webAppfolder = mainFolder.getFolder(new Path("webapp"));
//	            IFile file = webAppfolder.getFile("sample.txt");
//	            String test = "test";
//	            InputStream stream = new ByteArrayInputStream(test.getBytes());
//	            try {
//					file.create(stream, true, null);
//				} catch (CoreException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
	            
	        	AddNewJSLibWizard wizard = new AddNewJSLibWizard();

	            wizard.init(window.getWorkbench(),
	                      (IStructuredSelection)selection);
	              // Instantiates the wizard container with the wizard and opens it
	              WizardDialog dialog = new WizardDialog(shell, wizard);
	              dialog.create();
	              dialog.open();
	        }
	    }
		
		//IContainer container = (IContainer) proj;	
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
