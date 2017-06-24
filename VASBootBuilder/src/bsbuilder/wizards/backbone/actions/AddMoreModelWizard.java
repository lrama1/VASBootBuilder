package bsbuilder.wizards.backbone.actions;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
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
import org.apache.commons.lang.StringUtils;
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

import bsbuilder.wizards.site.BackboneProjectWizardPageFive;
import bsbuilder.wizards.site.BackboneProjectWizardPageFour;
import bsbuilder.wizards.site.BackboneProjectWizardPageThree;
import bsbuilder.wizards.site.utils.CommonUtils;
import bsbuilder.wizards.site.utils.TemplateMerger;

public class AddMoreModelWizard extends Wizard implements INewWizard {

	private BackboneProjectWizardPageThree pageThree;
	private BackboneProjectWizardPageFour pageFour;
	private BackboneProjectWizardPageFive pageFive;
	private IWorkbench workbench;
	private IStructuredSelection selection;
	private Properties bsBuilderProperties = new Properties();
	private Boolean generateSecurityCode;
	
	
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
		generateSecurityCode = Boolean.parseBoolean(bsBuilderProperties.getProperty("secureCodeEnabled"));
		String uiType = bsBuilderProperties.getProperty("uiType");
		pageThree = new BackboneProjectWizardPageThree("");		
		pageFour = new BackboneProjectWizardPageFour("");
		pageFive = new BackboneProjectWizardPageFive("", uiType);
		addPage(pageThree);
		if(generateSecurityCode)
			addPage(pageFour);
		addPage(pageFive);
	}

	@Override
	public boolean performFinish(){
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
				String useMongo = bsBuilderProperties.getProperty("useMongo");
				String uiType = bsBuilderProperties.getProperty("uiType");
				Map<String, Object> modelAttributes = pageThree.getModelAttributes();
				
				//create Domain Class
				createJavaDomainClass(projectContainer, basePackageName, pageThree.getDomainClassName());	
				
				//create SampleData
				createSampleData(projectContainer, pageThree.getDomainClassName(), modelAttributes);
				
				//create SampleDate for Mongo
				if(StringUtils.equals(useMongo, "true")){
					createSampleDataForMongo(projectContainer, pageThree.getDomainClassName(), modelAttributes);
				}
				
				//create Backbone Template files				
				createEditAndListTemplateFiles(projectContainer, pageThree.getDomainClassName(), modelAttributes);
				
				//createController
				createControllerClass(projectContainer, basePackageName);
				
				//createService
				createServiceClass(projectContainer, basePackageName);
				
				//createDao
				createDaoClass(projectContainer, basePackageName, modelAttributes);
				
				if(uiType.equalsIgnoreCase("BackboneJS")){
					/**************BACKBONE SPECIFIC****************************/
					//create backbone model
					createBackboneModel(projectContainer, projectName);
					
					//create backbone collection
					createBackboneCollection(projectContainer, projectName);
					
					//create backbone edit view
					createBackboneEditView(projectContainer, projectName);
					
					//create backbone collection view
					createCollectionView(projectContainer, projectName);
					
					//create Presenter
					//createPresenter(projectContainer, projectName);
					
					//
					addNewRoutesToRouter(projectContainer, projectName);
					addNewTabsToHomePage(projectContainer, pageThree.getDomainClassName());
					/**************END OF BACKBONE SPECIFIC****************************/
				}else if(uiType.equalsIgnoreCase("AngularJS")){				
					/**************ANGULAR SPECIFIC****************************/
					createAngularControllers(projectContainer, projectName);
					createAngularService(projectContainer, projectName);
					createAngularTemplates(projectContainer, projectName);				
					appendNewScriptsToAngular(projectContainer, createNewScriptsTag(projectName, pageThree.getDomainClassName()));
					appendNewRouteExpressionToAngular(projectContainer, createWhenExpressions(projectName, pageThree.getDomainClassName()));
					addNewTabsToAngularHomePage(projectContainer, pageThree.getDomainClassName());								
					
					/**************END OF ANGULAR SPECIFIC****************************/
				}else{
					/**************VUEJS SPECIFIC****************************/
					createVueTemplates(projectContainer, projectName);
					addNewRoutesToMainJS(projectContainer, pageThree.getDomainClassName());
					addNewTabsToVueAppPage(projectContainer, pageThree.getDomainClassName());
					
					String componentTag = pageThree.getDomainClassName().toLowerCase();
					insertStatementToHomeVue(projectContainer, pageThree.getDomainClassName(), 
							"\\<.*-list\\>[\\w\\s]*\\</.*-list\\>", 
							"\n<" + componentTag + "-list></" + componentTag + "-list>");
					
					insertStatementToHomeVue(projectContainer, pageThree.getDomainClassName(), 
							"\\<script\\>", "\nimport " + pageThree.getDomainClassName() + "s from './" +
									pageThree.getDomainClassName() + "s.vue'");
					
					insertStatementToHomeVue(projectContainer, pageThree.getDomainClassName(), 
							"components\\s*:\\s*\\{", 
							"\n'" + componentTag + "-list' : " + pageThree.getDomainClassName() + "s,\n");
					//addNewTabsToAppVue(projectContainer, projectName);
					/**************END OF VUEJS SPECIFIC****************************/
				}
			
				project.refreshLocal(IProject.DEPTH_INFINITE, new NullProgressMonitor());
	
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	        
		return true;
	}
	
	private void addNewTabsToVueAppPage(IContainer projectContainer, String domainClassName) throws Exception{
		IFolder indexFolder = projectContainer.getFolder(new Path("src/ui/src"));
		IFile indexJSPFile = indexFolder.getFile("App.vue");
		File file = indexJSPFile.getRawLocation().toFile();

		String modifiedFile = FileUtils.readFileToString(file);
		//String whenRegex = "\\<ul(.*?)class(.*?)\\>";
		String whenRegex = "\\<li\\>.*\\<router-link.*\\>[\\w\\s]*\\</router-link\\>\\</li\\>";
		Pattern whenPattern = Pattern.compile(whenRegex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

		Matcher matcher = whenPattern.matcher(modifiedFile);			
		int positionToInsert = -1;
		if(matcher.find()){
			System.out.println("===========>" + matcher.group());
			System.out.println("********************************************");
			positionToInsert = matcher.end();
		}

		StringBuffer buffer = new StringBuffer(modifiedFile);
		if(positionToInsert > -1){
			buffer = new StringBuffer(modifiedFile);
			//<li><router-link to='/accounts'>Accounts List</router-link></li>
			buffer.insert(positionToInsert, "\n<li><router-link to='/" + domainClassName.toLowerCase() + "s'>" + 
					domainClassName + " List</router-link></li>");
		}
		
		modifiedFile =  buffer.toString();
		
		
		InputStream modifiedFileContent = new ByteArrayInputStream(modifiedFile.getBytes());
		indexJSPFile.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
		indexJSPFile.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());
	}
	
	private void insertStatementToHomeVue(IContainer projectContainer, String domainClassName, 
			String regex, String stringToInsert) throws Exception{
		IFolder indexFolder = projectContainer.getFolder(new Path("src/ui/src/components"));
		IFile indexJSPFile = indexFolder.getFile("Home.vue");
		File file = indexJSPFile.getRawLocation().toFile();

		String modifiedFile = FileUtils.readFileToString(file);
		Pattern whenPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

		Matcher matcher = whenPattern.matcher(modifiedFile);			
		int positionToInsert = -1;
		if(matcher.find()){
			System.out.println("===========>" + matcher.group());
			System.out.println("********************************************");
			positionToInsert = matcher.end();
		}

		StringBuffer buffer = new StringBuffer(modifiedFile);
		if(positionToInsert > -1){
			buffer = new StringBuffer(modifiedFile);
			buffer.insert(positionToInsert, stringToInsert);
		}
		
		modifiedFile =  buffer.toString();
		
		
		InputStream modifiedFileContent = new ByteArrayInputStream(modifiedFile.getBytes());
		indexJSPFile.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
		indexJSPFile.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());
	}
	
	private void createVueTemplates(IContainer projectContainer, String projectName) throws Exception{
		IFolder vueTemplatesFolder = projectContainer.getFolder(new Path("src/ui/src/components"));
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());

		CommonUtils.addFileToProject(vueTemplatesFolder, new Path(domainClassName  + ".vue"), 
				TemplateMerger.merge("/bsbuilder/resources/web/js/vue/DomainEditor-template.vue", mapOfValues), new NullProgressMonitor());
		CommonUtils.addFileToProject(vueTemplatesFolder, new Path(domainClassName  + "s.vue"), 
				TemplateMerger.merge("/bsbuilder/resources/web/js/vue/DomainList-template.vue", mapOfValues), new NullProgressMonitor());
	}
	
	private void addNewRoutesToMainJS(IContainer projectContainer, String domainClassName) throws Exception{			
		String routesToAdd = "  {path : '/" + domainClassName.toLowerCase() + "/:id', component: " + domainClassName + "},\n" +
				"  {path : '/" + domainClassName.toLowerCase() + "s', component: " + domainClassName + "s}";
		IFolder jsFolder = projectContainer.getFolder(new Path("src/ui/src"));
		IFile routerFile = jsFolder.getFile("main.js");
		File file = routerFile.getRawLocation().toFile();
		
		String routerString = FileUtils.readFileToString(file);
		
		String whenRegex = "routes.*\\[(\\r|\\n|.)*]";
		Pattern whenPattern = Pattern.compile(whenRegex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
		
		StringBuffer buffer = new StringBuffer();
				
		Matcher matcher = whenPattern.matcher(routerString);	
		if(matcher.find()){	
			String matchedString = matcher.group().trim();
			//System.out.println(matchedString);
			int insertPosition = matchedString.lastIndexOf(']');			
			buffer.append(matchedString);
			buffer.insert(insertPosition-1, ",\n" + routesToAdd);
			System.out.println(buffer.toString());
		}

		String finalString = "import " + domainClassName + " from './components/" + domainClassName + ".vue'\n" +
				"import " + domainClassName + "s from './components/" + domainClassName + "s.vue'\n" +
				routerString.replaceFirst(whenRegex, buffer.toString());
		System.out.println("+++++++++++++++++++++++++++++++++");
		System.out.println(finalString);
		
		InputStream modifiedFileContent = new ByteArrayInputStream(CommonUtils.prettifyJS(finalString).getBytes());		
		routerFile.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
		routerFile.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());
	}
	
	private String createWhenExpressions(String projectName, String domainClassName){
		String basePath = domainClassName.toLowerCase();
		String whenExpressions = 
		".when('/" + basePath + "s', {controller :  '" + domainClassName + "ListController', templateUrl : '/" + 
					projectName + "/resources/js/angular_templates/" + domainClassName + "List.html'})\n" + 
		".when('/" + basePath + "/:id', {controller :  '" + domainClassName + "EditController', templateUrl : '/" + 
					projectName + "/resources/js/angular_templates/" + domainClassName + "Edit.html'})\n"		;
		
		return whenExpressions;
	}
	
	private String createNewScriptsTag(String projectName, String domainClassName){
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("<script src=\"/" + projectName + "/resources/js/angular_controllers/" + domainClassName + "ListController.js\"></script>");
		stringBuffer.append("\n<script src=\"/" + projectName + "/resources/js/angular_controllers/" + domainClassName + "EditController.js\"></script>");
		stringBuffer.append("\n<script src=\"/" + projectName + "/resources/js/angular_services/" + domainClassName + "Service.js\"></script>");
		return stringBuffer.toString();
	}
	
	private void createJavaDomainClass(IContainer projectContainer, String basePackageName, String className 
			) throws Exception{
		IFolder javaFolder = projectContainer.getFolder(new Path("src/main/java"));
		String domainPackageName = basePackageName + ".web.domain";
		boolean xssSelected = false;
		boolean csrfSelected = false;
		if(generateSecurityCode){
			xssSelected = pageFour.getXssCheckbox().getSelection();
			csrfSelected = pageFour.getCsrfCheckbox().getSelection();
		}
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName",  pageThree.getDomainClassName());
		mapOfValues.put("domainPackageName", domainPackageName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());	
		mapOfValues.put("basePackageName", basePackageName);
		mapOfValues.put("secured", xssSelected || csrfSelected);
		mapOfValues.put("useMongo", bsBuilderProperties.getProperty("useMongo"));
		CommonUtils.createPackageAndClass(javaFolder, domainPackageName, className, pageThree.getClassSource(mapOfValues), 
				new NullProgressMonitor());
	}
	
	private void createSampleData(IContainer projectContainer, String domainClassName,
			Map<String, Object> modelAttributes) throws Exception{
		/* Add Test Data in an external text file*/
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		StringWriter sampleDataStringWriter = new StringWriter();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", modelAttributes);
		IFolder sampleDataFolder = projectContainer.getFolder(new Path("src/main/resources"));
		IOUtils.copy(TemplateMerger.merge("/bsbuilder/resources/other/sampledata.txt-template", mapOfValues), sampleDataStringWriter);
		CommonUtils.createPackageAndClass(sampleDataFolder, "sampledata", mapOfValues.get("domainClassName").toString() + "s.txt",
				CommonUtils.cleanSampleData(sampleDataStringWriter.toString()),new NullProgressMonitor());
	}
	
	private void createSampleDataForMongo(IContainer projectContainer, String domainClassName,
			Map<String, Object> modelAttributes) throws Exception{
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", modelAttributes);
		StringWriter sampleMongoDataStringWriter = new StringWriter();
		IFolder sampleMongoDataFolder = projectContainer.getFolder(new Path("src/main/resources/scripts"));
		IOUtils.copy(TemplateMerger.merge("/bsbuilder/resources/other/mongo-script.txt-template", mapOfValues), sampleMongoDataStringWriter);
		CommonUtils.createPackageAndClass(sampleMongoDataFolder, "sampledata", mapOfValues.get("domainClassName").toString() + "s.txt",
			 CommonUtils.cleanSampleData(sampleMongoDataStringWriter.toString()), new NullProgressMonitor());
	}
	
	//NOT NEEDED
	// TODO - Determine if we really need to do this again	
	private void createJSPFile(IContainer projectContainer){
		//CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF"), new Path("index.jsp"),
		//		TemplateMerger.merge("/bsbuilder/resources/web/jsps/index.jsp-template", proj.getName(),"",""), monitor);
	}
	
	private void createEditAndListTemplateFiles(IContainer projectContainer, String domainClassName,
			Map<String, Object> modelAttributes) throws Exception{
		IFolder templatesFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF/resources/js/templates"));
		if(!templatesFolder.exists()){
			//try another location
			templatesFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF/resources/templates"));
		}
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("attrs", modelAttributes);
		mapOfValues.put("templateType", pageFive.isJSPTemplate()?"JSP" : "HTML");
		mapOfValues.put("fieldTypes", pageThree.getFieldTypes());
		Path editPath;
		Path listPath;
		//Path presenterTemplatePath;
		if(pageFive.isJSPTemplate()){
			editPath = new Path(domainClassName + "EditTemplate.jsp");
			listPath = new Path(domainClassName + "ListTemplate.jsp");
			//presenterTemplatePath = new Path(domainClassName + "PresenterTemplate.jsp");
		}else{
			editPath = new Path(domainClassName + "EditTemplate.htm");
			listPath = new Path(domainClassName + "ListTemplate.htm");
			//presenterTemplatePath = new Path(domainClassName + "PresenterTemplate.htm");
		}	
		
		CommonUtils.addFileToProject(templatesFolder, editPath, 
				TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/templates/EditTemplate.jsp-template",
						 mapOfValues ), new NullProgressMonitor());
		
		CommonUtils.addFileToProject(templatesFolder, listPath, 
				TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/templates/ListTemplate.jsp-template",
						 mapOfValues), new NullProgressMonitor());		
		
		//CommonUtils.addFileToProject(templatesFolder, presenterTemplatePath, 
		//		TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/templates/PresenterTemplate.jsp-template",
		//				 mapOfValues), new NullProgressMonitor());		
	

	}
	
	private void createControllerClass(IContainer projectContainer, String basePackageName)
	throws Exception{
		/* Add a Controller*/
		
		final String controllerClassName = pageThree.getDomainClassName() + "Controller";
		String controllerPackageName = basePackageName + ".controller";
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName",  pageThree.getDomainClassName());
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());	
		mapOfValues.put("basePackageName", basePackageName);
		
		final String controllerSourceCode = pageThree.buildSourceCode(mapOfValues, "controller.java-template");
		IFolder javaFolder = projectContainer.getFolder(new Path("src/main/java"));
		CommonUtils.createPackageAndClass(javaFolder, controllerPackageName, controllerClassName, controllerSourceCode , new NullProgressMonitor());
		
	}
	
	private void createServiceClass(IContainer projectContainer, String basePackageName)
	throws Exception{
		final String serviceClassName = pageThree.getDomainClassName() + "Service";
		String servicePackageName = basePackageName + ".service";
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName",  pageThree.getDomainClassName());
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());	
		mapOfValues.put("basePackageName", basePackageName);
		final String serviceSourceCode = pageThree.buildSourceCode(mapOfValues, "service.java-template");
		IFolder javaFolder = projectContainer.getFolder(new Path("src/main/java"));
		CommonUtils.createPackageAndClass(javaFolder, servicePackageName, serviceClassName, serviceSourceCode , new NullProgressMonitor());
	}
	
	private void createDaoClass(IContainer projectContainer, String basePackageName, Map<String, Object> modelAttributes)
			throws Exception{
		final String daoClassName = pageThree.getDomainClassName() + "DAO";
		String daoPackageName = basePackageName + ".dao";
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName",  pageThree.getDomainClassName());
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());	
		//domainPackageName
		mapOfValues.put("basePackageName", basePackageName);
		String domainPackageName = basePackageName + ".web.domain";
		mapOfValues.put("domainPackageName", domainPackageName);
		mapOfValues.put("attrs", modelAttributes);
		mapOfValues.put("oracleNames", pageThree.getOracleDerivedNamesForTableAndAttrs());
		mapOfValues.put("useMongo", bsBuilderProperties.getProperty("useMongo"));
		mapOfValues.put("prepForOracle", bsBuilderProperties.getProperty("prepForOracle"));
		final String daoSourceCode = pageThree.buildSourceCode(mapOfValues,
				"dao.java-template");
		
		IFolder javaFolder = projectContainer.getFolder(new Path("src/main/java"));
		CommonUtils.createPackageAndClass(javaFolder, daoPackageName, daoClassName, daoSourceCode , new NullProgressMonitor());
	
		//create mapper files
		if("true".equals(bsBuilderProperties.getProperty("prepForOracle"))){
			final String mapperSourceCode = pageThree.buildSourceCode(mapOfValues, "mapper.java-template");
			CommonUtils.createPackageAndClass(javaFolder, daoPackageName + ".mapper", pageThree.getDomainClassName() + "Mapper",
					mapperSourceCode , new NullProgressMonitor());
			
			IFolder resourceFolder = projectContainer.getFolder(new Path("src/main/resources"));
			CommonUtils.createPackageAndClass(resourceFolder, daoPackageName + ".mapper", pageThree.getDomainClassName() + "Mapper.xml",
					IOUtils.toString(TemplateMerger.merge("/bsbuilder/resources/java/mapper-template.xml", mapOfValues)) , new NullProgressMonitor());
	
		}
	}
	
	private void createBackboneModel(IContainer projectContainer, String projectName) throws Exception{
		IFolder modelsFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF/resources/js/models"));
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());
		
		CommonUtils.addFileToProject(modelsFolder, new Path(domainClassName + "Model.js"), 
				TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/models/model-template.js", mapOfValues), new NullProgressMonitor());
	}
		
	private void createBackboneCollection(IContainer projectContainer, String projectName) throws Exception{
		IFolder collectionsFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF/resources/js/collections"));
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());		
		CommonUtils.addFileToProject(collectionsFolder, new Path(domainClassName + "Collection.js"), 
				TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/collections/collection-template.js", mapOfValues), new NullProgressMonitor());
	}
	
	//LOOK HERE
	private void createBackboneEditView(IContainer projectContainer, String projectName) throws Exception{
		IFolder viewsFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF/resources/js/views"));
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());
		mapOfValues.put("templateType", pageFive.isJSPTemplate()?"JSP" : "HTML");
		mapOfValues.put("fieldTypes", pageThree.getFieldTypes());
		CommonUtils.addFileToProject(viewsFolder, new Path(domainClassName + "EditView.js"), 
				TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/views/view-template.js", mapOfValues), new NullProgressMonitor());
	}
	
	private void createCollectionView(IContainer projectContainer, String projectName) throws Exception{
		IFolder viewsFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF/resources/js/views"));
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();		
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());
		mapOfValues.put("templateType", pageFive.isJSPTemplate()?"JSP" : "HTML");
		CommonUtils.addFileToProject(viewsFolder, new Path(domainClassName + "CollectionView.js"), 
				TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/views/collection-view-template.js", mapOfValues), new NullProgressMonitor());
	}
	
	private void createPresenter(IContainer projectContainer, String projectName) throws Exception{
		IFolder presenterFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF/resources/js/presenters"));
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();		
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());
		mapOfValues.put("templateType", pageFive.isJSPTemplate()?"JSP" : "HTML");
		
		CommonUtils.addFileToProject(presenterFolder, new Path(domainClassName + "Presenter.js"), 
				TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/views/presenter-template.js", mapOfValues), new NullProgressMonitor());
		
	}
	
	private void addNewTabsToHomePage(IContainer projectContainer, String className) throws Exception{
		IFolder indexFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF"));
		IFile indexJSPFile = indexFolder.getFile("index.jsp");
		File file = indexJSPFile.getRawLocation().toFile();
		String regex = "<!-- MARKER FOR INSERTING -->";
		String modifiedFile = FileUtils.readFileToString(file);
		modifiedFile = modifier(modifiedFile, regex,
				"<li><a href=\"#" + className.toLowerCase() + "s" + "\" >" + className + "s" + "</a></li>\n", "");
		
		InputStream modifiedFileContent = new ByteArrayInputStream(modifiedFile.getBytes());
		//indexJSPFile.delete(true, new NullProgressMonitor());
		//indexJSPFile.create(modifiedFileContent, IResource.FORCE, new NullProgressMonitor());
		indexJSPFile.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
		indexJSPFile.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());
	}
	
	private void addNewTabsToAngularHomePage(IContainer projectContainer, String className) throws Exception{
		IFolder indexFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF"));
		IFile indexJSPFile = indexFolder.getFile("index.jsp");
		File file = indexJSPFile.getRawLocation().toFile();

		String modifiedFile = FileUtils.readFileToString(file);
		//modifiedFile = modifier(modifiedFile, regex,
		//		"<li><a href=\"#" + className.toLowerCase() + "s" + "\" >" + className + "s" + "</a></li>\n", "");
		String whenRegex = "\\<ul(.*?)class(.*?)\\>";
		Pattern whenPattern = Pattern.compile(whenRegex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

		Matcher matcher = whenPattern.matcher(modifiedFile);			
		int positionToInsert = -1;
		if(matcher.find()){
			System.out.println("===========>" + matcher.group());
			positionToInsert = matcher.end();
		}
		
		//whenWriter.append("\n" + newModelTag);
		//htmlString = matcher.replaceAll("INSERTSCRIPTSHERE");
		StringBuffer buffer = new StringBuffer(modifiedFile);
		if(positionToInsert > -1){
			buffer = new StringBuffer(modifiedFile);
			buffer.insert(positionToInsert, "<li ng-class=\"{ active: isActive('/"+ 
					className.toLowerCase() + "s')}\"><a href=\"#"+ className.toLowerCase() +"s\">" +className + "</a></li>");
		}
		
		modifiedFile =  buffer.toString();
		
		
		InputStream modifiedFileContent = new ByteArrayInputStream(modifiedFile.getBytes());
		//indexJSPFile.delete(true, new NullProgressMonitor());		
		//indexJSPFile.create(modifiedFileContent, IResource.FORCE, new NullProgressMonitor());
		indexJSPFile.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
		indexJSPFile.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());
	}
	
	private void addNewRoutesToRouter(IContainer projectContainer, String projectName) throws Exception{
		IFolder jsFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF/resources/js"));
		IFile routerFile = jsFolder.getFile("router.js");
		//int lineToInsertTo = getLineToInsertNewRoutesTo(routerFile);
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());
		
		File file = routerFile.getRawLocation().toFile();
		String backboneModelName = domainClassName + "Model";
		String backboneModelViewName = domainClassName + "EditView";
		String backboneCollectionName = domainClassName + "Collection";
		String backboneCollectionViewName = domainClassName + "CollectionView";
		String defineStringToInsert = ",'models/" + backboneModelName + "'" +
				",'views/" + backboneModelViewName + "'" + 
				",'collections/" + backboneCollectionName + "'" + 
				",'views/" + backboneCollectionViewName + "'\n";	
		
		//insert params into the 'define'
		/*String defineModifierRegex = "define\\s*\\(\\[[\\d\\w\\s\\'\\,\\/]*\\]";
		String modifiedFile = modifier(file, defineModifierRegex,
				defineStringToInsert, "]");
		
		//insert corresponding params into the function
		String functionParamStringToInsert = ", " + backboneModelName + ", " + backboneModelViewName +
				", " + backboneCollectionName + ", " + backboneCollectionViewName;
		
		String functionModifierRegex = "function\\s*\\([\\d\\w\\s\\$\\,]*\\)";
		modifiedFile = modifier(modifiedFile, functionModifierRegex,
				functionParamStringToInsert, ")");
		*/		
		
		String modifiedFile = FileUtils.readFileToString(file);
		String routeDefinitionStringToInsert = "\n," + "\"" + domainClassName.toLowerCase() + "/:id\" : " +
				"\"get" + domainClassName + "\",\n" +
				"\"" + domainClassName.toLowerCase() + "s\" : " + "\"get" + domainClassName + "List\",\n" +
				"\"" + domainClassName.toLowerCase() + "Presenter\" : " + "\"show" + domainClassName + "Presenter\"\n";
		String routeDefinitionRegex = "routes\\s*:\\s*\\{[\\*\\d\\w\\s\\\"\\'\\/:,]*\\}";
		modifiedFile = modifier(modifiedFile, routeDefinitionRegex,
				routeDefinitionStringToInsert, "}");
		
		InputStream inputStream = 
				TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/routers/router-template-fragment-03.js", mapOfValues);
		
		StringWriter mergeOutput = new StringWriter();
		IOUtils.copy(inputStream, mergeOutput);
		
		//String routeActionStringToInsert = "";
		String routeActionRegex = "[\\s\\d\\w\\'\\/]*[\\n]\\s*Backbone.history.start\\(\\);";
		modifiedFile = modifier(modifiedFile, routeActionRegex,
				"\n" + mergeOutput.toString(), "");
		
		InputStream modifiedFileContent = new ByteArrayInputStream(modifiedFile.getBytes());
		//routerFile.delete(true, new NullProgressMonitor());
		//routerFile.create(modifiedFileContent, IResource.FORCE, new NullProgressMonitor());
		routerFile.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
		routerFile.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());
		
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
	
	public String modifier(File file, String expression, String stringToInsert, String stringToBefore)
	throws Exception{
		String contents = FileUtils.readFileToString(file);
		return modifier(contents, expression, stringToInsert, stringToBefore);
	}
	
	private void createAngularControllers(IContainer projectContainer, String projectName) throws Exception{
		IFolder angularControllerFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF/resources/js/angular_controllers"));
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());
		
		CommonUtils.addFileToProject(angularControllerFolder, new Path(domainClassName +"ListController.js"), 
				TemplateMerger.merge("/bsbuilder/resources/web/js/angular/angular_list_controller-template.js", mapOfValues), new NullProgressMonitor());
		CommonUtils.addFileToProject(angularControllerFolder, new Path(domainClassName +"EditController.js"), 
				TemplateMerger.merge("/bsbuilder/resources/web/js/angular/angular_edit_controller-template.js", mapOfValues), new NullProgressMonitor());
	}
	
	private void createAngularService(IContainer projectContainer, String projectName) throws Exception{
		IFolder angularServiceFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF/resources/js/angular_services"));
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());
		
		CommonUtils.addFileToProject(angularServiceFolder, new Path(domainClassName +"Service.js"), 
				TemplateMerger.merge("/bsbuilder/resources/web/js/angular/angular_service-template.js", mapOfValues), new NullProgressMonitor());
	}
	
	private void createAngularTemplates(IContainer projectContainer, String projectName) throws Exception{
		IFolder angularTemplatesFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF/resources/js/angular_templates"));
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());
		
		CommonUtils.addFileToProject(angularTemplatesFolder, new Path(domainClassName +"List.html"), 
				TemplateMerger.merge("/bsbuilder/resources/web/js/angular/angular_list_html-template.html", mapOfValues), new NullProgressMonitor());
		CommonUtils.addFileToProject(angularTemplatesFolder, new Path(domainClassName +"Edit.html"), 
				TemplateMerger.merge("/bsbuilder/resources/web/js/angular/angular_edit_html-template.html", mapOfValues), new NullProgressMonitor());
	}
	
	
	
	public void appendNewRouteExpressionToAngular(IContainer projectContainer,String newWhenExpression)
		throws Exception
	{
		IFolder jsFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF/resources/js"));
		IFile routerFile = jsFolder.getFile("angular_app.js");
		File file = routerFile.getRawLocation().toFile();
		
		String routerString = FileUtils.readFileToString(file);
		
		String whenRegex = "\\.when\\((.*?)\\)(\\;)*";
		Pattern whenPattern = Pattern.compile(whenRegex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
		//String value = routePattern.matcher(routerString).
		//System.out.println(value);
		Matcher matcher = whenPattern.matcher(routerString);	
		StringWriter whenWriter = new StringWriter();
		//1.  gather all 'when' expressions

		while(matcher.find()){			
			String matchedString = matcher.group().trim();
			if(matchedString.charAt(matchedString.length()-1) == ';'){
				matchedString = matchedString.substring(0, matchedString.length()-1);
			}
			System.out.println("===========>" + matchedString);
			whenWriter.append("\n" + matchedString);
		}
		routerString = matcher.replaceAll("INSERTHEREPLEASE");
		
		
		//2.  obtain the 'otherwise' expression (IF IT EXISTS)
		String otherwiseRegex = "\\.otherwise\\((.*?)\\)(\\;)*";
		Pattern otherwisePattern = Pattern.compile(otherwiseRegex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
		Matcher otherwiseMatcher = otherwisePattern.matcher(routerString);
		StringWriter otherwiseWriter = new StringWriter();
		while(otherwiseMatcher.find()){
			System.out.println("OTHERWISE EXPR===========>" + otherwiseMatcher.group());
			otherwiseWriter.append(otherwiseMatcher.group());
		}
		routerString = otherwiseMatcher.replaceAll("INSERTHEREPLEASE");
		
		//append the new route to the list of 'WHEN' EXPRESSIONS 
		whenWriter.append("\n" + newWhenExpression);
		
		System.out.println("=================================================================================");
		System.out.println(whenWriter.toString() + " " +  otherwiseWriter.toString());
		
		System.out.println("=================================================================================");
		int insertionPoint = routerString.indexOf("INSERTHEREPLEASE");
		StringBuffer stringBuffer = new StringBuffer(routerString);
		stringBuffer.insert(insertionPoint, whenWriter.toString() + " " +  otherwiseWriter.toString());
		
		InputStream modifiedFileContent = new ByteArrayInputStream(CommonUtils.prettifyJS(stringBuffer.toString().replaceAll("INSERTHEREPLEASE", "")).getBytes());		
		//routerFile.delete(true, new NullProgressMonitor());
		//routerFile.create(modifiedFileContent, IResource.FORCE, new NullProgressMonitor());
		routerFile.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
		routerFile.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());
		
	}
	
	private void appendNewScriptsToAngular(IContainer projectContainer, String newScriptTag)
		throws Exception
	{
		String whenRegex = "\\<script(.*?)\\>(.*?)\\<\\/script\\>";
		Pattern whenPattern = Pattern.compile(whenRegex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
		//String value = routePattern.matcher(routerString).
		//System.out.println(value);
		IFolder jsFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF"));
		IFile index2File = jsFolder.getFile("index.jsp");
		File file = index2File.getRawLocation().toFile();
		
		String htmlString = FileUtils.readFileToString(file);
		
		
		Matcher matcher = whenPattern.matcher(htmlString);	
		StringWriter whenWriter = new StringWriter();
		//1.  gather all 'when' expressions
		while(matcher.find()){
			System.out.println("===========>" + matcher.group());
			whenWriter.append("\n" + matcher.group());
		}
		whenWriter.append("\n" + newScriptTag);
		htmlString = matcher.replaceAll("INSERTSCRIPTSHERE");
		
		
		System.out.println("=================================================================================");
		int insertionPoint = htmlString.indexOf("INSERTSCRIPTSHERE");
		StringBuffer stringBuffer = new StringBuffer(htmlString);
		stringBuffer.insert(insertionPoint, whenWriter.toString());
		String finalString = stringBuffer.toString().replaceAll("INSERTSCRIPTSHERE", "");
		InputStream modifiedFileContent = new ByteArrayInputStream( finalString.getBytes());
		
		//index2File.delete(true, new NullProgressMonitor());		
		//index2File.create(modifiedFileContent, IResource.FORCE, new NullProgressMonitor());
		
		
		index2File.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
		index2File.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());
	}
	

	private String[] getLineToInsertNewRoutesTo(IFile routerFile)
			throws Exception {		
		List<String> lines = new ArrayList<String>();
		InputStream inputStream =  routerFile.getContents();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		String line = "";
		int lineNumber = 0;
		int blankLineNumber = 0;
		boolean blankLineFound = false;
		while((line = bufferedReader.readLine()) != null){
			lines.add(line + "\n");
			if(line.trim().equals("") && ! blankLineFound)
				blankLineNumber = lineNumber;
			if(line.indexOf("Backbone.history.start()") > -1){
				blankLineFound = true;
			}
			lineNumber++;
		}
		
		lines.add(blankLineNumber, "//HEY FOUND IT\n");
		lines.add(blankLineNumber, "\n");
		bufferedReader.close();
		//System.out.println("The last blank line before start is: " + blankLineNumber);
		String[] linesToReturn = new String[lines.size()];
		lines.toArray(linesToReturn);
		return linesToReturn;
	}

}