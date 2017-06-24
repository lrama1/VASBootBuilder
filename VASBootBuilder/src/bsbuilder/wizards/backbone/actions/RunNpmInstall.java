package bsbuilder.wizards.backbone.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ObjectPluginAction;

public class RunNpmInstall implements IObjectActionDelegate {

	@Override
	public void run(IAction iAction) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	    if (window != null){
	        IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
	        Object firstElement = selection.getFirstElement();
	        if (firstElement instanceof IAdaptable){
	            IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
	            IPath path = project.getFullPath();
	            System.out.println(path.toString());
	            System.out.println("+********************************");
	            System.out.println(project.getLocation().toPortableString());
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
	            
	           
	            String basePath = project.getLocation().toOSString();
				try {
					//bsBuilderProperties.load(new FileReader(new File(basePath + "/.settings/" + "org.bsbuilder.settings")));
					System.out.println(basePath + "/src/ui/runapp.cmd");
					System.out.println(executeCommand(basePath + "/src/ui/runapp.cmd"));
					
				} catch (Exception e) {
					//man we really need to clean this up
					e.printStackTrace();
				} 
	        }
	    }	 
	   // System.out.println(executeCommand("/tmp/sample.cmd"));
	    System.out.println("");
	}

	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setActivePart(IAction arg0, IWorkbenchPart arg1) {
		// TODO Auto-generated method stub

	}
	
	private String executeCommand(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader =
                            new BufferedReader(new InputStreamReader(p.getInputStream()));

                        String line = "";
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();

	}


}