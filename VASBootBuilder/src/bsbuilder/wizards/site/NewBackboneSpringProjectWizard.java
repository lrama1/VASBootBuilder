package bsbuilder.wizards.site;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import bsbuilder.wizards.site.utils.CommonUtils;
import bsbuilder.wizards.site.utils.TemplateMerger;

public class NewBackboneSpringProjectWizard extends Wizard implements
		INewWizard, IExecutableExtension {

	/*
	 * Use the WizardNewProjectCreationPage, which is provided by the Eclipse
	 * framework.
	 */
	private WizardNewProjectCreationPage wizardPage;
	private BackboneProjectWizardPageTwo pageTwo;
	private BackboneProjectWizardPageThree pageThree;
	private BackboneProjectWizardPageFour pageFour;
	private BackboneProjectWizardPageFive pageFive;
	private BackboneProjectWizardPageSix pageSix;

	private IConfigurationElement config;

	private IWorkbench workbench;

	private IStructuredSelection selection;

	private IProject project;
	
	private Map<String, IFolder> folders = new HashMap<String, IFolder>();

	@Override
	public void addPages() {
		/*
		 * Unlike the custom new wizard, we just add the pre-defined one and
		 * don't necessarily define our own.
		 */
		wizardPage = new WizardNewProjectCreationPage(
				"NewExampleComSiteProject");
		wizardPage.setDescription("Create a new Pre-Scaffolded Backbone/Spring Web Project.");
		wizardPage.setTitle("Pre-Scaffolded Backbone/Spring Web Project");
		addPage(wizardPage);
		
		pageTwo = new BackboneProjectWizardPageTwo("test");
		addPage(pageTwo);
		
		pageThree = new BackboneProjectWizardPageThree("");
		addPage(pageThree);
		
		pageFour = new BackboneProjectWizardPageFour("securityOptions");
		addPage(pageFour);
		
		pageFive = new BackboneProjectWizardPageFive("BackboneOptions");
		addPage(pageFive);
		
		pageSix = new BackboneProjectWizardPageSix("otherFeatures");
		addPage(pageSix);
		
	}

	@Override
	public boolean performFinish() {

		try{
		if (project != null) {
			return true;
		}

		final IProject projectHandle = wizardPage.getProjectHandle();

		URI projectURI = (!wizardPage.useDefaults()) ? wizardPage
				.getLocationURI() : null;

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		final IProjectDescription desc = workspace
				.newProjectDescription(projectHandle.getName());

		desc.setLocationURI(projectURI);
		
		//The following all go in the .project file
		desc.setNatureIds(new String[] {"org.eclipse.jem.workbench.JavaEMFNature",
				"org.eclipse.wst.common.modulecore.ModuleCoreNature",
				"org.eclipse.jdt.core.javanature",
				"org.eclipse.m2e.core.maven2Nature",
				"org.eclipse.wst.common.project.facet.core.nature",
				"org.eclipse.wst.jsdt.core.jsNature"});
		ICommand[] commands = 
				new ICommand[] { desc.newCommand(), desc.newCommand(),
				desc.newCommand(), desc.newCommand(),desc.newCommand()};
		commands[0].setBuilderName("org.eclipse.wst.jsdt.core.javascriptValidator");
		commands[1].setBuilderName("org.eclipse.jdt.core.javabuilder");
		commands[2].setBuilderName("org.eclipse.wst.common.project.facet.core.builder");
		commands[3].setBuilderName("org.eclipse.m2e.core.maven2Builder");
		commands[4].setBuilderName("org.eclipse.wst.validation.validationbuilder");
		desc.setBuildSpec(commands);
		final boolean xssSelected = pageFour.getXssCheckbox().getSelection();
		final boolean csrfSelected = pageFour.getCsrfCheckbox().getSelection();
		
		final String basePackageName = pageTwo.getBasePackageName(); 
		final String controllerPackageName = pageTwo.getControllerPackage();
		final String domainPackageName = pageTwo.getDomainPackage();
		//final String utilPackageName = pageTwo.getBasePackageName() + ".util"; 
		
		final String domainClassName = pageThree.getDomainClassName();
		final String domainClassIdAttributeName = pageThree.getDomainClassAttributeName();
		
		final Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("projectName", projectHandle.getName());
		mapOfValues.put("domainPackageName", domainPackageName);
		mapOfValues.put("domainClassName",  domainClassName);
		mapOfValues.put("domainClassIdAttributeName", domainClassIdAttributeName);	
		mapOfValues.put("basePackageName", basePackageName);
		mapOfValues.put("utilPackageName", basePackageName + ".util");
		mapOfValues.put("secured", xssSelected || csrfSelected);		
		mapOfValues.put("useMongo", pageTwo.useMongoDB());
		mapOfValues.put("mongoHostName", pageTwo.getMongoHostName());
		mapOfValues.put("mongoPort", pageTwo.getMongoPort());
		mapOfValues.put("mongoDBName", pageTwo.getMongoDBName());					
		mapOfValues.put("templateType", pageFive.isJSPTemplate()?"JSP" : "HTML");
		mapOfValues.put("injectMessages", pageFive.injectLocalizedMessages());
		
		mapOfValues.put("useMongo", pageTwo.useMongoDB());
		mapOfValues.put("mongoDBName", "localdb");
		mapOfValues.put("mongoHostName", pageTwo.getMongoHostName());
		mapOfValues.put("mongoPort", pageTwo.getMongoPort());
		mapOfValues.put("mongoDBName", pageTwo.getMongoDBName());
		mapOfValues.put("prepForOracle", pageTwo.prepForOracle());
		mapOfValues.put("prepForMySQL", pageTwo.prepForMySql());
		mapOfValues.put("oracleHost", pageTwo.getOracleHost());
		mapOfValues.put("oraclePort", pageTwo.getOraclePort());
		mapOfValues.put("oracleInstance", pageTwo.getOracleInstance());
		mapOfValues.put("oracleUser", pageTwo.getOracleUser());
		mapOfValues.put("oraclePassword", pageTwo.getOraclePassword());
		
		mapOfValues.put("addWebService", pageSix.addWebServiceFeature());
		mapOfValues.put("springVersion", pageTwo.getSpringVersion());
				
		final String domainClassSourceCode = pageThree.getClassSource(mapOfValues);
		mapOfValues.put("attrs", pageThree.getModelAttributes());
		mapOfValues.put("oracleNames", pageThree.getOracleDerivedNamesForTableAndAttrs());		
		mapOfValues.put("fieldTypes", pageThree.getFieldTypes());
		mapOfValues.put("vueComponentTagName", createComponentTag(domainClassName));
		
		final String controllerClassName = domainClassName + "Controller";
		//final String mainControllerSourceCode = pageThree.getMainControllerSource(controllerPackageName, utilPackageName);
		//final String domainControllerSourceCode = pageThree.getControllerSource(basePackageName, controllerPackageName, domainClassName, domainClassIdAttributeName);
		final String controllerTestSourceCode = pageThree.getControllerTestSource(basePackageName, controllerPackageName, domainClassName);
		final String servicePackageName = pageTwo.getBasePackageName() + ".service";
		final String webServicePackageName = pageTwo.getBasePackageName() + ".webservice";
		final String daoPackageName = pageTwo.getBasePackageName() + ".dao";
		final String commonPackageName = pageTwo.getBasePackageName() + ".common";
		final String securityPackageName = pageTwo.getBasePackageName() + ".security";
		
		
		
		final SourceCodeGeneratorParameters params = new SourceCodeGeneratorParameters();
		params.setBasePackageName(basePackageName);
		params.setControllerPackageName(controllerPackageName);
		params.setDomainPackageName(domainPackageName);
		params.setDomainClassName(domainClassName);
		params.setDomainClassSourceCode(domainClassSourceCode);
		params.setDomainClassIdAttributeName(domainClassIdAttributeName);
		params.setControllerClassName(controllerClassName);
		params.setMainControllerSourceCode(pageThree.buildSourceCode(mapOfValues, "common-controller.java-template")); 
		params.setDomainControllerSourceCode(pageThree.buildSourceCode(mapOfValues, "controller.java-template"));
		params.setControllerTestSourceCode(controllerTestSourceCode);
		params.setServicePackageName(servicePackageName);
		//params.setServiceSourceCode(pageThree.getSeviceSourceCode(basePackageName, servicePackageName, domainClassName, domainClassIdAttributeName));
		params.setServiceSourceCode(pageThree.buildSourceCode(mapOfValues, "service.java-template"));
		
		params.setDaoPackageName(daoPackageName);
		//params.setDaoSourceCode(pageThree.getDaoSourceCode(basePackageName, daoPackageName, domainClassName, domainClassIdAttributeName));
		params.setDaoSourceCode(pageThree.buildSourceCode(mapOfValues, "dao.java-template"));
		params.setMapperSourceCode(pageThree.buildSourceCode(mapOfValues, "mapper.java-template"));
				
		params.setCommonPackageName(commonPackageName);
		params.setListWrapperSourceCode(pageThree.getListWrapperSourceCode(basePackageName, commonPackageName, domainClassName));
		params.setNameValuePairSourceCode(pageThree.getNameValueSourceCode(basePackageName, commonPackageName, domainClassName));
		params.setSecurityPackageName(securityPackageName);
		params.setSecurityUserDetailsServiceSourceCode(pageThree.getSecurityUserDetailsServiceSourceCode(securityPackageName));
		params.setSecurityUserDetailsSourceCode(pageThree.getSecurityUserDetailsSourceCode(securityPackageName));
		if(xssSelected || csrfSelected){
			params.setGenerateSecurityCode(true);
			params.setSecurityAspectCode(pageThree.buildSourceCode(mapOfValues, "security-aspect.java-template"));
			params.setSecuredDomainCode(pageThree.buildSourceCode(mapOfValues, "security-domain.java-template"));
		}
		params.setSecurityEnumCode(pageThree.buildSourceCode(mapOfValues, "security-annotation-type.java-template"));
		params.setSecurityAnnotationCode(pageThree.buildSourceCode(mapOfValues, "security-field-annotation.java-template"));
		params.setSecurityTokenGeneratorCode(pageThree.buildSourceCode(mapOfValues, "security-token-generator.java-template"));
		params.setSmHeaderChangeSourceCode(pageThree.buildSourceCode(mapOfValues, "clear-session-on-sm-header-change.java-template"));
		
		params.setSampleMessageBundleContent(pageThree.getMessageBundleContent("", "", ""));
		params.setSampleMessageBundleContentEs(pageThree.getMessageBundleContentEs("", "", ""));
		params.setUtilPackageName(basePackageName + ".util");
		//params.setResourceBundleUtilSourceCode(pageThree.getResourceBundleSourceCode(utilPackageName));
		params.setResourceBundleUtilSourceCode(pageThree.buildSourceCode(mapOfValues, "exposed-resource-bundle.java-template"));
		
		params.setSampleESAPIProperties(CommonUtils.linesToString(IOUtils.readLines(getClass().getResourceAsStream("/bsbuilder/resources/esapi/ESAPI.properties")),"\n"));
		params.setJSPTemplate(pageFive.isJSPTemplate());
		params.setInjectLocalizedMessages(pageFive.injectLocalizedMessages());
		params.setUiType(pageFive.getUIType());
		
		params.setGenerateWebService(pageSix.addWebServiceFeature());
		params.setWebServicePackageName(webServicePackageName);
		params.setWebServiceInterfaceSourceCode(pageThree.buildSourceCode(mapOfValues, "webserviceinterface.java-template"));
		params.setWebServiceImplSourceCode(pageThree.buildSourceCode(mapOfValues, "webserviceimpl.java-template"));
		System.out.println("JSP?*******************************" + pageFive.isJSPTemplate());
		System.out.println("HTML?*******************************" + pageFive.isHTMLTemplate());

		
		/*
		 * Just like the NewFileWizard, but this time with an operation object
		 * that modifies workspaces.
		 */
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws CoreException {
				createProject(desc, projectHandle, params, mapOfValues , monitor);
			}
		};

		/*
		 * This isn't as robust as the code in the BasicNewProjectResourceWizard
		 * class. Consider beefing this up to improve error handling.
		 */
		try {
			getContainer().run(true, true, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException
					.getMessage());
			return false;
		}

		project = projectHandle;

		if (project == null) {
			return false;
		}

		BasicNewProjectResourceWizard.updatePerspective(config);
		BasicNewProjectResourceWizard.selectAndReveal(project, workbench
				.getActiveWorkbenchWindow());

		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * This creates the project in the workspace.
	 * 
	 * @param description
	 * @param projectHandle
	 * @param monitor
	 * @throws CoreException
	 * @throws OperationCanceledException
	 */
	void createProject(IProjectDescription description, IProject proj, 
			SourceCodeGeneratorParameters params, Map<String, Object> mapOfValues,
			IProgressMonitor monitor) throws CoreException,
			OperationCanceledException {
		try {

			monitor.beginTask("", 2000);
			proj.create(description, new SubProgressMonitor(monitor, 1000));

			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}

			proj.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(
					monitor, 1000));

			/*
			 * Okay, now we have the project and we can do more things with it
			 * before updating the perspective.
			 */
			IContainer container = (IContainer) proj;			

			/* Add an pom file */
			CommonUtils.addFileToProject(container, new Path("pom.xml"),
					TemplateMerger.merge("/bsbuilder/resources/maven/pom.xml-template", mapOfValues), monitor);			

			//call create folders here
			createFolderStructures(container, monitor);			
			
			//add 3rd party JS libs
			if(params.getUiType().equalsIgnoreCase("BackboneJS")){
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources"), new Path("r.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/r.js"), monitor);
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("require.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/require.js"), monitor);
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("backbone.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/backbone.js"), monitor);
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("underscore.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/underscore-min.js"), monitor);
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("backgrid.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/backgrid.js"), monitor);
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/css/libs"), new Path("backgrid.css"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/backgrid.css"), monitor);
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/css/libs"), new Path("backgrid-paginator.css"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/backgrid-paginator.css"), monitor);
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("backgrid-paginator.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/backgrid-paginator.js"), monitor);			
				
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/css/libs"), new Path("backgrid-select-all.css"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/backgrid-select-all.css"), monitor);
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("backgrid-select-all.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/backgrid-select-all.js"), monitor);			
				
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("backbone-pageable.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/backbone-pageable.js"), monitor);
				//
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("bootstrap-datepicker.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/bootstrap-datepicker.js"), monitor);
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/css/libs"), new Path("datepicker.css"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/datepicker.css"), monitor);
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("backbone.global.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/backbone.global.js"), monitor);
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/models"), new Path(params.getDomainClassName()  + "Model.js"), 
						TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/models/model-template.js", mapOfValues), monitor);
				
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/collections"), new Path(params.getDomainClassName() + "Collection.js"), 
						TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/collections/collection-template.js", mapOfValues), monitor);
				
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/views"), new Path(params.getDomainClassName() + "EditView.js"), 
						TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/views/view-template.js", mapOfValues), monitor);			
				
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/views"), new Path(params.getDomainClassName() + "CollectionView.js"), 
						TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/views/collection-view-template.js", mapOfValues), monitor);
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources"), new Path("buildconfig.js"), 
						TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/main/buildconfig-template.js", mapOfValues), monitor);
				
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js"), new Path("app.js"), 
						TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/main/app-template.js", mapOfValues), monitor);
				
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js"), new Path("router.js"), 
						TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/routers/router-template.js", mapOfValues), monitor);
				
				/*Add a backbone template file.  This is dependent on the Java Model generation.
				 * Instead of using plain html, we are going to use JSP so we can use Spring's
				 * Message Bundles for localization.
				 * */			
				Path listTemplatePath;
				Path editTemplatePath;
				//Path presenterTemplatePath;
				if(params.isJSPTemplate()){
					listTemplatePath = new Path(params.getDomainClassName() + "ListTemplate.jsp");
					editTemplatePath = new Path(params.getDomainClassName() + "EditTemplate.jsp");
					//presenterTemplatePath = new Path(params.getDomainClassName() + "PresenterTemplate.jsp");
				}else{
					listTemplatePath = new Path(params.getDomainClassName() + "ListTemplate.htm");
					editTemplatePath = new Path(params.getDomainClassName() + "EditTemplate.htm");
					//presenterTemplatePath = new Path(params.getDomainClassName() + "PresenterTemplate.htm");
				}	
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/templates"), editTemplatePath, 
						TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/templates/EditTemplate.jsp-template",  mapOfValues), monitor);
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/templates"), listTemplatePath, 
						TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/templates/ListTemplate.jsp-template", mapOfValues), monitor);
				//CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/templates"), presenterTemplatePath, 
				//		TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/templates/PresenterTemplate.jsp-template", mapOfValues), monitor);
				/* Add a default jsp file.  This is dependent on the Java Model generation */
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF"), new Path("index.jsp"),
								TemplateMerger.merge("/bsbuilder/resources/web/jsps/index.jsp-template", mapOfValues), monitor);
			
			}else if(params.getUiType().equalsIgnoreCase("AngularJS")){
				//ANGULARJS ONLY COMPONENTS
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("dirPagination.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/dirPagination.js"), monitor);
				
				
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("angular.min.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/angular.min.js"), monitor);
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("angular-route.min.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/angular-route.min.js"), monitor);
				
				
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js"), new Path("angular_app.js"), 
						TemplateMerger.merge("/bsbuilder/resources/web/js/angular/angular_app-template.js", mapOfValues), monitor);
				
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/angular_controllers"), new Path("HomeController.js"), 
						TemplateMerger.merge("/bsbuilder/resources/web/js/angular/angular_home_controller-template.js", mapOfValues), monitor);
				
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/angular_controllers"), new Path(params.getDomainClassName() +"ListController.js"), 
						TemplateMerger.merge("/bsbuilder/resources/web/js/angular/angular_list_controller-template.js", mapOfValues), monitor);
				
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/angular_templates"), new Path(params.getDomainClassName() + "List.html"), 
						TemplateMerger.merge("/bsbuilder/resources/web/js/angular/angular_list_html-template.html", mapOfValues), monitor);
				
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/angular_controllers"), new Path(params.getDomainClassName() +"EditController.js"), 
						TemplateMerger.merge("/bsbuilder/resources/web/js/angular/angular_edit_controller-template.js", mapOfValues), monitor);
				
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/angular_services"), new Path(params.getDomainClassName() +"Service.js"), 
						TemplateMerger.merge("/bsbuilder/resources/web/js/angular/angular_service-template.js", mapOfValues), monitor);
				
				
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/angular_templates"), new Path(params.getDomainClassName() + "Edit.html"), 
						TemplateMerger.merge("/bsbuilder/resources/web/js/angular/angular_edit_html-template.html", mapOfValues), monitor);
				
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF"), new Path("index.jsp"), 
						TemplateMerger.merge("/bsbuilder/resources/web/js/angular/angular_index-template.jsp", mapOfValues), monitor);
				
				CommonUtils.addFileToProject(container, new Path(".tern-project"), 
						TemplateMerger.merge("/bsbuilder/resources/web/js/angular/tern-project", mapOfValues), monitor);
			}else{ //VueJS
				CommonUtils.addFileToProject(folders.get("src/ui/config"), new Path("index.js"), 
						TemplateMerger.merge("/bsbuilder/resources/web/js/vue/index-template.js", mapOfValues), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/components"), new Path("Home.vue"), 
						TemplateMerger.merge("/bsbuilder/resources/web/js/vue/Home-template.vue", mapOfValues), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/components"), new Path(params.getDomainClassName()  + ".vue"), 
						TemplateMerger.merge("/bsbuilder/resources/web/js/vue/DomainEditor-template.vue", mapOfValues), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/components"), new Path(params.getDomainClassName()  + "s.vue"), 
						TemplateMerger.merge("/bsbuilder/resources/web/js/vue/DomainList-template.vue", mapOfValues), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("App.vue"), 
						TemplateMerger.merge("/bsbuilder/resources/web/js/vue/App-template.vue", mapOfValues), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("main.js"), 
						TemplateMerger.merge("/bsbuilder/resources/web/js/vue/main-template.js", mapOfValues), monitor);
				//CommonUtils.addFileToProject(folders.get("src/ui"), new Path("runapp.cmd"), 
				//		TemplateMerger.merge("/bsbuilder/resources/web/js/vue/runapp-template.cmd", mapOfValues), monitor);
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF"), new Path("index.jsp"), 
						TemplateMerger.merge("/bsbuilder/resources/web/js/vue/vue_index-template.jsp", mapOfValues), monitor);
				
				//non-template files for build folder
				CommonUtils.addFileToProject(folders.get("src/ui/build"), new Path("build.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/vue/others/build/build.js"), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/build"), new Path("check-versions.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/vue/others/build/check-versions.js"), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/build"), new Path("dev-client.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/vue/others/build/dev-client.js"), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/build"), new Path("dev-server.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/vue/others/build/dev-server.js"), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/build"), new Path("utils.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/vue/others/build/utils.js"), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/build"), new Path("webpack.base.conf.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/vue/others/build/webpack.base.conf.js"), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/build"), new Path("webpack.dev.conf.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/vue/others/build/webpack.dev.conf.js"), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/build"), new Path("webpack.prod.conf.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/vue/others/build/webpack.prod.conf.js"), monitor);
				
				//non-template files for config folder
				CommonUtils.addFileToProject(folders.get("src/ui/config"), new Path("dev.env.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/vue/others/config/dev.env.js"), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/config"), new Path("test.env.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/vue/others/config/test.env.js"), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/config"), new Path("prod.env.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/vue/others/config/prod.env.js"), monitor);
				
				//static
				CommonUtils.addFileToProject(folders.get("src/ui/static"), new Path("jquery.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/jquery-1.10.2.min.js"), monitor);
				
				//non-template files for root folder
				CommonUtils.addFileToProject(folders.get("src/ui"), new Path(".babelrc"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/vue/root/x.babelrc"), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui"), new Path(".editorconfig"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/vue/root/x.editorconfig"), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui"), new Path(".eslintignore"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/vue/root/x.eslintignore"), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui"), new Path(".eslintrc.js"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/vue/root/x.eslintrc.js"), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui"), new Path("index.html"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/vue/root/index.html"), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui"), new Path("package.json"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/vue/root/package.json"), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui"), new Path("README.md"), 
						this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/vue/root/README.md"), monitor);
				
			}
			
			
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("localizedmessages.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/localizedmessages.js"), monitor);
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("ejs_fulljslint.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/ejs_fulljslint.js"), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("jquery.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/jquery-1.10.2.min.js"), monitor);
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("jquery-1.10.2.min.map"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/jquery-1.10.2.min.map"), monitor);
			
						
			//CommonUtils.CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("jquery.dataTables.js"), 
			//		this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/jquery.dataTables.js"), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("json2.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/json2.js"), monitor);
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("jquery.bootstrap.wizard.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/jquery.bootstrap.wizard.js"), monitor);
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("bootstrap.min.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/bootstrap.min.js"), monitor);
					
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/css/fonts"), new Path("glyphicons-halflings-regular.eot"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/fonts/glyphicons-halflings-regular.eot"), monitor);
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/css/fonts"), new Path("glyphicons-halflings-regular.svg"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/fonts/glyphicons-halflings-regular.svg"), monitor);
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/css/fonts"), new Path("glyphicons-halflings-regular.ttf"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/fonts/glyphicons-halflings-regular.ttf"), monitor);
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/css/fonts"), new Path("glyphicons-halflings-regular.woff"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/fonts/glyphicons-halflings-regular.woff"), monitor);
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/css/fonts"), new Path("glyphicons-halflings-regular.woff2"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/fonts/glyphicons-halflings-regular.woff2"), monitor);
			
			
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("respond.min.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/respond.min.js"), monitor);
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("html5shiv.min.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/html5shiv.min.js"), monitor);
			
			
			//ANOMALY, why does text.js have to be outside the libs folder
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js"), new Path("text.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/text.js"), monitor);
						
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/globals"), new Path("global.js"), 
					TemplateMerger.merge("/bsbuilder/resources/web/js/libs/global.js", mapOfValues), monitor);
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js"), new Path("main.js"), 
					TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/main/main-template.js", mapOfValues), monitor);
		
			
			addVariousSettings(folders.get(".settings"), proj, params.getBasePackageName(), params.getControllerPackageName(),
					monitor);
			
			/* Add web-xml file */
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF"), new Path("web.xml"),
					TemplateMerger.merge("/bsbuilder/resources/maven/web.xml-template", mapOfValues), monitor);
			/* Add Spring servlet dispathcer mapping file */
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF"), new Path("yourdispatcher-servlet.xml"),
					TemplateMerger.merge("/bsbuilder/resources/maven/yourdispatcher-servlet.xml-template", mapOfValues), monitor);
			
			/* Add Spring context files */
			//CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/spring"), new Path("applicationContext.xml"),
			//		TemplateMerger.merge("/bsbuilder/resources/maven/applicationContext.xml-template", proj.getName(),params.getBasePackageName(),params.getControllerPackageName(), params.getUtilPackageName()), monitor);			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/spring"), new Path("applicationContext.xml"),
					TemplateMerger.merge("/bsbuilder/resources/maven/applicationContext.xml-template", mapOfValues), monitor);			
			
			if((Boolean)mapOfValues.get("prepForOracle") || (Boolean)mapOfValues.get("prepForMySQL")){
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/spring"), new Path("datasource.xml"),
						TemplateMerger.merge("/bsbuilder/resources/maven/datasource.xml-template", mapOfValues), monitor);
			}
			
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/spring"), new Path("spring-security.xml"),
					TemplateMerger.merge("/bsbuilder/resources/maven/spring-security.xml-template", mapOfValues), monitor);
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/spring"), new Path("ehcache.xml"),
					TemplateMerger.merge("/bsbuilder/resources/maven/ehcache.xml-template", proj.getName(),params.getBasePackageName(),params.getControllerPackageName(), params.getUtilPackageName()), monitor);

			
			/* Add a java model */
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getDomainPackageName(), params.getDomainClassName(),
					params.getDomainClassSourceCode() , monitor);
			
			Map<String, Object> modelAttributes = pageThree.getModelAttributes();
			
			
			/*Add a default CSS */
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/css"), new Path("site.css"),
					TemplateMerger.merge("/bsbuilder/resources/css/site.css-template", mapOfValues), monitor);
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/css/libs"), new Path("bootstrap.min.css"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/css/bootstrap.min.css"), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/css/libs"), new Path("bootstrap-theme.min.css"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/css/bootstrap-theme.min.css"), monitor);
			
			
			/* Add Controllers*/
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getControllerPackageName() , "MainController", 
					params.getMainControllerSourceCode() , monitor);			
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getControllerPackageName(), params.getControllerClassName(), 
					params.getDomainControllerSourceCode(), monitor);
			
			/* Add Service */
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getServicePackageName(), params.getDomainClassName() + "Service",
					params.getServiceSourceCode() , monitor);
			
			if(params.isGenerateWebService()){
				CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getWebServicePackageName(), 
						params.getDomainClassName() + "WebService",
						params.getWebServiceInterfaceSourceCode() , monitor);	
				CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getWebServicePackageName(), 
						params.getDomainClassName() + "WebServiceImpl",
						params.getWebServiceImplSourceCode() , monitor);
			}			
			
			/* Add DAO */
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getDaoPackageName(), params.getDomainClassName() + "DAO",
					params.getDaoSourceCode() , monitor);
			
			if((Boolean)mapOfValues.get("prepForOracle") || (Boolean)mapOfValues.get("prepForMySQL")){
				CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getDaoPackageName()+ ".mapper", params.getDomainClassName() + "Mapper",
						params.getMapperSourceCode() , monitor);
				
				CommonUtils.createPackageAndClass(folders.get("src/main/resources"), params.getDaoPackageName()+ ".mapper", params.getDomainClassName() + "Mapper.xml",
						IOUtils.toString(TemplateMerger.merge("/bsbuilder/resources/java/mapper-template.xml", mapOfValues)) , monitor);
			}
			
			/* Add Security */
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getSecurityPackageName(), "SampleUserDetailsService",
					params.getSecurityUserDetailsServiceSourceCode() , monitor);
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getSecurityPackageName(), "SampleUserDetails",
					params.getSecurityUserDetailsSourceCode() , monitor);
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getSecurityPackageName(), "ClearSessionOnSMHeaderChange",
					params.getSmHeaderChangeSourceCode() , monitor);
		
			
			/* Add ListWrapper */
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getCommonPackageName(), "ListWrapper",
					params.getListWrapperSourceCode() , monitor);
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getCommonPackageName(), "NameValuePair",
					params.getNameValuePairSourceCode() , monitor);
			
			
			/* Add Resource Bundle wrapper */
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getUtilPackageName(), "ExposedResourceBundleMessageSource",
					params.getResourceBundleUtilSourceCode() , monitor);
			
			/* Add message bundles */
			CommonUtils.createPackageAndClass(folders.get("src/main/resources"), "locales", "messages_en.properties",
					params.getSampleMessageBundleContent() , monitor);
			CommonUtils.createPackageAndClass(folders.get("src/main/resources"), "locales", "messages_es.properties",
					params.getSampleMessageBundleContentEs() , monitor);
			
			CommonUtils.createPackageAndClass(folders.get("src/main/resources"), "", "ESAPI.properties",
					params.getSampleESAPIProperties() , monitor);
			
			CommonUtils.addFileToProject(folders.get("src/main/resources"), new Path("log4j.properties"),
					TemplateMerger.merge("/bsbuilder/resources/other/log4j.properties-template", proj.getName(),params.getBasePackageName(),params.getControllerPackageName(), params.getUtilPackageName()), monitor);

			CommonUtils.addFileToProject(folders.get("src/main/resources"), new Path("env_local.properties"),
					TemplateMerger.merge("/bsbuilder/resources/other/env_local.properties-template", proj.getName(),params.getBasePackageName(),params.getControllerPackageName(), params.getUtilPackageName()), monitor);

			
			CommonUtils.addFileToProject(container, new Path("readme.txt"),
					TemplateMerger.merge("/bsbuilder/resources/other/readme.txt-template", proj.getName(),params.getBasePackageName(),params.getControllerPackageName(), params.getUtilPackageName()), monitor);
	
			/* Add Test Data in an external text file*/
			StringWriter sampleDataStringWriter = new StringWriter();
			IOUtils.copy(TemplateMerger.merge("/bsbuilder/resources/other/sampledata.txt-template", mapOfValues), sampleDataStringWriter);
			CommonUtils.createPackageAndClass(folders.get("src/main/resources"), "sampledata", mapOfValues.get("domainClassName").toString() + "s.txt",
					 CommonUtils.cleanSampleData(sampleDataStringWriter.toString()), monitor);
			
			/* Add Test Mongo Data in an external text file*/
			if((Boolean)mapOfValues.get("useMongo")){
				StringWriter sampleMongoDataStringWriter = new StringWriter();
				IOUtils.copy(TemplateMerger.merge("/bsbuilder/resources/other/mongo-script.txt-template", mapOfValues), sampleMongoDataStringWriter);
				CommonUtils.createPackageAndClass(folders.get("src/main/resources/scripts"), "sampledata", mapOfValues.get("domainClassName").toString() + "s.txt",
					 CommonUtils.cleanSampleData(sampleMongoDataStringWriter.toString()), monitor);
			}
			
			//add junit for Controllers
			CommonUtils.createPackageAndClass(folders.get("src/test/java"), params.getControllerPackageName(),
					params.getControllerClassName() + "Test", params.getControllerTestSourceCode() , monitor);
			
			
			IJavaProject javaProject = JavaCore.create(proj);
			
			//Create classpath entries which really creates the ".classpath" file of the Eclipse project
			createClassPathEntries(folders.get("target/classes"), folders.get("target/test-classes"), folders.get("src/main/java"),
					folders.get("src/test/java"), folders.get("src/main/resources"), folders.get("src/test/resources"), javaProject);
			
			//add bsbuilder-specific settings
			addBSBuilderSettings(folders.get(".settings"), project, params.getBasePackageName(),params.isGenerateSecurityCode(),
					(Boolean)mapOfValues.get("useMongo"), (Boolean)mapOfValues.get("prepForOracle"), params.getUiType() , monitor);
			
		} catch (Throwable ioe) {
			IStatus status = new Status(IStatus.ERROR, "NewFileWizard", IStatus.OK,
					ioe.getLocalizedMessage(), null);
			throw new CoreException(status);
		} finally {
			monitor.done();
		}
	}
	
	private void createFolderStructures(IContainer container, IProgressMonitor monitor)
		throws Exception
	{
		//target
		IFolder outputFolder = container.getFolder(new Path("target"));
		outputFolder.create(true, true, monitor);
		folders.put("target", outputFolder);

		//target/classes
		IFolder outputFolder2 = outputFolder.getFolder(new Path("classes"));
		outputFolder2.create(true, true, monitor);
		folders.put("target/classes", outputFolder2);
		
		//target/test-classes
		IFolder outputFolder3 = outputFolder.getFolder(new Path("test-classes"));
		outputFolder3.create(true, true, monitor);	
		folders.put("target/test-classes", outputFolder3);

		//src
		IFolder srcFolder = container.getFolder(new Path("src"));
		
		//src/main/java
		srcFolder.create(false, true, new NullProgressMonitor());		
		IFolder srcFolder21 = srcFolder.getFolder(new Path("main"));
		srcFolder21.create(false, true, new NullProgressMonitor());
		IFolder srcFolder31 = srcFolder21.getFolder(new Path("java"));
		srcFolder31.create(false, true, new NullProgressMonitor());
		folders.put("src/main/java", srcFolder31);
		
		/************************* VueJS folders *******************************/
		//src/ui
		IFolder srcFolder23 = srcFolder.getFolder(new Path("ui"));
		srcFolder23.create(false, true, new NullProgressMonitor());
		folders.put("src/ui", srcFolder23);
		//src/ui/build
		IFolder srcFolder231 = srcFolder23.getFolder(new Path("build"));
		srcFolder231.create(false, true, new NullProgressMonitor());
		folders.put("src/ui/build", srcFolder231);
		
		IFolder srcFolder232 = srcFolder23.getFolder(new Path("config"));
		srcFolder232.create(false, true, new NullProgressMonitor());
		folders.put("src/ui/config", srcFolder232);
		
		IFolder srcFolder233 = srcFolder23.getFolder(new Path("node_modules"));
		srcFolder233.create(false, true, new NullProgressMonitor());
		folders.put("src/ui/node_modules", srcFolder233);
		
		IFolder srcFolder234 = srcFolder23.getFolder(new Path("src"));
		srcFolder234.create(false, true, new NullProgressMonitor());
		folders.put("src/ui/src", srcFolder234);
		
		IFolder srcFolder2341 = srcFolder234.getFolder(new Path("assets"));
		srcFolder2341.create(false, true, new NullProgressMonitor());
		folders.put("src/ui/src/assets", srcFolder2341);
		
		IFolder srcFolder2342 = srcFolder234.getFolder(new Path("components"));
		srcFolder2342.create(false, true, new NullProgressMonitor());
		folders.put("src/ui/src/components", srcFolder2342);
		
		IFolder srcFolder235 = srcFolder23.getFolder(new Path("static"));
		srcFolder235.create(false, true, new NullProgressMonitor());
		folders.put("src/ui/static", srcFolder235);
		
		IFolder srcFolder236 = srcFolder23.getFolder(new Path("test"));
		srcFolder236.create(false, true, new NullProgressMonitor());
		folders.put("src/ui/test", srcFolder236);
				
		/************* END of VueJS *******************************************/
		
		//src/main/resources
		IFolder srcFolder42 = srcFolder21.getFolder(new Path("resources"));
		srcFolder42.create(false, true, new NullProgressMonitor());
		folders.put("src/main/resources", srcFolder42);
		
		//src/main/resources
		IFolder srcFolder421 = srcFolder42.getFolder(new Path("scripts"));
		srcFolder421.create(false, true, new NullProgressMonitor());
		folders.put("src/main/resources/scripts", srcFolder421);
		
		//src/test/java
		IFolder srcFolder22 = srcFolder.getFolder(new Path("test"));
		srcFolder22.create(false, true, new NullProgressMonitor());
		IFolder srcFolder32 = srcFolder22.getFolder(new Path("java"));
		srcFolder32.create(false, true, new NullProgressMonitor());
		folders.put("src/test/java", srcFolder32);
		
		//src/test/resources
		IFolder srcFolder43 = srcFolder22.getFolder(new Path("resources"));
		srcFolder43.create(false, true, new NullProgressMonitor());
		folders.put("src/test/resources", srcFolder43);
		
		//src/main/webapp
		IFolder srcFolder41 = srcFolder21.getFolder(new Path("webapp"));
		srcFolder41.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp", srcFolder41);
		
		//src/main/webapp/WEB-INF
		IFolder srcFolder51 = srcFolder41.getFolder(new Path("WEB-INF"));
		srcFolder51.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF", srcFolder51);
		
		//.settings
		IFolder settingsFolder = container.getFolder(new Path(".settings"));
		settingsFolder.create(false, true, new NullProgressMonitor());
		folders.put(".settings", settingsFolder);
		
		//src/main/webapp/WEB-INF/spring
		IFolder springFolder = srcFolder51.getFolder(new Path("spring"));
		springFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/spring", springFolder);
		
		//src/main/webapp/WEB-INF/resources
		IFolder resourcesFolder = srcFolder51.getFolder(new Path("resources"));
		resourcesFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources", resourcesFolder);
		
		//src/main/webapp/WEB-INF/resources/css
		IFolder cssFolder = resourcesFolder.getFolder(new Path("css"));
		cssFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources/css", cssFolder);
		
		//src/main/webapp/WEB-INF/resources/css/libs
		IFolder thirdPartyCssFolder = cssFolder.getFolder(new Path("libs"));
		thirdPartyCssFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources/css/libs", thirdPartyCssFolder);
		
		//src/main/webapp/WEB-INF/resources/css/fonts
		IFolder thirdPartyFontsFolder = cssFolder.getFolder(new Path("fonts"));
		thirdPartyFontsFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources/css/fonts", thirdPartyFontsFolder);
		
		//src/main/webapp/WEB-INF/resources/js
		IFolder jsFolder = resourcesFolder.getFolder(new Path("js"));
		jsFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources/js", jsFolder);
		
		//src/main/webapp/WEB-INF/resources/js/angular_controllers
		IFolder angularControllerFolder = jsFolder.getFolder(new Path("angular_controllers"));
		angularControllerFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources/js/angular_controllers", angularControllerFolder);
		
		//src/main/webapp/WEB-INF/resources/js/angular_factories
		IFolder angularFactoriesFolder = jsFolder.getFolder(new Path("angular_factories"));
		angularFactoriesFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources/js/angular_factories", angularFactoriesFolder);
		
		//src/main/webapp/WEB-INF/resources/js/angular_templates
		IFolder angularTemplatesFolder = jsFolder.getFolder(new Path("angular_templates"));
		angularTemplatesFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources/js/angular_templates", angularTemplatesFolder);
				
		//src/main/webapp/WEB-INF/resources/js/angular_services
		IFolder angularServicesFolder = jsFolder.getFolder(new Path("angular_services"));
		angularServicesFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources/js/angular_services", angularServicesFolder);
		
		//src/main/webapp/WEB-INF/resources/js/fonts
		IFolder fontsFolder = jsFolder.getFolder(new Path("fonts"));
		fontsFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources/js/fonts", fontsFolder);
		
		//src/main/webapp/WEB-INF/resources/js/libs
		IFolder jsLibsFolder = jsFolder.getFolder(new Path("libs"));
		jsLibsFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources/js/libs", jsLibsFolder);
		
		//src/main/webapp/WEB-INF/resources/js/models
		IFolder modelsFolder = jsFolder.getFolder(new Path("models"));
		modelsFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources/js/models", modelsFolder);
		
		//src/main/webapp/WEB-INF/resources/js/collections
		IFolder collectionsFolder = jsFolder.getFolder(new Path("collections"));
		collectionsFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources/js/collections", collectionsFolder);
		
		//src/main/webapp/WEB-INF/resources/js/globals
		IFolder globalsFolder = jsFolder.getFolder(new Path("globals"));
		globalsFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources/js/globals", globalsFolder);
		
		//src/main/webapp/WEB-INF/resources/js/views
		IFolder viewsFolder = jsFolder.getFolder(new Path("views"));
		viewsFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources/js/views", viewsFolder);
		
		//src/main/webapp/WEB-INF/resources/js/presenters
		IFolder presentersFolder = jsFolder.getFolder(new Path("presenters"));
		presentersFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources/js/presenters", presentersFolder);
		
		//src/main/webapp/WEB-INF/resources/js/templates
		IFolder templatesFolder = jsFolder.getFolder(new Path("templates"));
		templatesFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources/js/templates", templatesFolder);
	}
	
	
	


	private void createClassPathEntries(IFolder outputFolder2,
			IFolder outputFolder3, IFolder srcFolder31, IFolder srcFolder32,
			IFolder mainResourcesFolder, IFolder testResourcesFolder,
			IJavaProject javaProject) throws JavaModelException {
		IClasspathEntry javasrc = JavaCore.newSourceEntry(srcFolder31.getFullPath(), null, null, outputFolder2.getFullPath(), 
				new IClasspathAttribute[] { 
					JavaCore.newClasspathAttribute("optional", "true"),
					JavaCore.newClasspathAttribute("maven.pomderived", "true")
					});
		
		IClasspathEntry mainResources = JavaCore.newSourceEntry(mainResourcesFolder.getFullPath(), null, null, outputFolder2.getFullPath(), 
				new IClasspathAttribute[] { 
					JavaCore.newClasspathAttribute("optional", "true"),
					JavaCore.newClasspathAttribute("maven.pomderived", "true")
					});
		
		IClasspathEntry testsrc = JavaCore.newSourceEntry(srcFolder32.getFullPath(), null, null, outputFolder3.getFullPath(), 
				new IClasspathAttribute[] { 
					JavaCore.newClasspathAttribute("optional", "true"),
					JavaCore.newClasspathAttribute("maven.pomderived", "true")
					});
		IClasspathEntry testResources = JavaCore.newSourceEntry(testResourcesFolder.getFullPath(), null, null, outputFolder3.getFullPath(), 
				new IClasspathAttribute[] { 
					JavaCore.newClasspathAttribute("optional", "true"),
					JavaCore.newClasspathAttribute("maven.pomderived", "true")
					});
		
		IClasspathEntry jre = JavaCore.newContainerEntry(new Path("org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/J2SE-1.5"),
				new IAccessRule[0], 
				new IClasspathAttribute[] { JavaCore.newClasspathAttribute("maven.pomderived", "true")}, false);
		
		IClasspathEntry mavenContainer = JavaCore.newContainerEntry(new Path("org.eclipse.m2e.MAVEN2_CLASSPATH_CONTAINER"),
				new IAccessRule[0], 
				new IClasspathAttribute[] { 
						JavaCore.newClasspathAttribute("maven.pomderived", "true"),
			 			JavaCore.newClasspathAttribute("org.eclipse.jst.component.dependency", "/WEB-INF/lib")
					}, false);			

		IClasspathEntry[] entries = new IClasspathEntry[] { javasrc,testsrc, mainResources, testResources ,jre, mavenContainer };			
		javaProject.setRawClasspath(entries, outputFolder2.getFullPath(), new NullProgressMonitor());
	}

	
	private void addVariousSettings(IFolder settingsFolder, IProject project, String basePackageName, String controllerPackageName,IProgressMonitor monitor)
		throws Exception{
		//TODO Add value substitution capability for setting values within settings files
		InputStream jdtCorePref = this.getClass().getResourceAsStream(
				"/bsbuilder/resources/settings/org.eclipse.jdt.core.prefs.template");
		CommonUtils.addFileToProject(settingsFolder, new Path("org.eclipse.jdt.core.prefs"),
				jdtCorePref, monitor);
		
		InputStream m2eCorePref = this.getClass().getResourceAsStream(
				"/bsbuilder/resources/settings/org.eclipse.m2e.core.prefs.template");
		CommonUtils.addFileToProject(settingsFolder, new Path("org.eclipse.m2e.core.prefs"),
				m2eCorePref, monitor);
		
		//InputStream wstCommonComponent = this.getClass().getResourceAsStream(
		//		"/bsbuilder/resources/settings/org.eclipse.wst.common.component.template");
		CommonUtils.addFileToProject(settingsFolder, new Path("org.eclipse.wst.common.component"),
				TemplateMerger.merge("/bsbuilder/resources/settings/org.eclipse.wst.common.component.template", project.getName(),basePackageName,controllerPackageName, ""), monitor);
		
		InputStream wstCommonProject = this.getClass().getResourceAsStream(
				"/bsbuilder/resources/settings/org.eclipse.wst.common.project.facet.core.xml.template");
		CommonUtils.addFileToProject(settingsFolder, new Path("org.eclipse.wst.common.project.facet.core.xml"),
				wstCommonProject, monitor);
		
		InputStream wstJsdtContainer = this.getClass().getResourceAsStream(
				"/bsbuilder/resources/settings/org.eclipse.wst.jsdt.ui.superType.container.template");
		CommonUtils.addFileToProject(settingsFolder, new Path("org.eclipse.wst.jsdt.ui.superType.container"),
				wstJsdtContainer, monitor);
		
		InputStream wstJsdtName = this.getClass().getResourceAsStream(
				"/bsbuilder/resources/settings/org.eclipse.wst.jsdt.ui.superType.name.template");
		CommonUtils.addFileToProject(settingsFolder, new Path("org.eclipse.wst.jsdt.ui.superType.name"),
				wstJsdtName, monitor);
		
		InputStream jsdtScope = this.getClass().getResourceAsStream(
				"/bsbuilder/resources/settings/jsdtscope.template");
		CommonUtils.addFileToProject(settingsFolder, new Path(".jsdtscope"),
				jsdtScope, monitor);
	}
	
	private void addBSBuilderSettings(IFolder settingsFolder, IProject project, 
			String basePackageName, boolean secureEnabled, boolean useMongo, boolean prepForOracle,String uiType,IProgressMonitor monitor)
			throws Exception{		
		//String props = "basePackage=" + basePackageName;
		StringWriter properties = new StringWriter();
		properties.append("basePackage=" + basePackageName);		
		properties.append("\nsecureCodeEnabled=" + secureEnabled);
		properties.append("\nuseMongo=" + useMongo);
		properties.append("\nuiType=" + uiType);
		properties.append("\nprepForOracle=" + prepForOracle);
		
        InputStream stream = new ByteArrayInputStream(properties.toString().getBytes());
		CommonUtils.addFileToProject(settingsFolder, new Path("org.bsbuilder.settings"),
				stream, monitor);
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 *      org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		this.workbench = workbench;
	}

	/**
	 * Sets the initialization data for the wizard.
	 */
	@Override
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		this.config = config;
	}
	
	public String createComponentTag(String domainClassName){
		String newTagname = StringUtils.uncapitalize(domainClassName);
		int indexOfUppercase = lastIndexOfUCL(newTagname);
		if(indexOfUppercase > -1){
			newTagname = newTagname.substring(0, indexOfUppercase) + "-" +
				newTagname.substring(indexOfUppercase).toLowerCase();
		}else{
			newTagname = newTagname.toLowerCase();
		}
		return newTagname;
	}
	
	public int lastIndexOfUCL(String str) {        
	    for(int i=str.length()-1; i>=0; i--) {
	        if(Character.isUpperCase(str.charAt(i))) {
	            return i;
	        }
	    }
	    return -1;
	}

	public class SourceCodeGeneratorParameters{
		private String basePackageName; 
		private String controllerPackageName; 
		private String mainControllerSourceCode;
		private String controllerClassName; 			
		private String domainControllerSourceCode;
		private String controllerTestSourceCode;
		private String domainPackageName; 
		private String domainClassName; 
		private String domainClassSourceCode;
		private String domainClassIdAttributeName;
		private String servicePackageName;
		private String serviceSourceCode;
		
		private boolean generateWebService;
		private String webServicePackageName;
		private String webServiceInterfaceSourceCode;
		private String webServiceImplSourceCode;
		
		private String daoPackageName;
		private String daoSourceCode;
		private String mapperSourceCode;
		private String commonPackageName;
		private String listWrapperSourceCode;
		private String nameValuePairSourceCode;
		private String securityPackageName;
		private String securityUserDetailsServiceSourceCode;
		private String securityUserDetailsSourceCode;
		private String smHeaderChangeSourceCode;
		private boolean generateSecurityCode;
		private String securityAspectCode;
		private String securityEnumCode;
		private String securityAnnotationCode;
		private String securedDomainCode;
		private String securityTokenGeneratorCode;
		private String utilPackageName;
		private String resourceBundleUtilSourceCode;
		private String sampleMessageBundleContent;
		private String sampleMessageBundleContentEs;
		private String sampleESAPIProperties;
		private boolean isJSPTemplate = true;
		private boolean injectLocalizedMessages;
		private String uiType;
		
		
		public String getUiType() {
			return uiType;
		}
		public void setUiType(String uiType) {
			this.uiType = uiType;
		}
		public String getBasePackageName() {
			return basePackageName;
		}
		public void setBasePackageName(String basePackageName) {
			this.basePackageName = basePackageName;
		}
		public String getControllerPackageName() {
			return controllerPackageName;
		}
		public void setControllerPackageName(String controllerPackageName) {
			this.controllerPackageName = controllerPackageName;
		}
		public String getMainControllerSourceCode() {
			return mainControllerSourceCode;
		}
		public void setMainControllerSourceCode(String mainControllerSourceCode) {
			this.mainControllerSourceCode = mainControllerSourceCode;
		}
		public String getControllerClassName() {
			return controllerClassName;
		}
		public void setControllerClassName(String controllerClassName) {
			this.controllerClassName = controllerClassName;
		}
		public String getDomainControllerSourceCode() {
			return domainControllerSourceCode;
		}
		public void setDomainControllerSourceCode(String domainControllerSourceCode) {
			this.domainControllerSourceCode = domainControllerSourceCode;
		}
		public String getControllerTestSourceCode() {
			return controllerTestSourceCode;
		}
		public void setControllerTestSourceCode(String controllerTestSourceCode) {
			this.controllerTestSourceCode = controllerTestSourceCode;
		}
		public String getDomainPackageName() {
			return domainPackageName;
		}
		public void setDomainPackageName(String domainPackageName) {
			this.domainPackageName = domainPackageName;
		}
		public String getDomainClassName() {
			return domainClassName;
		}
		public void setDomainClassName(String domainClassName) {
			this.domainClassName = domainClassName;
		}
		public String getDomainClassSourceCode() {
			return domainClassSourceCode;
		}
		public void setDomainClassSourceCode(String domainClassSourceCode) {
			this.domainClassSourceCode = domainClassSourceCode;
		}
		public String getDomainClassIdAttributeName() {
			return domainClassIdAttributeName;
		}
		public void setDomainClassIdAttributeName(String domainClassIdAttributeName) {
			this.domainClassIdAttributeName = domainClassIdAttributeName;
		}
		public String getServicePackageName() {
			return servicePackageName;
		}
		public void setServicePackageName(String servicePackageName) {
			this.servicePackageName = servicePackageName;
		}
		public String getServiceSourceCode() {
			return serviceSourceCode;
		}
		public void setServiceSourceCode(String serviceSourceCode) {
			this.serviceSourceCode = serviceSourceCode;
		}
		public String getDaoPackageName() {
			return daoPackageName;
		}
		public void setDaoPackageName(String daoPackageName) {
			this.daoPackageName = daoPackageName;
		}
		public String getDaoSourceCode() {
			return daoSourceCode;
		}
		public void setDaoSourceCode(String daoSourceCode) {
			this.daoSourceCode = daoSourceCode;
		}
		public String getListWrapperSourceCode() {
			return listWrapperSourceCode;
		}
		public void setListWrapperSourceCode(String listWrapperSourceCode) {
			this.listWrapperSourceCode = listWrapperSourceCode;
		}
		public String getCommonPackageName() {
			return commonPackageName;
		}
		public void setCommonPackageName(String commonPackageName) {
			this.commonPackageName = commonPackageName;
		}
		public String getSecurityPackageName() {
			return securityPackageName;
		}
		public void setSecurityPackageName(String securityPackageName) {
			this.securityPackageName = securityPackageName;
		}
		public String getSecurityUserDetailsServiceSourceCode() {
			return securityUserDetailsServiceSourceCode;
		}
		public void setSecurityUserDetailsServiceSourceCode(
				String securityUserDetailsServiceSourceCode) {
			this.securityUserDetailsServiceSourceCode = securityUserDetailsServiceSourceCode;
		}
		public String getSampleMessageBundleContent() {
			return sampleMessageBundleContent;
		}
		public void setSampleMessageBundleContent(String sampleMessageBundleContent) {
			this.sampleMessageBundleContent = sampleMessageBundleContent;
		}
		public String getSampleMessageBundleContentEs() {
			return sampleMessageBundleContentEs;
		}
		public void setSampleMessageBundleContentEs(String sampleMessageBundleContentEs) {
			this.sampleMessageBundleContentEs = sampleMessageBundleContentEs;
		}
		public String getUtilPackageName() {
			return utilPackageName;
		}
		public void setUtilPackageName(String utilPackageName) {
			this.utilPackageName = utilPackageName;
		}
		public String getResourceBundleUtilSourceCode() {
			return resourceBundleUtilSourceCode;
		}
		public void setResourceBundleUtilSourceCode(String resourceBundleUtilSourceCode) {
			this.resourceBundleUtilSourceCode = resourceBundleUtilSourceCode;
		}
		public String getSecurityUserDetailsSourceCode() {
			return securityUserDetailsSourceCode;
		}
		public void setSecurityUserDetailsSourceCode(
				String securityUserDetailsSourceCode) {
			this.securityUserDetailsSourceCode = securityUserDetailsSourceCode;
		}
		public String getSampleESAPIProperties() {
			return sampleESAPIProperties;
		}
		public void setSampleESAPIProperties(String sampleESAPIProperties) {
			this.sampleESAPIProperties = sampleESAPIProperties;
		}
		public String getSecurityAspectCode() {
			return securityAspectCode;
		}
		public void setSecurityAspectCode(String securityAspectCode) {
			this.securityAspectCode = securityAspectCode;
		}
		public String getSecuredDomainCode() {
			return securedDomainCode;
		}
		public void setSecuredDomainCode(String securedDomainCode) {
			this.securedDomainCode = securedDomainCode;
		}
		public String getSecurityEnumCode() {
			return securityEnumCode;
		}
		public void setSecurityEnumCode(String securityEnumCode) {
			this.securityEnumCode = securityEnumCode;
		}
		public String getSecurityAnnotationCode() {
			return securityAnnotationCode;
		}
		public void setSecurityAnnotationCode(String securityAnnotationCode) {
			this.securityAnnotationCode = securityAnnotationCode;
		}
		public String getSecurityTokenGeneratorCode() {
			return securityTokenGeneratorCode;
		}
		public void setSecurityTokenGeneratorCode(String securityTokenGeneratorCode) {
			this.securityTokenGeneratorCode = securityTokenGeneratorCode;
		}
		public boolean isJSPTemplate() {
			return isJSPTemplate;
		}
		public void setJSPTemplate(boolean isJSPTemplate) {
			this.isJSPTemplate = isJSPTemplate;
		}
		public boolean isGenerateSecurityCode() {
			return generateSecurityCode;
		}
		public void setGenerateSecurityCode(boolean generateSecurityCode) {
			this.generateSecurityCode = generateSecurityCode;
		}
		public boolean isInjectLocalizedMessages() {
			return injectLocalizedMessages;
		}
		public void setInjectLocalizedMessages(boolean injectLocalizedMessages) {
			this.injectLocalizedMessages = injectLocalizedMessages;
		}
		public String getNameValuePairSourceCode() {
			return nameValuePairSourceCode;
		}
		public void setNameValuePairSourceCode(String nameValuePairSourceCode) {
			this.nameValuePairSourceCode = nameValuePairSourceCode;
		}
		
		
		public boolean isGenerateWebService() {
			return generateWebService;
		}
		public void setGenerateWebService(boolean generateWebService) {
			this.generateWebService = generateWebService;
		}
		public String getWebServicePackageName() {
			return webServicePackageName;
		}
		public void setWebServicePackageName(String webServicePackageName) {
			this.webServicePackageName = webServicePackageName;
		}
		public String getWebServiceInterfaceSourceCode() {
			return webServiceInterfaceSourceCode;
		}
		public void setWebServiceInterfaceSourceCode(
				String webServiceInterfaceSourceCode) {
			this.webServiceInterfaceSourceCode = webServiceInterfaceSourceCode;
		}
		public String getWebServiceImplSourceCode() {
			return webServiceImplSourceCode;
		}
		public void setWebServiceImplSourceCode(String webServiceImplSourceCode) {
			this.webServiceImplSourceCode = webServiceImplSourceCode;
		}
		public String getSmHeaderChangeSourceCode() {
			return smHeaderChangeSourceCode;
		}
		public void setSmHeaderChangeSourceCode(String smHeaderChangeSourceCode) {
			this.smHeaderChangeSourceCode = smHeaderChangeSourceCode;
		}
		public String getMapperSourceCode() {
			return mapperSourceCode;
		}
		public void setMapperSourceCode(String mapperSourceCode) {
			this.mapperSourceCode = mapperSourceCode;
		}
		
		
	}

}