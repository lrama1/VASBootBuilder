package bsbuilder.wizards.backbone.actions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.statushandlers.StatusManager;

import bsbuilder.wizards.site.BackboneAddWizardTypePresenterPage;
import bsbuilder.wizards.site.utils.CommonUtils;
import bsbuilder.wizards.site.utils.TemplateMerger;

public class AddNewWizardTypePresenterWizard extends Wizard implements INewWizard {

	private BackboneAddWizardTypePresenterPage addPresenterPage;
	private IWorkbench workbench;
	private IStructuredSelection selection;
	private Properties bsBuilderProperties = new Properties();
	private List<String> viewPaths = new ArrayList<String>();
	
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
				IFolder folder = project.getFolder("src/main/webapp/WEB-INF/resources/js/views");

				IResource[] resources =  folder.members(false);
				
				for(IResource resource : resources){
					if(resource instanceof org.eclipse.core.internal.resources.File){
						org.eclipse.core.internal.resources.File jsFile = (org.eclipse.core.internal.resources.File) resource;
						String[] pathSegments = jsFile.getLocation().segments();
						
						System.out.println(pathSegments[pathSegments.length-2] +
								pathSegments[pathSegments.length-1] + "==xx==>" +   resource.getName());
						viewPaths.add(pathSegments[pathSegments.length-2] + "/" +
								pathSegments[pathSegments.length-1]);						
					}
				}
								
				
			} catch (Exception e) {
				//man we really need to clean this up
				e.printStackTrace();
				StatusManager.getManager().handle(new Status(IStatus.ERROR, "BSB", e.getMessage()), StatusManager.BLOCK); 
			} 
		}
	}
	
	@Override
	public void addPages() {
		addPresenterPage = new BackboneAddWizardTypePresenterPage("");
		addPresenterPage.setViewNamesFound(viewPaths);
		addPage(addPresenterPage);

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
				createWizardPresenter(projectContainer, projectName);
				addNewRoutesToRouter(projectContainer, projectName, this.addPresenterPage.getPresenterName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				StatusManager.getManager().handle(new Status(IStatus.ERROR, "BSB", e.getMessage()), StatusManager.BLOCK); 
			}
        }
	        
		return true;
	}
	
	private void createWizardPresenter(IContainer projectContainer, String projectName) throws Exception{
		IFolder presenterFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF/resources/js/presenters"));
		String presenterName = this.addPresenterPage.getPresenterName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();		
		mapOfValues.put("wizardPresenterName", presenterName);
		mapOfValues.put("childViews", arrayToList(addPresenterPage.getSelectedViews()));
		
		CommonUtils.addFileToProject(presenterFolder, new Path(presenterName + ".js"), 
				TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/views/wizard-presenter-template.js", mapOfValues), new NullProgressMonitor());
	
		IFolder templatesFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF/resources/js/templates"));
		if(!templatesFolder.exists()){
			//try another location
			templatesFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF/resources/templates"));
		}
		Path presenterTemplatePath = new Path(presenterName + "Template.htm");
		CommonUtils.addFileToProject(templatesFolder, presenterTemplatePath, 
				TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/templates/WizardPresenterTemplate.jsp-template",
						 mapOfValues), new NullProgressMonitor());	
	}
	
	private Map<String, String> arrayToList(String[] selectedViews){
		Map<String, String> views = new HashMap<String, String>();
		for(String viewPath : selectedViews){
			String viewName = viewPath.substring(viewPath.lastIndexOf("/")+1, viewPath.lastIndexOf('.'));
			views.put(viewPath.replace(" ","").replace(".js", ""), viewName);
		}
		return views;
	}
	
	private void addNewRoutesToRouter(IContainer projectContainer, String projectName, String presenterName) throws Exception{
		IFolder jsFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF/resources/js"));
		IFile routerFile = jsFolder.getFile("router.js");
		
		File file = routerFile.getRawLocation().toFile();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("wizardPresenterName", presenterName);
		
		String modifiedFile = FileUtils.readFileToString(file);
		String routeDefinitionStringToInsert = 
				"\n,\"" + presenterName + "\" : " + "\"show" + presenterName + "\"\n";
		String routeDefinitionRegex = "routes\\s*:\\s*\\{[\\*\\d\\w\\s\\\"\\'\\/:,]*\\}";
		modifiedFile = modifier(modifiedFile, routeDefinitionRegex,
				routeDefinitionStringToInsert, "}");
		
		InputStream inputStream = 
				TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/routers/router-template-fragment-04.js", mapOfValues);
		
		StringWriter mergeOutput = new StringWriter();
		IOUtils.copy(inputStream, mergeOutput);
		
		//String routeActionStringToInsert = "";
		String routeActionRegex = "[\\s\\d\\w\\'\\/]*[\\n]\\s*Backbone.history.start\\(\\);";
		modifiedFile = modifier(modifiedFile, routeActionRegex,
				"\n" + mergeOutput.toString(), "");
		
		InputStream modifiedFileContent = new ByteArrayInputStream(modifiedFile.getBytes());
		routerFile.delete(true, new NullProgressMonitor());
		routerFile.create(modifiedFileContent, IResource.FORCE, new NullProgressMonitor());
	}
	
	public String modifier(String fileContents, String expression, String stringToInsert, String stringToBefore){
		String newFileContents = "";
				
			//String contents = FileUtils.readFileToString(new File(fileName));
			
			StringBuffer buffer = null;
			Pattern pattern = Pattern.compile(expression);
			Matcher matcher = pattern.matcher(fileContents);
			if(matcher.find()){
				String origString = matcher.group();
				buffer = new StringBuffer(origString);
				if(!stringToBefore.equals(""))
					buffer.insert(origString.lastIndexOf(stringToBefore), stringToInsert);
				else
					buffer.insert(0, stringToInsert);
			}
			//newFileContents = fileContents.replaceAll(expression, buffer.toString().replace("$", "\\$"));
			newFileContents = fileContents.replaceFirst(expression, buffer.toString().replace("$", "\\$"));
		
		return newFileContents;
	}


}
