package bsbuilder.wizards.backbone.actions;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bsbuilder.wizards.site.BackboneAddJSLibPage;
import bsbuilder.wizards.site.BackboneProjectWizardPageFive;
import bsbuilder.wizards.site.BackboneProjectWizardPageFour;
import bsbuilder.wizards.site.BackboneProjectWizardPageThree;
import bsbuilder.wizards.site.utils.CommonUtils;
import bsbuilder.wizards.site.utils.TemplateMerger;

public class AddNewJSLibWizard extends Wizard implements INewWizard {

	private BackboneAddJSLibPage addJSLibPage;
	private IWorkbench workbench;
	private IStructuredSelection selection;
	private Properties bsBuilderProperties = new Properties();

	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
		
		Object firstElement = selection.getFirstElement();
	    if (firstElement instanceof IAdaptable)
        {
            IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
            String basePath = project.getLocation().toOSString();
			try {
				bsBuilderProperties.load(new FileReader(new File(basePath + "/.settings/" + "org.bsbuilder.settings")));
			} catch (Exception e) {
				//man we really need to clean this up
				e.printStackTrace();
			} 
		}
	}
	
	@Override
	public void addPages() {
		addJSLibPage = new BackboneAddJSLibPage("");
		addPage(addJSLibPage);

	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		//CommonUtils.addFileToProject(container, path, contentStream, monitor)
		
	    Object firstElement = selection.getFirstElement();
	    if (firstElement instanceof IAdaptable)
        {
            IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
            IContainer projectContainer = (IContainer) project;
            try {
				String projectName = project.getName();
				String basePackageName = bsBuilderProperties.getProperty("basePackage");				
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	        
		return true;
	}
	
	

}
