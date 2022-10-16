package vasbootbuilder.wizards.site;

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

import vasbootbuilder.wizards.site.utils.CommonUtils;
import vasbootbuilder.wizards.site.utils.TemplateMerger;

public class NewBackboneSpringProjectWizard extends Wizard implements INewWizard, IExecutableExtension {

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
		 * Unlike the custom new wizard, we just add the pre-defined one and don't
		 * necessarily define our own.
		 */
		wizardPage = new WizardNewProjectCreationPage("NewExampleComSiteProject");
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

		try {
			if (project != null) {
				return true;
			}

			final IProject projectHandle = wizardPage.getProjectHandle();

			URI projectURI = (!wizardPage.useDefaults()) ? wizardPage.getLocationURI() : null;

			IWorkspace workspace = ResourcesPlugin.getWorkspace();

			final IProjectDescription desc = workspace.newProjectDescription(projectHandle.getName());

			desc.setLocationURI(projectURI);

			// The following all go in the .project file
			desc.setNatureIds(new String[] { "org.eclipse.jem.workbench.JavaEMFNature",
					"org.eclipse.wst.common.modulecore.ModuleCoreNature", "org.eclipse.jdt.core.javanature",
					"org.eclipse.m2e.core.maven2Nature", "org.eclipse.wst.common.project.facet.core.nature",
					"org.eclipse.wst.jsdt.core.jsNature" });
			ICommand[] commands = new ICommand[] { desc.newCommand(), desc.newCommand(), desc.newCommand(),
					desc.newCommand(), desc.newCommand() };
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
			// final String utilPackageName = pageTwo.getBasePackageName() + ".util";

			final String domainClassName = pageThree.getDomainClassName();
			final String domainClassIdAttributeName = pageThree.getDomainClassAttributeName();
			final String servicePackageName = pageTwo.getBasePackageName() + ".service";
            final String webServicePackageName = pageTwo.getBasePackageName() + ".webservice";
            final String daoPackageName = pageTwo.getBasePackageName() + ".dao";
            final String commonPackageName = pageTwo.getBasePackageName() + ".common";
            final String securityPackageName = pageTwo.getBasePackageName() + ".security";

			final Map<String, Object> mapOfValues = new HashMap<String, Object>();
			mapOfValues.put("projectName", projectHandle.getName());
			mapOfValues.put("domainPackageName", domainPackageName);
			mapOfValues.put("commonPackageName", commonPackageName);
            
			
			mapOfValues.put("domainClassName", domainClassName);
			mapOfValues.put("domainObjectName", domainClassName.substring(0, 1).toLowerCase() + domainClassName.substring(1));
			mapOfValues.put("domainClassIdAttributeName", domainClassIdAttributeName);
			mapOfValues.put("basePackageName", basePackageName);
			mapOfValues.put("utilPackageName", basePackageName + ".util");
			mapOfValues.put("secured", xssSelected || csrfSelected);
			mapOfValues.put("useMongo", pageTwo.useMongoDB());
			mapOfValues.put("mongoHostName", pageTwo.getMongoHostName());
			mapOfValues.put("mongoPort", pageTwo.getMongoPort());
			mapOfValues.put("mongoDBName", pageTwo.getMongoDBName());
			mapOfValues.put("templateType", pageFive.isJSPTemplate() ? "JSP" : "HTML");
			mapOfValues.put("injectMessages", pageFive.injectLocalizedMessages());

			mapOfValues.put("useMongo", pageTwo.useMongoDB());
			mapOfValues.put("mongoDBName", "localdb");
			mapOfValues.put("mongoHostName", pageTwo.getMongoHostName());
			mapOfValues.put("mongoPort", pageTwo.getMongoPort());
			mapOfValues.put("mongoDBName", pageTwo.getMongoDBName());
			mapOfValues.put("prepForOracle", pageTwo.prepForOracle());
			mapOfValues.put("prepForMySQL", pageTwo.prepForMySql());
			mapOfValues.put("prepForHSQL", pageTwo.prepForHSQL());
			mapOfValues.put("oracleHost", pageTwo.getOracleHost());
			mapOfValues.put("oraclePort", pageTwo.getOraclePort());
			mapOfValues.put("oracleInstance", pageTwo.getOracleInstance());
			mapOfValues.put("oracleUser", pageTwo.getOracleUser());
			mapOfValues.put("oraclePassword", pageTwo.getOraclePassword());

			mapOfValues.put("addWebService", pageSix.addWebServiceFeature());
			mapOfValues.put("springVersion", pageTwo.getSpringVersion());

			final String domainClassSourceCode = pageThree.getClassSource(mapOfValues);
			mapOfValues.put("attrs", pageThree.getModelAttributes());
			mapOfValues.put("oracleNames", 
			        pageThree.getOracleDerivedNamesForTableAndAttrs((Boolean) mapOfValues.get("prepForOracle") ||
			                (Boolean) mapOfValues.get("prepForHSQL")));
			mapOfValues.put("fieldTypes", pageThree.getFieldTypes());
			mapOfValues.put("vueComponentTagName", createComponentTag(domainClassName));
			mapOfValues.put("uiType", pageFive.getUIType());

			final String controllerClassName = domainClassName + "Controller";
			// final String mainControllerSourceCode =
			// pageThree.getMainControllerSource(controllerPackageName, utilPackageName);
			// final String domainControllerSourceCode =
			// pageThree.getControllerSource(basePackageName, controllerPackageName,
			// domainClassName, domainClassIdAttributeName);
			//final String controllerTestSourceCode = pageThree.getControllerTestSource(basePackageName,
			//		controllerPackageName, domainClassName);
			
			final String controllerTestSourceCode = pageThree.buildSourceCode(mapOfValues, "controllerTest.java-template");

			final SourceCodeGeneratorParameters params = new SourceCodeGeneratorParameters();
			params.setBasePackageName(basePackageName);
			params.setControllerPackageName(controllerPackageName);
			params.setDomainPackageName(domainPackageName);
			params.setDomainClassName(domainClassName);
			params.setDomainClassSourceCode(domainClassSourceCode);
			params.setDomainClassIdAttributeName(domainClassIdAttributeName);
			params.setControllerClassName(controllerClassName);
			params.setSpringBootStarterClassName("SpringBootStarter");
			params.setSpringBootStarterSourceCode(
					pageThree.buildSourceCode(mapOfValues, "spring-boot-starter.java-template"));
			params.setMainControllerSourceCode(
					pageThree.buildSourceCode(mapOfValues, "common-controller.java-template"));	
			params.setMiscControllerSourceCode(pageThree.buildSourceCode(mapOfValues, "misc-controller.java-template"));
			
			params.setBaseControllerSourceCode(
                    pageThree.buildSourceCode(mapOfValues, "basecontroller.java-template"));
			params.setDomainControllerSourceCode(pageThree.buildSourceCode(mapOfValues, "controller.java-template"));
			params.setControllerTestSourceCode(controllerTestSourceCode);
			params.setServicePackageName(servicePackageName);
			// params.setServiceSourceCode(pageThree.getSeviceSourceCode(basePackageName,
			// servicePackageName, domainClassName, domainClassIdAttributeName));
			params.setServiceSourceCode(pageThree.buildSourceCode(mapOfValues, "service.java-template"));

			params.setDaoPackageName(daoPackageName);
			params.setDaoSourceCode(pageThree.buildSourceCode(mapOfValues, "jpa-repository.java-template"));
			//params.setMapperSourceCode(pageThree.buildSourceCode(mapOfValues, "mapper.java-template"));
			

			params.setCommonPackageName(commonPackageName);
			params.setListWrapperSourceCode(
					pageThree.getListWrapperSourceCode(basePackageName, commonPackageName, domainClassName));
			params.setMainConfigSourceCode(pageThree.buildSourceCode(mapOfValues, "mainconfig.java-template"));
			
			params.setNameValuePairSourceCode(
					pageThree.getNameValueSourceCode(basePackageName, commonPackageName, domainClassName));
			params.setSecurityPackageName(securityPackageName);
			params.setSecurityUserDetailsServiceSourceCode(
					pageThree.getSecurityUserDetailsServiceSourceCode(securityPackageName, domainClassName));
			params.setSecurityUserDetailsSourceCode(
					pageThree.getSecurityUserDetailsSourceCode(securityPackageName, domainClassName));
			params.setCustomLogoutSuccessHandlerSourceCode(pageThree.getCustomLogoutSuccessHandlerSourceCode(securityPackageName, domainClassName));
			
			params.setSpringSecurityConfigSourceCode(
					pageThree.getSpringSecurityConfigSourceCode(securityPackageName, domainClassName));
			params.setSpringSecurityAuhenticationProvider(
					pageThree.getSpringSecurityAuthProviderSourceCode(securityPackageName, domainClassName));

			if (xssSelected || csrfSelected) {
				params.setGenerateSecurityCode(true);
				params.setSecurityAspectCode(pageThree.buildSourceCode(mapOfValues, "security-aspect.java-template"));
				params.setSecuredDomainCode(pageThree.buildSourceCode(mapOfValues, "security-domain.java-template"));
			}
			params.setSecurityEnumCode(
					pageThree.buildSourceCode(mapOfValues, "security-annotation-type.java-template"));
			params.setSecurityAnnotationCode(
					pageThree.buildSourceCode(mapOfValues, "security-field-annotation.java-template"));
			params.setSecurityTokenGeneratorCode(
					pageThree.buildSourceCode(mapOfValues, "security-token-generator.java-template"));
			params.setSmHeaderChangeSourceCode(
					pageThree.buildSourceCode(mapOfValues, "clear-session-on-sm-header-change.java-template"));

			params.setSampleMessageBundleContent(pageThree.getMessageBundleContent("", "", ""));
			params.setSampleMessageBundleContentEs(pageThree.getMessageBundleContentEs("", "", ""));
			params.setUtilPackageName(basePackageName + ".util");
			// params.setResourceBundleUtilSourceCode(pageThree.getResourceBundleSourceCode(utilPackageName));
			params.setResourceBundleUtilSourceCode(
					pageThree.buildSourceCode(mapOfValues, "exposed-resource-bundle.java-template"));

			params.setSampleESAPIProperties(
					CommonUtils.linesToString(
							IOUtils.readLines(
									getClass().getResourceAsStream("/vasbootbuilder/resources/esapi/ESAPI.properties")),
							"\n"));
			params.setJSPTemplate(pageFive.isJSPTemplate());
			params.setInjectLocalizedMessages(pageFive.injectLocalizedMessages());
			params.setUiType(pageFive.getUIType());

			params.setGenerateWebService(pageSix.addWebServiceFeature());
			params.setWebServicePackageName(webServicePackageName);
			params.setWebServiceInterfaceSourceCode(
					pageThree.buildSourceCode(mapOfValues, "webserviceinterface.java-template"));
			params.setWebServiceImplSourceCode(pageThree.buildSourceCode(mapOfValues, "webserviceimpl.java-template"));
			System.out.println("JSP?*******************************" + pageFive.isJSPTemplate());
			System.out.println("HTML?*******************************" + pageFive.isHTMLTemplate());

			/*
			 * Just like the NewFileWizard, but this time with an operation object that
			 * modifies workspaces.
			 */
			WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
				protected void execute(IProgressMonitor monitor) throws CoreException {
					createProject(desc, projectHandle, params, mapOfValues, monitor);
				}
			};

			/*
			 * This isn't as robust as the code in the BasicNewProjectResourceWizard class.
			 * Consider beefing this up to improve error handling.
			 */
			try {
				getContainer().run(true, true, op);
			} catch (InterruptedException e) {
				return false;
			} catch (InvocationTargetException e) {
				Throwable realException = e.getTargetException();
				MessageDialog.openError(getShell(), "Error", realException.getMessage());
				return false;
			}

			project = projectHandle;

			if (project == null) {
				return false;
			}

			BasicNewProjectResourceWizard.updatePerspective(config);
			BasicNewProjectResourceWizard.selectAndReveal(project, workbench.getActiveWorkbenchWindow());

		} catch (Exception e) {
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
	void createProject(IProjectDescription description, IProject proj, SourceCodeGeneratorParameters params,
			Map<String, Object> mapOfValues, IProgressMonitor monitor)
			throws CoreException, OperationCanceledException {
		try {

			monitor.beginTask("", 2000);
			proj.create(description, new SubProgressMonitor(monitor, 1000));

			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}

			proj.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(monitor, 1000));

			/*
			 * Okay, now we have the project and we can do more things with it before
			 * updating the perspective.
			 */
			IContainer container = (IContainer) proj;

			/* Add an pom file */
			CommonUtils.addFileToProject(container, new Path("pom.xml"),
					TemplateMerger.merge("/vasbootbuilder/resources/maven/pom.xml-template", mapOfValues), monitor);
			
			/* Add Gradle scripts*/
			CommonUtils.addFileToProject(container, new Path("build.gradle"),
                    TemplateMerger.merge("/vasbootbuilder/resources/gradle/build-template.gradle", mapOfValues), monitor);
			CommonUtils.addFileToProject(container, new Path("settings.gradle"),
                    TemplateMerger.merge("/vasbootbuilder/resources/gradle/settings-template.gradle", mapOfValues), monitor);

			// call create folders here
			createFolderStructures(container, monitor, params.getUiType());
			//String domainName = params.getDomainClassName().toLowerCase();
			String domainName = params.getDomainClassName().substring(0, 1).toLowerCase() +
					params.getDomainClassName().substring(1);

			// add 3rd party JS libs
			if (params.getUiType().equalsIgnoreCase("BackboneJS")) {
				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources"), new Path("r.js"),
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/libs/r.js"), monitor);
				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/libs"),
						new Path("require.js"),
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/libs/require.js"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/libs"),
						new Path("backbone.js"),
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/libs/backbone.js"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/libs"),
						new Path("underscore.js"),
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/libs/underscore-min.js"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/libs"),
						new Path("backgrid.js"),
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/libs/backgrid.js"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/css/libs"),
						new Path("backgrid.css"),
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/libs/backgrid.css"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/css/libs"),
						new Path("backgrid-paginator.css"), this.getClass().getResourceAsStream(
								"/vasbootbuilder/resources/web/js/libs/backgrid-paginator.css"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/libs"),
						new Path("backgrid-paginator.js"), this.getClass().getResourceAsStream(
								"/vasbootbuilder/resources/web/js/libs/backgrid-paginator.js"),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/css/libs"),
						new Path("backgrid-select-all.css"), this.getClass().getResourceAsStream(
								"/vasbootbuilder/resources/web/js/libs/backgrid-select-all.css"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/libs"),
						new Path("backgrid-select-all.js"), this.getClass().getResourceAsStream(
								"/vasbootbuilder/resources/web/js/libs/backgrid-select-all.js"),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/libs"),
						new Path("backbone-pageable.js"), this.getClass().getResourceAsStream(
								"/vasbootbuilder/resources/web/js/libs/backbone-pageable.js"),
						monitor);
				//
				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/libs"),
						new Path("bootstrap-datepicker.js"), this.getClass().getResourceAsStream(
								"/vasbootbuilder/resources/web/js/libs/bootstrap-datepicker.js"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/css/libs"),
						new Path("datepicker.css"),
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/libs/datepicker.css"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/libs"),
						new Path("backbone.global.js"),
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/libs/backbone.global.js"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/models"),
						new Path(params.getDomainClassName() + "Model.js"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/backbone/models/model-template.js",
								mapOfValues),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/collections"),
						new Path(params.getDomainClassName() + "Collection.js"),
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/backbone/collections/collection-template.js",
								mapOfValues),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/views"),
						new Path(params.getDomainClassName() + "EditView.js"), TemplateMerger
								.merge("/vasbootbuilder/resources/web/js/backbone/views/view-template.js", mapOfValues),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/views"),
						new Path(params.getDomainClassName() + "CollectionView.js"),
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/backbone/views/collection-view-template.js",
								mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources"),
						new Path("buildconfig.js"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/backbone/main/buildconfig-template.js",
								mapOfValues),
						monitor);

				CommonUtils.addFileToProject(
						folders.get("src/main/resources/public/resources/js"), new Path("app.js"), TemplateMerger
								.merge("/vasbootbuilder/resources/web/js/backbone/main/app-template.js", mapOfValues),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js"),
						new Path("router.js"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/backbone/routers/router-template.js",
								mapOfValues),
						monitor);

				/*
				 * Add a backbone template file. This is dependent on the Java Model generation.
				 * Instead of using plain html, we are going to use JSP so we can use Spring's
				 * Message Bundles for localization.
				 */
				Path listTemplatePath;
				Path editTemplatePath;
				// Path presenterTemplatePath;
				if (params.isJSPTemplate()) {
					listTemplatePath = new Path(params.getDomainClassName() + "ListTemplate.jsp");
					editTemplatePath = new Path(params.getDomainClassName() + "EditTemplate.jsp");
					// presenterTemplatePath = new Path(params.getDomainClassName() +
					// "PresenterTemplate.jsp");
				} else {
					listTemplatePath = new Path(params.getDomainClassName() + "ListTemplate.htm");
					editTemplatePath = new Path(params.getDomainClassName() + "EditTemplate.htm");
					// presenterTemplatePath = new Path(params.getDomainClassName() +
					// "PresenterTemplate.htm");
				}
				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/templates"),
						editTemplatePath,
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/backbone/templates/EditTemplate.jsp-template",
								mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/templates"),
						listTemplatePath,
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/backbone/templates/ListTemplate.jsp-template",
								mapOfValues),
						monitor);
				// CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/templates"),
				// presenterTemplatePath,
				// TemplateMerger.merge("/vasbootbuilder/resources/web/js/backbone/templates/PresenterTemplate.jsp-template",
				// mapOfValues), monitor);
				/* Add a default jsp file. This is dependent on the Java Model generation */
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF"), new Path("index.jsp"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/jsps/index.jsp-template", mapOfValues),
						monitor);

			} else if (params.getUiType().equalsIgnoreCase("AngularJS")) {
				// ANGULARJS ONLY COMPONENTS
				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/libs"),
						new Path("dirPagination.js"),
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/libs/dirPagination.js"),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/libs"),
						new Path("angular.min.js"),
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/libs/angular.min.js"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/libs"),
						new Path("angular-route.min.js"), this.getClass().getResourceAsStream(
								"/vasbootbuilder/resources/web/js/libs/angular-route.min.js"),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js"),
						new Path("angular_app.js"), TemplateMerger
								.merge("/vasbootbuilder/resources/web/js/angular/angular_app-template.js", mapOfValues),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/angular_controllers"),
						new Path("HomeController.js"),
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/angular/angular_home_controller-template.js",
								mapOfValues),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/angular_controllers"),
						new Path(params.getDomainClassName() + "ListController.js"),
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/angular/angular_list_controller-template.js",
								mapOfValues),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/angular_templates"),
						new Path(params.getDomainClassName() + "List.html"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/angular/angular_list_html-template.html",
								mapOfValues),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/angular_controllers"),
						new Path(params.getDomainClassName() + "EditController.js"),
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/angular/angular_edit_controller-template.js",
								mapOfValues),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/angular_services"),
						new Path(params.getDomainClassName() + "Service.js"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/angular/angular_service-template.js",
								mapOfValues),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/angular_templates"),
						new Path(params.getDomainClassName() + "Edit.html"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/angular/angular_edit_html-template.html",
								mapOfValues),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF"), new Path("index.jsp"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/angular/angular_index-template.jsp",
								mapOfValues),
						monitor);

				CommonUtils.addFileToProject(container, new Path(".tern-project"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/angular/tern-project", mapOfValues),
						monitor);
			} else if (params.getUiType().equalsIgnoreCase("Angular4")) {
				// ANGULAR4 Components
				CommonUtils.addFileToProject(container, new Path("npm-build.sh"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/angular4/other/npm-build.sh", mapOfValues), monitor);
				CommonUtils.addFileToProject(container, new Path("npm-build.cmd"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/angular4/other/npm-build.cmd", mapOfValues), monitor);

				// non-templated files
				CommonUtils
						.addFileToProject(folders.get("src/ui/src/environments"), new Path("environment.prod.ts"),
								this.getClass().getResourceAsStream(
										"/vasbootbuilder/resources/web/js/angular4/other/environment.prod.ts"),
								monitor);
				CommonUtils.addFileToProject(
						folders.get("src/ui/src/environments"), new Path("environment.ts"), this.getClass()
								.getResourceAsStream("/vasbootbuilder/resources/web/js/angular4/other/environment.ts"),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("favicon.ico"), this.getClass()
						.getResourceAsStream("/vasbootbuilder/resources/web/js/angular4/other/favicon.ico"), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("index.html"), this.getClass()
						.getResourceAsStream("/vasbootbuilder/resources/web/js/angular4/other/index.html"), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("main.ts"),
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/angular4/other/main.ts"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("polyfills.ts"), this.getClass()
						.getResourceAsStream("/vasbootbuilder/resources/web/js/angular4/other/polyfills.ts"), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("styles.css"), this.getClass()
						.getResourceAsStream("/vasbootbuilder/resources/web/js/angular4/other/styles.css"), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("test.ts"),
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/angular4/other/test.ts"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("tsconfig.app.json"), this.getClass()
						.getResourceAsStream("/vasbootbuilder/resources/web/js/angular4/other/tsconfig.app.json"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("tsconfig.spec.json"), this.getClass()
						.getResourceAsStream("/vasbootbuilder/resources/web/js/angular4/other/tsconfig.spec.json"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("typings.d.ts"), this.getClass()
						.getResourceAsStream("/vasbootbuilder/resources/web/js/angular4/other/typings.d.ts"), monitor);

				CommonUtils.addFileToProject(folders.get("src/ui"), new Path("karma.conf.js"), this.getClass()
						.getResourceAsStream("/vasbootbuilder/resources/web/js/angular4/other2/karma.conf.js"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui"), new Path("package.json"), this.getClass()
						.getResourceAsStream("/vasbootbuilder/resources/web/js/angular4/other2/package.json"), monitor);
				CommonUtils
						.addFileToProject(folders.get("src/ui"), new Path("protractor.conf.js"),
								this.getClass().getResourceAsStream(
										"/vasbootbuilder/resources/web/js/angular4/other2/protractor.conf.js"),
								monitor);
				CommonUtils.addFileToProject(folders.get("src/ui"), new Path("README.md"), this.getClass()
						.getResourceAsStream("/vasbootbuilder/resources/web/js/angular4/other2/README.md"), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui"), new Path("tsconfig.json"), this.getClass()
						.getResourceAsStream("/vasbootbuilder/resources/web/js/angular4/other2/tsconfig.json"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui"), new Path("tslint.json"), this.getClass()
						.getResourceAsStream("/vasbootbuilder/resources/web/js/angular4/other2/tslint.json"), monitor);
				// CommonUtils.addFileToProject(folders.get("src/ui"), new
				// Path(".angular-cli.json"),
				// this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/angular4/other2/x.angular-cli.json"),
				// monitor);

				CommonUtils.addFileToProject(folders.get("src/ui"), new Path(".editorconfig"), this.getClass()
						.getResourceAsStream("/vasbootbuilder/resources/web/js/angular4/other2/x.editorconfig"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui"), new Path(".gitignore"), this.getClass()
						.getResourceAsStream("/vasbootbuilder/resources/web/js/angular4/other2/x.gitignore"), monitor);

				// create list and edit components
				// create initial 'seed' domain folder
				
				IFolder domainFolder = folders.get("src/ui/src/app").getFolder(new Path(domainName));
				domainFolder.create(false, true, new NullProgressMonitor());
				folders.put("src/ui/src/app/" + domainName, domainFolder);

				IFolder domainListFolder = folders.get("src/ui/src/app/" + domainName).getFolder(domainName + "-list");
				domainListFolder.create(false, true, new NullProgressMonitor());
				folders.put("src/ui/src/app/" + domainName + "-list", domainListFolder);

				IFolder domainEditFolder = folders.get("src/ui/src/app/" + domainName).getFolder(domainName + "-edit");
				domainEditFolder.create(false, true, new NullProgressMonitor());
				folders.put("src/ui/src/app/" + domainName + "-edit", domainEditFolder);

				// template files
				CommonUtils.addFileToProject(folders.get("src/ui"), new Path(".angular-cli.json"), TemplateMerger.merge(
						"/vasbootbuilder/resources/web/js/angular4/other2/x.angular-cli.json", mapOfValues), monitor);

				CommonUtils.addFileToProject(
						folders.get("src/ui"), new Path("proxy.conf.json"), TemplateMerger
								.merge("/vasbootbuilder/resources/web/js/angular4/other2/proxy.conf.json", mapOfValues),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/ui/src/app/home"), new Path("home.component.css"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/angular4/app/home/home.component.css",
								mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/app/home"), new Path("home.component.html"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/angular4/app/home/home.component.html",
								mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/app/home"), new Path("home.component.ts"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/angular4/app/home/home.component.ts",
								mapOfValues),
						monitor);

				// main app stuff
				CommonUtils.addFileToProject(folders.get("src/ui/src/app"), new Path("app.component.css"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/angular4/app/app.component-template.css",
								mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/app"), new Path("app.component.html"),
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/angular4/app/app.component-template.html",
								mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/app"), new Path("app.component.spec.ts"),
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/angular4/app/app.component.spec-template.ts",
								mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/app"), new Path("app.component.ts"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/angular4/app/app.component-template.ts", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/app"), new Path("app.module.ts"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/angular4/app/app.module-template.ts", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/app"), new Path("app.login.interceptor.ts"),
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/angular4/app/app.login.interceptor-template.ts",
								mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/app"), new Path("app.listwrapper.ts"),
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/angular4/app/app.listwrapper-template.ts",
								mapOfValues),
						monitor);

				// model and service classes
				CommonUtils.addFileToProject(folders.get("src/ui/src/app/" + domainName),
						new Path(domainName + ".model.ts"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/angular4/app/domain/domain.model.ts",
								mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/app/" + domainName),
						new Path(domainName + ".service.ts"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/angular4/app/domain/domain.service.ts",
								mapOfValues),
						monitor);

				// edit components
				CommonUtils.addFileToProject(folders.get("src/ui/src/app/" + domainName + "-edit"),
						new Path(domainName + "-edit.component.css"),
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/angular4/app/domain/domain-edit.component.css",
								mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/app/" + domainName + "-edit"),
						new Path(domainName + "-edit.component.html"),
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/angular4/app/domain/domain-edit.component.html",
								mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/app/" + domainName + "-edit"),
						new Path(domainName + "-edit.component.spec.ts"),
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/angular4/app/domain/domain-edit.component.spec.ts",
								mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/app/" + domainName + "-edit"),
						new Path(domainName + "-edit.component.ts"),
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/angular4/app/domain/domain-edit.component.ts",
								mapOfValues),
						monitor);

				// list components
				CommonUtils.addFileToProject(folders.get("src/ui/src/app/" + domainName + "-list"),
						new Path(domainName + "-list.component.css"),
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/angular4/app/domain/domain-list.component.css",
								mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/app/" + domainName + "-list"),
						new Path(domainName + "-list.component.html"),
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/angular4/app/domain/domain-list.component.html",
								mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/app/" + domainName + "-list"),
						new Path(domainName + "-list.component.spec.ts"),
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/angular4/app/domain/domain-list.component.spec.ts",
								mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/app/" + domainName + "-list"),
						new Path(domainName + "-list.component.ts"),
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/angular4/app/domain/domain-list.component.ts",
								mapOfValues),
						monitor);

			} else if (params.getUiType().equalsIgnoreCase("React")) {
			    String domainClassName = params.getDomainClassName();
			    
				CommonUtils.addFileToProject(container, new Path("npm-build.sh"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/react/other/npm-build.sh", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(container, new Path("npm-build.cmd"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/react/other/npm-build.cmd", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui"), new Path("package.json"), TemplateMerger.merge(
						"/vasbootbuilder/resources/web/js/react/config/package-template.json", mapOfValues), monitor);
				
				CommonUtils.addFileToProject(folders.get("src/ui"), new Path(".env.local"), TemplateMerger.merge(
                        "/vasbootbuilder/resources/web/js/react/config/env.local", mapOfValues), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui"), new Path(".env.production"), TemplateMerger.merge(
                        "/vasbootbuilder/resources/web/js/react/config/env.production", mapOfValues), monitor);
				
				//mocks
				CommonUtils.addFileToProject(folders.get("src/ui/mocks"), new Path("server.js"), TemplateMerger.merge(
                        "/vasbootbuilder/resources/web/js/react/other/server-template.js", mapOfValues), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/mocks"), new Path(domainClassName +"s.json"), TemplateMerger.merge(
                        "/vasbootbuilder/resources/web/js/react/other/mockdata-template.json", mapOfValues), monitor);
				
				
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("index.js"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/app/index-template.js", mapOfValues), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("index.css"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/app/index-template.css", mapOfValues), monitor);
				
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("App.js"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/react/app/App-template.js", mapOfValues),
						monitor);
				
				//App.js related files
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("AppBreadcrumb.js"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/react/app/AppBreadcrumb-template.js", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("AppCodeHighlight.js"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/react/app/AppCodeHighlight-template.js", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("AppDemo.scss"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/react/app/AppDemo.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("App.scss"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/react/app/App.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("AppFooter.js"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/react/app/AppFooter-template.js", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("AppInlineProfile.js"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/react/app/AppInlineProfile-template.js", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("AppMenu.js"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/react/app/AppMenu-template.js", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("AppTopbar.js"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/react/app/AppTopbar-template.js", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("AppWrapper.js"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/react/app/AppWrapper-template.js", mapOfValues),
						monitor);
				
				
				CommonUtils.addFileToProject(folders.get("src/ui/src/containers"), new Path("AppContainer.js"),
                        TemplateMerger.merge("/vasbootbuilder/resources/web/js/react/app/AppContainer-template.js", mapOfValues),
                        monitor);
				
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("registerServiceWorker.js"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/react/app/registerServiceWorker.js" ,mapOfValues),
						monitor);
				
				// Home
				IFolder homeFolder = folders.get("src/ui/src/components");
				CommonUtils.addFileToProject(homeFolder, new Path("Home.js"), TemplateMerger.merge(
						"/vasbootbuilder/resources/web/js/react/component/Home-template.js", mapOfValues), monitor);
				

				// actions
				IFolder actionsContainerFolder = folders.get("src/ui/src/actions");
				CommonUtils.addFileToProject(actionsContainerFolder, new Path(domainName + ".js"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/react/actions/index-template.js", mapOfValues),
						monitor);
				
				CommonUtils.addFileToProject(actionsContainerFolder, new Path(domainName + ".test.js"),
                        TemplateMerger.merge("/vasbootbuilder/resources/web/js/react/actions/actionTest-template.js", mapOfValues),
                        monitor);
				
				//assets
				CommonUtils.addFileToProject(folders.get("src/ui/src/assets/flags"), new Path("flags_responsive.png"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/assets/flags/flags_responsive.png", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/assets/flags"), new Path("flags.css"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/assets/flags/flags.css", mapOfValues),
						monitor);
				
				// reducers
				IFolder reducersContainerFolder = folders.get("src/ui/src/reducers");
				CommonUtils.addFileToProject(reducersContainerFolder, new Path(domainName + ".js"),
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/react/reducers/domain-reducer-template.js",
								mapOfValues),
						monitor);
				CommonUtils.addFileToProject(reducersContainerFolder, new Path(domainName + ".test.js"),
                        TemplateMerger.merge(
                                "/vasbootbuilder/resources/web/js/react/reducers/domain-reducer-test-template.js",
                                mapOfValues),
                        monitor);
				
				CommonUtils.addFileToProject(reducersContainerFolder, new Path("index.js"),
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/react/reducers/index-template.js",
								mapOfValues),
						monitor);
				
				// Domain Folders				
				// Domain List
				CommonUtils.addFileToProject(folders.get("src/ui/src/components"),
						new Path(domainClassName + "List.js"),
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/react/component/domain/DomainList-template.js",
								mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/containers"),
						new Path(domainClassName + "ListContainer.js"),
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/react/component/domain/DomainListContainer-template.js",
								mapOfValues),
						monitor);				
				mapOfValues.put("componentName", domainClassName + "List");
				
				// Domain Details (Editing Form}
				CommonUtils.addFileToProject(folders.get("src/ui/src/components"),
						new Path(domainClassName + "Edit.js"),
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/react/component/domain/DomainDetail-template.js",
								mapOfValues),
						monitor);
				
				CommonUtils.addFileToProject(folders.get("src/ui/src/utils"),
                        new Path("authority.js"),
                        TemplateMerger.merge(
                                "/vasbootbuilder/resources/web/js/react/component/domain/authority-template.js",
                                mapOfValues),
                        monitor);
				
				//Test
                CommonUtils.addFileToProject(folders.get("src/ui/src/utils"),
                        new Path("TestUtils.js"),
                        TemplateMerger.merge(
                                "/vasbootbuilder/resources/web/js/react/component/domain/TestUtils.js",
                                mapOfValues),
                        monitor);
				
				CommonUtils.addFileToProject(folders.get("src/ui/src/components"),
						new Path(domainClassName + "Edit.test.js"),
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/react/component/domain/DomainDetailTest-template.js",
								mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/components"),
                        new Path(domainClassName + "List.test.js"),
                        TemplateMerger.merge(
                                "/vasbootbuilder/resources/web/js/react/component/domain/DomainListTest-template.js",
                                mapOfValues),
                        monitor);
				//
				CommonUtils.addFileToProject(folders.get("src/ui/src/containers"),
                        new Path(domainClassName + "ListContainer.test.js"),
                        TemplateMerger.merge(
                                "/vasbootbuilder/resources/web/js/react/component/domain/DomainListContainerTest-template.js",
                                mapOfValues),
                        monitor);
				
				CommonUtils.addFileToProject(folders.get("src/ui/src/containers"),
						new Path(domainClassName + "EditContainer.js"),
						TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/react/component/domain/DomainDetailContainer-template.js",
								mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/containers"),
                        new Path(domainClassName + "EditContainer.test.js"),
                        TemplateMerger.merge(
                                "/vasbootbuilder/resources/web/js/react/component/domain/DomainDetailContainerTest-template.js",
                                mapOfValues),
                        monitor);
				mapOfValues.put("componentName", domainClassName + "Detail");

				// add the root public resources
				CommonUtils.addFileToProject(folders.get("src/ui/public"), new Path("index.html"), TemplateMerger.merge(
						"/vasbootbuilder/resources/web/js/react/publik/index-template.html", mapOfValues), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public"), new Path("manifest.json"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/manifest-template.json", mapOfValues),
						monitor);
				
				//public/assets/layout/css
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/layout/css"), new Path("layout-blue.css"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/layout/css/layout-blue.css", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/layout/css"), new Path("layout-blue.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/layout/css/layout-blue.scss", mapOfValues),
						monitor);

				//public/assets/layout/fonts
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/layout/fonts"), new Path("cabin-v12-latin-500.eot"), 
							this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/react/publik/assets/layout/fonts/cabin-v12-latin-500.eot"),
						monitor);				
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/layout/fonts"), new Path("cabin-v12-latin-500.svg"), 
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/react/publik/assets/layout/fonts/cabin-v12-latin-500.svg"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/layout/fonts"), new Path("cabin-v12-latin-500.ttf"), 
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/react/publik/assets/layout/fonts/cabin-v12-latin-500.ttf"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/layout/fonts"), new Path("cabin-v12-latin-500.woff"), 
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/react/publik/assets/layout/fonts/cabin-v12-latin-500.woff"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/layout/fonts"), new Path("cabin-v12-latin-500.woff2"), 
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/react/publik/assets/layout/fonts/cabin-v12-latin-500.woff2"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/layout/fonts"), new Path("cabin-v12-latin-700.eot"), 
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/react/publik/assets/layout/fonts/cabin-v12-latin-700.eot"),
						monitor);
				
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/layout/fonts"), new Path("cabin-v12-latin-700.svg"), 
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/react/publik/assets/layout/fonts/cabin-v12-latin-700.svg"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/layout/fonts"), new Path("cabin-v12-latin-700.ttf"), 
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/react/publik/assets/layout/fonts/cabin-v12-latin-700.ttf"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/layout/fonts"), new Path("cabin-v12-latin-700.woff"), 
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/react/publik/assets/layout/fonts/cabin-v12-latin-700.woff"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/layout/fonts"), new Path("cabin-v12-latin-700.woff2"), 
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/react/publik/assets/layout/fonts/cabin-v12-latin-700.woff2"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/layout/fonts"), new Path("cabin-v12-latin-regular.eot"), 
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/react/publik/assets/layout/fonts/cabin-v12-latin-regular.eot"),
						monitor);
				
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/layout/fonts"), new Path("cabin-v12-latin-regular.svg"), 
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/react/publik/assets/layout/fonts/cabin-v12-latin-regular.svg"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/layout/fonts"), new Path("cabin-v12-latin-regular.ttf"), 
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/react/publik/assets/layout/fonts/cabin-v12-latin-regular.ttf"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/layout/fonts"), new Path("cabin-v12-latin-regular.woff"), 
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/react/publik/assets/layout/fonts/cabin-v12-latin-regular.woff"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/layout/fonts"), new Path("cabin-v12-latin-regular.woff2"), 
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/react/publik/assets/layout/fonts/cabin-v12-latin-regular.woff2"),
						monitor);
				
				//public/assets/layout/images
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/layout/images"), new Path("avatar-john.png"), 
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/react/publik/assets/layout/images/avatar-john.png"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/layout/images"), new Path("avatar-julia.png"), 
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/react/publik/assets/layout/images/avatar-julia.png"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/layout/images"), new Path("avatar-kevin.png"), 
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/react/publik/assets/layout/images/avatar-kevin.png"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/layout/images"), new Path("avatar.png"), 
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/react/publik/assets/layout/images/avatar.png"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/layout/images"), new Path("logo-black.png"), 
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/react/publik/assets/layout/images/logo-black.png"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/layout/images"), new Path("logo-white.png"), 
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/react/publik/assets/layout/images/logo-white.png"),
						monitor);
				
				//public/assets/sass
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass"), new Path("_fonts.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/_fonts.scss", mapOfValues),
						monitor);
				//public assets/sass/layout
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/layout"), new Path("_config.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/layout/_config.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/layout"), new Path("_dashboard.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/layout/_dashboard.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/layout"), new Path("_exception.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/layout/_exception.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/layout"), new Path("_footer.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/layout/_footer.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/layout"), new Path("_help.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/layout/_help.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/layout"), new Path("_invoice.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/layout/_invoice.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/layout"), new Path("_landing.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/layout/_landing.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/layout"), new Path("_layout.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/layout/_layout.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/layout"), new Path("_loader.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/layout/_loader.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/layout"), new Path("_login.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/layout/_login.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/layout"), new Path("_main.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/layout/_main.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/layout"), new Path("_menu.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/layout/_menu.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/layout"), new Path("_mixins.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/layout/_mixins.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/layout"), new Path("_topbar.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/layout/_topbar.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/layout"), new Path("_typography.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/layout/_typography.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/layout"), new Path("_utils.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/layout/_utils.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/layout"), new Path("_variables.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/layout/_variables.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/layout"), new Path("_widgets.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/layout/_widgets.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/layout"), new Path("_wizard.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/layout/_wizard.scss", mapOfValues),
						monitor);
				
				//public assets/sass/overrides
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/overrides"), new Path("_layout_styles.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/overrides/_layout_styles.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/overrides"), new Path("_layout_variables.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/overrides/_layout_variables.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/overrides"), new Path("_theme_styles.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/overrides/_theme_styles.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/overrides"), new Path("_theme_variables.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/overrides/_theme_variables.scss", mapOfValues),
						monitor);
				
				//public assets/sass/theme
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/theme"), new Path("_theme.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/theme/_theme.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/theme"), new Path("_variables.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/theme/_variables.scss", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/sass/theme"), new Path("_vendor_extensions.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/sass/theme/_vendor_extensions.scss", mapOfValues),
						monitor);
				
				//public assets/theme
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/theme"), new Path("theme-blue.css"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/theme/theme-blue.css", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/theme"), new Path("theme-blue.css.map"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/theme/theme-blue.css.map", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/public/assets/theme"), new Path("theme-blue.scss"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/react/publik/assets/theme/theme-blue.scss", mapOfValues),
						monitor);
				
				
			} else if (params.getUiType().equalsIgnoreCase("VueJS")) { // VueJS
				CommonUtils.addFileToProject(folders.get("src/ui/config"), new Path("index.js"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/vue/index-template.js", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/components"), new Path("Home.vue"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/vue/Home-template.vue", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/components"),
						new Path(params.getDomainClassName() + ".vue"), TemplateMerger
								.merge("/vasbootbuilder/resources/web/js/vue/DomainEditor-template.vue", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src/components"),
						new Path(params.getDomainClassName() + "s.vue"), TemplateMerger.merge(
								"/vasbootbuilder/resources/web/js/vue/DomainList-template.vue", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("App.vue"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/vue/App-template.vue", mapOfValues),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/src"), new Path("main.js"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/vue/main-template.js", mapOfValues),
						monitor);

				CommonUtils.addFileToProject(container, new Path("npm-build.sh"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/vue/npm-build.sh", mapOfValues),
						monitor);

				CommonUtils.addFileToProject(container, new Path("npm-build.cmd"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/vue/npm-build.cmd", mapOfValues),
						monitor);

				// CommonUtils.addFileToProject(folders.get("src/ui"), new Path("runapp.cmd"),
				// TemplateMerger.merge("/vasbootbuilder/resources/web/js/vue/runapp-template.cmd",
				// mapOfValues), monitor);
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF"), new Path("index.jsp"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/vue/vue_index-template.jsp",
								mapOfValues),
						monitor);

				// non-template files for build folder
				CommonUtils.addFileToProject(folders.get("src/ui/build"), new Path("build.js"), this.getClass()
						.getResourceAsStream("/vasbootbuilder/resources/web/js/vue/others/build/build.js"), monitor);
				CommonUtils
						.addFileToProject(folders.get("src/ui/build"), new Path("check-versions.js"),
								this.getClass().getResourceAsStream(
										"/vasbootbuilder/resources/web/js/vue/others/build/check-versions.js"),
								monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/build"), new Path("dev-client.js"), this.getClass()
						.getResourceAsStream("/vasbootbuilder/resources/web/js/vue/others/build/dev-client.js"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/build"), new Path("dev-server.js"), this.getClass()
						.getResourceAsStream("/vasbootbuilder/resources/web/js/vue/others/build/dev-server.js"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/build"), new Path("utils.js"), this.getClass()
						.getResourceAsStream("/vasbootbuilder/resources/web/js/vue/others/build/utils.js"), monitor);
				CommonUtils
						.addFileToProject(folders.get("src/ui/build"), new Path("webpack.base.conf.js"),
								this.getClass().getResourceAsStream(
										"/vasbootbuilder/resources/web/js/vue/others/build/webpack.base.conf.js"),
								monitor);
				CommonUtils
						.addFileToProject(folders.get("src/ui/build"), new Path("webpack.dev.conf.js"),
								this.getClass().getResourceAsStream(
										"/vasbootbuilder/resources/web/js/vue/others/build/webpack.dev.conf.js"),
								monitor);
				CommonUtils
						.addFileToProject(folders.get("src/ui/build"), new Path("webpack.prod.conf.js"),
								this.getClass().getResourceAsStream(
										"/vasbootbuilder/resources/web/js/vue/others/build/webpack.prod.conf.js"),
								monitor);

				// non-template files for config folder
				CommonUtils.addFileToProject(folders.get("src/ui/config"), new Path("dev.env.js"), this.getClass()
						.getResourceAsStream("/vasbootbuilder/resources/web/js/vue/others/config/dev.env.js"), monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/config"), new Path("test.env.js"), this.getClass()
						.getResourceAsStream("/vasbootbuilder/resources/web/js/vue/others/config/test.env.js"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui/config"), new Path("prod.env.js"), this.getClass()
						.getResourceAsStream("/vasbootbuilder/resources/web/js/vue/others/config/prod.env.js"),
						monitor);

				// static
				CommonUtils.addFileToProject(folders.get("src/ui/static"), new Path("jquery.js"), this.getClass()
						.getResourceAsStream("/vasbootbuilder/resources/web/js/libs/jquery-1.10.2.min.js"), monitor);

				// non-template files for root folder
				CommonUtils.addFileToProject(folders.get("src/ui"), new Path(".babelrc"),
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/vue/root/x.babelrc"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui"), new Path(".editorconfig"),
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/vue/root/x.editorconfig"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui"), new Path(".eslintignore"),
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/vue/root/x.eslintignore"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui"), new Path(".eslintrc.js"),
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/vue/root/x.eslintrc.js"),
						monitor);

				// CommonUtils.addFileToProject(folders.get("src/ui"), new Path("index.html"),
				// this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/vue/root/index.html"),
				// monitor);
				CommonUtils.addFileToProject(folders.get("src/ui"), new Path("index.html"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/vue/root/index.html", mapOfValues),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/ui"), new Path("package.json"),
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/vue/root/package.json"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/ui"), new Path("README.md"),
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/vue/root/README.md"),
						monitor);

			} else {
				// None. Don't create UI Portion
			}

			// Common UI-related JS
			if (!params.getUiType().equalsIgnoreCase("None")) {
				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/libs"),
						new Path("localizedmessages.js"), this.getClass().getResourceAsStream(
								"/vasbootbuilder/resources/web/js/libs/localizedmessages.js"),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/libs"),
						new Path("ejs_fulljslint.js"),
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/libs/ejs_fulljslint.js"),
						monitor);
				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/libs"),
						new Path("jquery.js"), this.getClass().getResourceAsStream(
								"/vasbootbuilder/resources/web/js/libs/jquery-1.10.2.min.js"),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/libs"),
						new Path("jquery-1.10.2.min.map"), this.getClass().getResourceAsStream(
								"/vasbootbuilder/resources/web/js/libs/jquery-1.10.2.min.map"),
						monitor);

				// CommonUtils.CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/libs"),
				// new Path("jquery.dataTables.js"),
				// this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/libs/jquery.dataTables.js"),
				// monitor);
				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/libs"),
						new Path("json2.js"),
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/libs/json2.js"), monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/libs"),
						new Path("jquery.bootstrap.wizard.js"), this.getClass().getResourceAsStream(
								"/vasbootbuilder/resources/web/js/libs/jquery.bootstrap.wizard.js"),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/libs"),
						new Path("bootstrap.min.js"),
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/libs/bootstrap.min.js"),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/css/fonts"),
						new Path("glyphicons-halflings-regular.eot"), this.getClass().getResourceAsStream(
								"/vasbootbuilder/resources/fonts/glyphicons-halflings-regular.eot"),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/css/fonts"),
						new Path("glyphicons-halflings-regular.svg"), this.getClass().getResourceAsStream(
								"/vasbootbuilder/resources/fonts/glyphicons-halflings-regular.svg"),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/css/fonts"),
						new Path("glyphicons-halflings-regular.ttf"), this.getClass().getResourceAsStream(
								"/vasbootbuilder/resources/fonts/glyphicons-halflings-regular.ttf"),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/css/fonts"),
						new Path("glyphicons-halflings-regular.woff"), this.getClass().getResourceAsStream(
								"/vasbootbuilder/resources/fonts/glyphicons-halflings-regular.woff"),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/css/fonts"),
						new Path("glyphicons-halflings-regular.woff2"), this.getClass().getResourceAsStream(
								"/vasbootbuilder/resources/fonts/glyphicons-halflings-regular.woff2"),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/libs"),
						new Path("respond.min.js"),
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/libs/respond.min.js"),
						monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/libs"),
						new Path("html5shiv.min.js"),
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/libs/html5shiv.min.js"),
						monitor);

				// ANOMALY, why does text.js have to be outside the libs folder
				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js"), new Path("text.js"),
						this.getClass().getResourceAsStream("/vasbootbuilder/resources/web/js/libs/text.js"), monitor);

				CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/js/globals"),
						new Path("global.js"),
						TemplateMerger.merge("/vasbootbuilder/resources/web/js/libs/global.js", mapOfValues), monitor);

				CommonUtils.addFileToProject(
						folders.get("src/main/resources/public/resources/js"), new Path("main.js"), TemplateMerger
								.merge("/vasbootbuilder/resources/web/js/backbone/main/main-template.js", mapOfValues),
						monitor);
				
			}

			addVariousSettings(folders.get(".settings"), proj, params.getBasePackageName(),
					params.getControllerPackageName(), monitor);
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF"), new Path("health.jsp"),
					TemplateMerger.merge("/vasbootbuilder/resources/java/health.jsp", mapOfValues),
					monitor);

			/* Add web-xml file */
			/*
			 * No longer needed for Srping Boot??
			 * CommonUtils.addFileToProject(folders.get("src/main/resources/public"), new
			 * Path("web.xml"),
			 * TemplateMerger.merge("/vasbootbuilder/resources/maven/web.xml-template",
			 * mapOfValues), monitor);
			 */

			/* Add Spring servlet dispathcer mapping file */
			/*
			 * No longer needed for Srping Boot??
			 * CommonUtils.addFileToProject(folders.get("src/main/resources/public"), new
			 * Path("yourdispatcher-servlet.xml"), TemplateMerger.merge(
			 * "/vasbootbuilder/resources/maven/yourdispatcher-servlet.xml-template",
			 * mapOfValues), monitor);
			 */

			/* Add Spring context files */
			/*
			 * No longer needed for Srping Boot??
			 * CommonUtils.addFileToProject(folders.get("src/main/resources/public/spring"),
			 * new Path("applicationContext.xml"), TemplateMerger.merge(
			 * "/vasbootbuilder/resources/maven/applicationContext.xml-template",
			 * mapOfValues), monitor);
			 */
			CommonUtils.addFileToProject(folders.get("src/main/resources"), new Path("application.properties"),
					TemplateMerger.merge("/vasbootbuilder/resources/maven/application-template.properties",
							mapOfValues),
					monitor);

			if ((Boolean) mapOfValues.get("prepForOracle") || (Boolean) mapOfValues.get("prepForMySQL")) {
				CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/spring"), new Path("datasource.xml"),
						TemplateMerger.merge("/vasbootbuilder/resources/maven/datasource.xml-template", mapOfValues),
						monitor);
			}

			/*
			 * CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/spring"),
			 * new Path("spring-security.xml"), TemplateMerger.merge(
			 * "/vasbootbuilder/resources/maven/spring-security.xml-template", mapOfValues),
			 * monitor);
			 * 
			 * CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/spring"),
			 * new Path("ehcache.xml"),
			 * TemplateMerger.merge("/vasbootbuilder/resources/maven/ehcache.xml-template",
			 * proj.getName(),params.getBasePackageName(),params.getControllerPackageName(),
			 * params.getUtilPackageName()), monitor);
			 */

			/* Add a java model */
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getDomainPackageName(),
					params.getDomainClassName(), params.getDomainClassSourceCode(), monitor);

			Map<String, Object> modelAttributes = pageThree.getModelAttributes();

			/* Add a default CSS */
			CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/css"), new Path("site.css"),
					TemplateMerger.merge("/vasbootbuilder/resources/css/site.css-template", mapOfValues), monitor);

			CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/css/libs"),
					new Path("bootstrap.min.css"),
					this.getClass().getResourceAsStream("/vasbootbuilder/resources/css/bootstrap.min.css"), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/resources/public/resources/css/libs"),
					new Path("bootstrap-theme.min.css"),
					this.getClass().getResourceAsStream("/vasbootbuilder/resources/css/bootstrap-theme.min.css"),
					monitor);

			/*
			 * Add SpringBootStarter
			 */
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getBasePackageName(),
					params.getSpringBootStarterClassName(), params.getSpringBootStarterSourceCode(), monitor);

			/* Add Controllers */
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getControllerPackageName(),
					"MainController", params.getMainControllerSourceCode(), monitor);
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getControllerPackageName(),
                    "BaseController", params.getBaseControllerSourceCode(), monitor);
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getControllerPackageName(),
					params.getControllerClassName(), params.getDomainControllerSourceCode(), monitor);
			
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getControllerPackageName(),
					"MiscController", params.getMiscControllerSourceCode(), monitor);
		

			/* Add Service */
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getServicePackageName(),
					params.getDomainClassName() + "Service", params.getServiceSourceCode(), monitor);

			if (params.isGenerateWebService()) {
				CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getWebServicePackageName(),
						params.getDomainClassName() + "WebService", params.getWebServiceInterfaceSourceCode(), monitor);
				CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getWebServicePackageName(),
						params.getDomainClassName() + "WebServiceImpl", params.getWebServiceImplSourceCode(), monitor);
			}

			/* Add DAO */
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getDaoPackageName(),
					params.getDomainClassName() + "Repository", params.getDaoSourceCode(), monitor);

			/*if ((Boolean) mapOfValues.get("prepForOracle") || (Boolean) mapOfValues.get("prepForMySQL") || 
					(Boolean) mapOfValues.get("prepForHSQL")  ) {
				CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getDaoPackageName() + ".mapper",
						params.getDomainClassName() + "Mapper", params.getMapperSourceCode(), monitor);

				CommonUtils
						.createPackageAndClass(folders.get("src/main/resources"),
								params.getDaoPackageName() + ".mapper", params.getDomainClassName() + "Mapper.xml",
								IOUtils.toString(TemplateMerger
										.merge("/vasbootbuilder/resources/java/mapper-template.xml", mapOfValues)),
								monitor);
			}*/

			/* Add Security */
			/*
			 * CommonUtils.createPackageAndClass(folders.get("src/main/java"),
			 * params.getSecurityPackageName(), "SampleUserDetailsService",
			 * params.getSecurityUserDetailsServiceSourceCode() , monitor);
			*/
			CommonUtils.createPackageAndClass(folders.get("src/main/java"),
			 params.getSecurityPackageName(), "SampleUserDetails",
			 params.getSecurityUserDetailsSourceCode() , monitor);
			
			CommonUtils.createPackageAndClass(folders.get("src/main/java"),
		             params.getSecurityPackageName(), "CustomLogoutSuccessHandler",
		             params.getCustomLogoutSuccessHandlerSourceCode() , monitor);
			
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getSecurityPackageName(),
					"ClearSessionOnSMHeaderChange", params.getSmHeaderChangeSourceCode(), monitor);
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getSecurityPackageName(),
					"SpringSecurityConfig", params.getSpringSecurityConfigSourceCode(), monitor);
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getSecurityPackageName(),
					"SpringAuthenticationProvider", params.getSpringSecurityAuhenticationProvider(), monitor);

			/* Add ListWrapper */
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getCommonPackageName(),
					"ListWrapper", params.getListWrapperSourceCode(), monitor);
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getCommonPackageName(),
                    "MainConfig", params.getMainConfigSourceCode(), monitor);
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getCommonPackageName(),
                    "SortedIndicator", IOUtils.toString(TemplateMerger
                            .merge("/vasbootbuilder/resources/java/sortedIndicator.java-template", mapOfValues)), monitor);
			
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getCommonPackageName(),
					"NameValuePair", params.getNameValuePairSourceCode(), monitor);

			/* Add Resource Bundle wrapper */
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getUtilPackageName(),
					"ExposedResourceBundleMessageSource", params.getResourceBundleUtilSourceCode(), monitor);

			/* Add message bundles */
			CommonUtils.createPackageAndClass(folders.get("src/main/resources"), "locales", "messages_en.properties",
					params.getSampleMessageBundleContent(), monitor);
			CommonUtils.createPackageAndClass(folders.get("src/main/resources"), "locales", "messages_es.properties",
					params.getSampleMessageBundleContentEs(), monitor);

			CommonUtils.createPackageAndClass(folders.get("src/main/resources"), "", "ESAPI.properties",
					params.getSampleESAPIProperties(), monitor);

			CommonUtils.addFileToProject(folders.get("src/main/resources"), new Path("log4j.properties"),
					TemplateMerger.merge("/vasbootbuilder/resources/other/log4j.properties-template", proj.getName(),
							params.getBasePackageName(), params.getControllerPackageName(),
							params.getUtilPackageName()),
					monitor);

			CommonUtils.addFileToProject(folders.get("src/main/resources"), new Path("env_local.properties"),
					TemplateMerger.merge("/vasbootbuilder/resources/other/env_local.properties-template",
							proj.getName(), params.getBasePackageName(), params.getControllerPackageName(),
							params.getUtilPackageName()),
					monitor);

			/*
			 * CommonUtils.addFileToProject(container, new Path("readme.txt"),
			 * TemplateMerger.merge("/vasbootbuilder/resources/other/readme.txt-template",
			 * proj.getName(),params.getBasePackageName(),params.getControllerPackageName(),
			 * params.getUtilPackageName()), monitor);
			 */

			CommonUtils.addFileToProject(container, new Path("readme.txt"),
					TemplateMerger.merge("/vasbootbuilder/resources/other/readme.txt-template", mapOfValues), monitor);

			CommonUtils.addFileToProject(container, new Path("readme.md"),
                    TemplateMerger.merge("/vasbootbuilder/resources/other/readme-template.md", mapOfValues), monitor);

			
			/* Add Test Data in an external text file */
			StringWriter sampleDataStringWriter = new StringWriter();
			IOUtils.copy(TemplateMerger.merge("/vasbootbuilder/resources/other/sampledata.txt-template", mapOfValues),
					sampleDataStringWriter);
			CommonUtils.createPackageAndClass(folders.get("src/main/resources"), "sampledata",
					mapOfValues.get("domainClassName").toString() + "s.txt",
					CommonUtils.cleanSampleData(sampleDataStringWriter.toString()), monitor);

			/* Add Test Mongo Data in an external text file */
			if ((Boolean) mapOfValues.get("useMongo")) {
				StringWriter sampleMongoDataStringWriter = new StringWriter();
				IOUtils.copy(
						TemplateMerger.merge("/vasbootbuilder/resources/other/mongo-script.txt-template", mapOfValues),
						sampleMongoDataStringWriter);
				CommonUtils.createPackageAndClass(folders.get("src/main/resources/scripts"), "sampledata",
						mapOfValues.get("domainClassName").toString() + "s.txt",
						CommonUtils.cleanSampleData(sampleMongoDataStringWriter.toString()), monitor);
			}
			
			/* Add test data for HSQL*/
			if ((Boolean) mapOfValues.get("prepForHSQL")) {
				StringWriter sampleHSQLDataStringWriter = new StringWriter();
				IOUtils.copy(
						TemplateMerger.merge("/vasbootbuilder/resources/other/data-template.sql", mapOfValues),
						sampleHSQLDataStringWriter);
				CommonUtils.createPackageAndClass(folders.get("src/main/resources"), "",
						"data.sql",
						CommonUtils.cleanSampleData(sampleHSQLDataStringWriter.toString()), monitor);
				
				StringWriter sampleHSQLSchemaStringWriter = new StringWriter();
				IOUtils.copy(
						TemplateMerger.merge("/vasbootbuilder/resources/other/schema-template.sql", mapOfValues),
						sampleHSQLSchemaStringWriter);
				CommonUtils.createPackageAndClass(folders.get("src/main/resources"), "",
						"schema.sql",
						CommonUtils.cleanSampleData(sampleHSQLSchemaStringWriter.toString()), monitor);
			}

			// add junit for Controllers
			CommonUtils.createPackageAndClass(folders.get("src/test/java"), params.getControllerPackageName(),
					params.getControllerClassName() + "Test", params.getControllerTestSourceCode(), monitor);

			IJavaProject javaProject = JavaCore.create(proj);

			// Create classpath entries which really creates the ".classpath" file of the
			// Eclipse project
			createClassPathEntries(folders.get("target/classes"), folders.get("target/test-classes"),
					folders.get("src/main/java"), folders.get("src/test/java"), folders.get("src/main/resources"),
					folders.get("src/test/resources"), javaProject);

			// add vasbootbuilder-specific settings  
			addVASBootBuilderSettings(folders.get(".settings"), project, params.getBasePackageName(),
					params.isGenerateSecurityCode(), (Boolean) mapOfValues.get("useMongo"),
					(Boolean) mapOfValues.get("prepForOracle"), 
					(Boolean) mapOfValues.get("prepForHSQL"),
					params.getUiType(), monitor);

		} catch (Throwable ioe) {
			IStatus status = new Status(IStatus.ERROR, "NewFileWizard", IStatus.OK, ioe.getLocalizedMessage(), null);
			throw new CoreException(status);
		} finally {
			monitor.done();
		}
	}

	private void createFolderStructures(IContainer container, IProgressMonitor monitor, String uiType)
			throws Exception {
		// target
		IFolder outputFolder = container.getFolder(new Path("target"));
		outputFolder.create(true, true, monitor);
		folders.put("target", outputFolder);

		// target/classes
		IFolder outputFolder2 = outputFolder.getFolder(new Path("classes"));
		outputFolder2.create(true, true, monitor);
		folders.put("target/classes", outputFolder2);

		// target/test-classes
		IFolder outputFolder3 = outputFolder.getFolder(new Path("test-classes"));
		outputFolder3.create(true, true, monitor);
		folders.put("target/test-classes", outputFolder3);

		// src
		IFolder srcFolder = container.getFolder(new Path("src"));

		// src/main/java
		srcFolder.create(false, true, new NullProgressMonitor());
		IFolder srcFolder21 = srcFolder.getFolder(new Path("main"));
		srcFolder21.create(false, true, new NullProgressMonitor());
		IFolder srcFolder31 = srcFolder21.getFolder(new Path("java"));
		srcFolder31.create(false, true, new NullProgressMonitor());
		folders.put("src/main/java", srcFolder31);

		if (uiType.equalsIgnoreCase("VueJS")) {
			/************************* VueJS folders *******************************/
			// src/ui
			IFolder srcFolder23 = srcFolder.getFolder(new Path("ui"));
			srcFolder23.create(false, true, new NullProgressMonitor());
			folders.put("src/ui", srcFolder23);
			
			// src/ui/build
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
		}else if (uiType.equalsIgnoreCase ("React") ) {
			//src/ui
			IFolder srcFolder23 = srcFolder.getFolder (new Path("ui"));
			srcFolder23.create(false, true, new NullProgressMonitor());
			folders.put("src/ui", srcFolder23);
			
	         // src/ui/mocks
            IFolder srcFolder230 = srcFolder23.getFolder(new Path("mocks"));
            srcFolder230.create(false, true, new NullProgressMonitor());
            folders.put("src/ui/mocks", srcFolder230);

			IFolder srcFolder231 = srcFolder23.getFolder(new Path("public"));
			srcFolder231.create(false, true, new NullProgressMonitor()); 
			folders.put("src/ui/public", srcFolder231); 
			
			//All public-related folders=============
			IFolder srcFolder2311 = srcFolder231.getFolder(new Path("assets"));
			srcFolder2311.create(false, true, new NullProgressMonitor()); 
			folders.put("src/ui/public/assets", srcFolder2311); 
			
			IFolder srcFolder23111 = srcFolder2311.getFolder(new Path("layout"));
			srcFolder23111.create(false, true, new NullProgressMonitor()); 
			folders.put("src/ui/public/assets/layout", srcFolder23111); 
			
			//layout subfolders
			IFolder srcFolder231111 = srcFolder23111.getFolder(new Path("css"));
			srcFolder231111.create(false, true, new NullProgressMonitor()); 
			folders.put("src/ui/public/assets/layout/css", srcFolder231111); 
			
			IFolder srcFolder231112 = srcFolder23111.getFolder(new Path("fonts"));
			srcFolder231112.create(false, true, new NullProgressMonitor()); 
			folders.put("src/ui/public/assets/layout/fonts", srcFolder231112); 
			
			IFolder srcFolder231113 = srcFolder23111.getFolder(new Path("images"));
			srcFolder231113.create(false, true, new NullProgressMonitor()); 
			folders.put("src/ui/public/assets/layout/images", srcFolder231113); 			
			//end of layout subfolders
			
			IFolder srcFolder23112 = srcFolder2311.getFolder(new Path("sass"));
			srcFolder23112.create(false, true, new NullProgressMonitor()); 
			folders.put("src/ui/public/assets/sass", srcFolder23112); 
			//sass subfolders
			IFolder srcFolder231121 = srcFolder23111.getFolder(new Path("layout"));
			srcFolder231121.create(false, true, new NullProgressMonitor()); 
			folders.put("src/ui/public/assets/sass/layout", srcFolder231121); 
			
			IFolder srcFolder231122 = srcFolder23111.getFolder(new Path("overrides"));
			srcFolder231122.create(false, true, new NullProgressMonitor()); 
			folders.put("src/ui/public/assets/sass/overrides", srcFolder231122); 
			
			IFolder srcFolder231123 = srcFolder23111.getFolder(new Path("theme"));
			srcFolder231123.create(false, true, new NullProgressMonitor()); 
			folders.put("src/ui/public/assets/sass/theme", srcFolder231123); 
			
			//end of sass subfolders
			
			IFolder srcFolder23113 = srcFolder2311.getFolder(new Path("theme"));
			srcFolder23113.create(false, true, new NullProgressMonitor()); 
			folders.put("src/ui/public/assets/theme", srcFolder23113); 
			
			
			//===========END of All public-related folders==========

			IFolder srcFolder234 = srcFolder23.getFolder(new Path("src"));
			srcFolder234.create(false, true, new NullProgressMonitor()); 
			folders.put("src/ui/src", srcFolder234) ;

			//src/ui/src/components
			IFolder srcFolder2341 = srcFolder234.getFolder(new Path("components"));
			srcFolder2341.create(false, true, new NullProgressMonitor());
			folders.put("src/ui/src/components", srcFolder2341); 
			
			//src/ui/src/actions
			IFolder srcFolder2342 = srcFolder234.getFolder(new Path("actions"));
			srcFolder2342.create(false, true, new NullProgressMonitor());
			folders.put("src/ui/src/actions", srcFolder2342); 
			
			//src/ui/src/reducers
			IFolder srcFolder2343 = srcFolder234.getFolder(new Path("reducers"));
			srcFolder2343.create(false, true, new NullProgressMonitor());
			folders.put("src/ui/src/reducers", srcFolder2343); 
			
			//src/ui/src/containers
			IFolder srcFolder2344 = srcFolder234.getFolder(new Path("containers"));
			srcFolder2344.create(false, true, new NullProgressMonitor());
			folders.put("src/ui/src/containers", srcFolder2344);
			
			//src/ui/src/containers
            IFolder srcFolder2345 = srcFolder234.getFolder(new Path("utils"));
            srcFolder2345.create(false, true, new NullProgressMonitor());
            folders.put("src/ui/src/utils", srcFolder2345);
            
            //src/ui/src/assets
			IFolder srcFolder2346 = srcFolder234.getFolder(new Path("assets"));
			srcFolder2346.create(false, true, new NullProgressMonitor());
			folders.put("src/ui/src/assets", srcFolder2346); 
			
			//src/ui/src/assets/flags
			IFolder srcFolder23461 = srcFolder2346.getFolder(new Path("flags"));
			srcFolder23461.create(false, true, new NullProgressMonitor());
			folders.put("src/ui/src/assets/flags", srcFolder23461);
			
		} else if (uiType.equalsIgnoreCase("Angular4")) {
			// src/ui
			IFolder srcFolder23 = srcFolder.getFolder(new Path("ui"));
			srcFolder23.create(false, true, new NullProgressMonitor());
			folders.put("src/ui", srcFolder23);
			// src/ui/e2e
			IFolder srcFolder231 = srcFolder23.getFolder(new Path("e2e"));
			srcFolder231.create(false, true, new NullProgressMonitor());
			folders.put("src/ui/e2e", srcFolder231);

			IFolder srcFolder233 = srcFolder23.getFolder(new Path("node_modules"));
			srcFolder233.create(false, true, new NullProgressMonitor());
			folders.put("src/ui/node_modules", srcFolder233);

			IFolder srcFolder234 = srcFolder23.getFolder(new Path("src"));
			srcFolder234.create(false, true, new NullProgressMonitor());
			folders.put("src/ui/src", srcFolder234);

			// src/ui/src/app
			IFolder srcFolder2341 = srcFolder234.getFolder(new Path("app"));
			srcFolder2341.create(false, true, new NullProgressMonitor());
			folders.put("src/ui/src/app", srcFolder2341);

			// src/ui/src/app/home
			IFolder srcFolder23411 = srcFolder2341.getFolder(new Path("home"));
			srcFolder23411.create(false, true, new NullProgressMonitor());
			folders.put("src/ui/src/app/home", srcFolder23411);

			// src/ui/src/assets
			IFolder srcFolder2342 = srcFolder234.getFolder(new Path("assets"));
			srcFolder2342.create(false, true, new NullProgressMonitor());
			folders.put("src/ui/src/assets", srcFolder2342);

			// src/ui/src/environments
			IFolder srcFolder2343 = srcFolder234.getFolder(new Path("environments"));
			srcFolder2343.create(false, true, new NullProgressMonitor());
			folders.put("src/ui/src/environments", srcFolder2343);

		}

		// src/main/resources
		IFolder srcFolder42 = srcFolder21.getFolder(new Path("resources"));
		srcFolder42.create(false, true, new NullProgressMonitor());
		folders.put("src/main/resources", srcFolder42);

		// src/main/resources/scripts
		IFolder srcFolder421 = srcFolder42.getFolder(new Path("scripts"));
		srcFolder421.create(false, true, new NullProgressMonitor());
		folders.put("src/main/resources/scripts", srcFolder421);

		// src/main/resources/public
		IFolder srcFolder422 = srcFolder42.getFolder(new Path("public"));
		srcFolder422.create(false, true, new NullProgressMonitor());
		folders.put("src/main/resources/public", srcFolder422);

		// Statics
		// src/main/resources/public/resources
		IFolder resourcesFolder = srcFolder422.getFolder(new Path("resources"));
		resourcesFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/resources/public/resources", resourcesFolder);

		// src/main/resources/public/resources/css
		IFolder cssFolder = resourcesFolder.getFolder(new Path("css"));
		cssFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/resources/public/resources/css", cssFolder);

		// src/main/resources/public/resources/css/libs
		IFolder thirdPartyCssFolder = cssFolder.getFolder(new Path("libs"));
		thirdPartyCssFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/resources/public/resources/css/libs", thirdPartyCssFolder);

		// src/main/resources/public/resources/css/fonts
		IFolder thirdPartyFontsFolder = cssFolder.getFolder(new Path("fonts"));
		thirdPartyFontsFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/resources/public/resources/css/fonts", thirdPartyFontsFolder);

		// src/main/resources/public/resources/js
		IFolder jsFolder = resourcesFolder.getFolder(new Path("js"));
		jsFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/resources/public/resources/js", jsFolder);

		// src/main/resources/public/resources/js/angular_controllers
		IFolder angularControllerFolder = jsFolder.getFolder(new Path("angular_controllers"));
		angularControllerFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/resources/public/resources/js/angular_controllers", angularControllerFolder);

		// src/main/resources/public/resources/js/angular_factories
		IFolder angularFactoriesFolder = jsFolder.getFolder(new Path("angular_factories"));
		angularFactoriesFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/resources/public/resources/js/angular_factories", angularFactoriesFolder);

		// src/main/resources/public/resources/js/angular_templates
		IFolder angularTemplatesFolder = jsFolder.getFolder(new Path("angular_templates"));
		angularTemplatesFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/resources/public/resources/js/angular_templates", angularTemplatesFolder);

		// src/main/resources/public/resources/js/angular_services
		IFolder angularServicesFolder = jsFolder.getFolder(new Path("angular_services"));
		angularServicesFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/resources/public/resources/js/angular_services", angularServicesFolder);

		// src/main/resources/public/resources/js/fonts
		IFolder fontsFolder = jsFolder.getFolder(new Path("fonts"));
		fontsFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/resources/public/resources/js/fonts", fontsFolder);

		// src/main/resources/public/resources/js/libs
		IFolder jsLibsFolder = jsFolder.getFolder(new Path("libs"));
		jsLibsFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/resources/public/resources/js/libs", jsLibsFolder);

		// src/main/resources/public/resources/js/models
		IFolder modelsFolder = jsFolder.getFolder(new Path("models"));
		modelsFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/resources/public/resources/js/models", modelsFolder);

		// src/main/resources/public/resources/js/collections
		IFolder collectionsFolder = jsFolder.getFolder(new Path("collections"));
		collectionsFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/resources/public/resources/js/collections", collectionsFolder);

		// src/main/resources/public/resources/js/globals
		IFolder globalsFolder = jsFolder.getFolder(new Path("globals"));
		globalsFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/resources/public/resources/js/globals", globalsFolder);

		// src/main/resources/public/resources/js/views
		IFolder viewsFolder = jsFolder.getFolder(new Path("views"));
		viewsFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/resources/public/resources/js/views", viewsFolder);

		// src/main/resources/public/resources/js/presenters
		IFolder presentersFolder = jsFolder.getFolder(new Path("presenters"));
		presentersFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/resources/public/resources/js/presenters", presentersFolder);

		// src/main/resources/public/resources/js/templates
		IFolder templatesFolder = jsFolder.getFolder(new Path("templates"));
		templatesFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/resources/public/resources/js/templates", templatesFolder);

		// end of Statics

		// src/test/java
		IFolder srcFolder22 = srcFolder.getFolder(new Path("test"));
		srcFolder22.create(false, true, new NullProgressMonitor());
		IFolder srcFolder32 = srcFolder22.getFolder(new Path("java"));
		srcFolder32.create(false, true, new NullProgressMonitor());
		folders.put("src/test/java", srcFolder32);

		// src/test/resources
		IFolder srcFolder43 = srcFolder22.getFolder(new Path("resources"));
		srcFolder43.create(false, true, new NullProgressMonitor());
		folders.put("src/test/resources", srcFolder43);

		// src/main/webapp
		IFolder srcFolder41 = srcFolder21.getFolder(new Path("webapp"));
		srcFolder41.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp", srcFolder41);

		// src/main/webapp/WEB-INF
		IFolder srcFolder51 = srcFolder41.getFolder(new Path("WEB-INF"));
		srcFolder51.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF", srcFolder51);

		// .settings
		IFolder settingsFolder = container.getFolder(new Path(".settings"));
		settingsFolder.create(false, true, new NullProgressMonitor());
		folders.put(".settings", settingsFolder);

		// src/main/webapp/WEB-INF/spring
		IFolder springFolder = srcFolder51.getFolder(new Path("spring"));
		springFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/spring", springFolder);

	}

	private void createClassPathEntries(IFolder outputFolder2, IFolder outputFolder3, IFolder srcFolder31,
			IFolder srcFolder32, IFolder mainResourcesFolder, IFolder testResourcesFolder, IJavaProject javaProject)
			throws JavaModelException {
		IClasspathEntry javasrc = JavaCore.newSourceEntry(srcFolder31.getFullPath(), null, null,
				outputFolder2.getFullPath(),
				new IClasspathAttribute[] { JavaCore.newClasspathAttribute("optional", "true"),
						JavaCore.newClasspathAttribute("maven.pomderived", "true") });

		IClasspathEntry mainResources = JavaCore.newSourceEntry(mainResourcesFolder.getFullPath(), null, null,
				outputFolder2.getFullPath(),
				new IClasspathAttribute[] { JavaCore.newClasspathAttribute("optional", "true"),
						JavaCore.newClasspathAttribute("maven.pomderived", "true") });

		IClasspathEntry testsrc = JavaCore.newSourceEntry(srcFolder32.getFullPath(), null, null,
				outputFolder3.getFullPath(),
				new IClasspathAttribute[] { JavaCore.newClasspathAttribute("optional", "true"),
						JavaCore.newClasspathAttribute("maven.pomderived", "true"),
						JavaCore.newClasspathAttribute("test", "true") });
		IClasspathEntry testResources = JavaCore.newSourceEntry(testResourcesFolder.getFullPath(), null, null,
				outputFolder3.getFullPath(),
				new IClasspathAttribute[] { JavaCore.newClasspathAttribute("optional", "true"),
						JavaCore.newClasspathAttribute("maven.pomderived", "true"),
						JavaCore.newClasspathAttribute("test", "true") });

		IClasspathEntry jre = JavaCore.newContainerEntry(new Path(
				"org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/J2SE-1.5"),
				new IAccessRule[0],
				new IClasspathAttribute[] { JavaCore.newClasspathAttribute("maven.pomderived", "true") }, false);

		IClasspathEntry mavenContainer = JavaCore.newContainerEntry(
				new Path("org.eclipse.m2e.MAVEN2_CLASSPATH_CONTAINER"), new IAccessRule[0],
				new IClasspathAttribute[] { JavaCore.newClasspathAttribute("maven.pomderived", "true"),
						JavaCore.newClasspathAttribute("org.eclipse.jst.component.dependency", "/WEB-INF/lib") },
				false);

		IClasspathEntry[] entries = new IClasspathEntry[] { javasrc, testsrc, mainResources, testResources, jre,
				mavenContainer };
		javaProject.setRawClasspath(entries, outputFolder2.getFullPath(), new NullProgressMonitor());
	}

	private void addVariousSettings(IFolder settingsFolder, IProject project, String basePackageName,
			String controllerPackageName, IProgressMonitor monitor) throws Exception {
		// TODO Add value substitution capability for setting values within settings
		// files
		InputStream jdtCorePref = this.getClass()
				.getResourceAsStream("/vasbootbuilder/resources/settings/org.eclipse.jdt.core.prefs.template");
		CommonUtils.addFileToProject(settingsFolder, new Path("org.eclipse.jdt.core.prefs"), jdtCorePref, monitor);

		InputStream m2eCorePref = this.getClass()
				.getResourceAsStream("/vasbootbuilder/resources/settings/org.eclipse.m2e.core.prefs.template");
		CommonUtils.addFileToProject(settingsFolder, new Path("org.eclipse.m2e.core.prefs"), m2eCorePref, monitor);

		// InputStream wstCommonComponent = this.getClass().getResourceAsStream(
		// "/vasbootbuilder/resources/settings/org.eclipse.wst.common.component.template");
		CommonUtils.addFileToProject(settingsFolder, new Path("org.eclipse.wst.common.component"),
				TemplateMerger.merge("/vasbootbuilder/resources/settings/org.eclipse.wst.common.component.template",
						project.getName(), basePackageName, controllerPackageName, ""),
				monitor);

		InputStream wstCommonProject = this.getClass().getResourceAsStream(
				"/vasbootbuilder/resources/settings/org.eclipse.wst.common.project.facet.core.xml.template");
		CommonUtils.addFileToProject(settingsFolder, new Path("org.eclipse.wst.common.project.facet.core.xml"),
				wstCommonProject, monitor);

		InputStream wstJsdtContainer = this.getClass().getResourceAsStream(
				"/vasbootbuilder/resources/settings/org.eclipse.wst.jsdt.ui.superType.container.template");
		CommonUtils.addFileToProject(settingsFolder, new Path("org.eclipse.wst.jsdt.ui.superType.container"),
				wstJsdtContainer, monitor);

		InputStream wstJsdtName = this.getClass().getResourceAsStream(
				"/vasbootbuilder/resources/settings/org.eclipse.wst.jsdt.ui.superType.name.template");
		CommonUtils.addFileToProject(settingsFolder, new Path("org.eclipse.wst.jsdt.ui.superType.name"), wstJsdtName,
				monitor);

		InputStream jsdtScope = this.getClass()
				.getResourceAsStream("/vasbootbuilder/resources/settings/jsdtscope.template");
		CommonUtils.addFileToProject(settingsFolder, new Path(".jsdtscope"), jsdtScope, monitor);
	}

	private void addVASBootBuilderSettings(IFolder settingsFolder, IProject project, String basePackageName,
			boolean secureEnabled, boolean useMongo, boolean prepForOracle, 
			boolean prepForHSQL, String uiType, IProgressMonitor monitor)
			throws Exception {
		// String props = "basePackage=" + basePackageName;
		StringWriter properties = new StringWriter();
		properties.append("basePackage=" + basePackageName);
		properties.append("\nsecureCodeEnabled=" + secureEnabled);
		properties.append("\nuseMongo=" + useMongo);
		properties.append("\nuiType=" + uiType);
		properties.append("\nprepForOracle=" + prepForOracle);
		properties.append("\nprepForHSQL=" + prepForHSQL);

		InputStream stream = new ByteArrayInputStream(properties.toString().getBytes());
		CommonUtils.addFileToProject(settingsFolder, new Path("org.bsbuilder.settings"), stream, monitor);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
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
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
		this.config = config;
	}

	public String createComponentTag(String domainClassName) {
		String newTagname = StringUtils.uncapitalize(domainClassName);
		int indexOfUppercase = lastIndexOfUCL(newTagname);
		if (indexOfUppercase > -1) {
			newTagname = newTagname.substring(0, indexOfUppercase) + "-"
					+ newTagname.substring(indexOfUppercase).toLowerCase();
		} else {
			newTagname = newTagname.toLowerCase();
		}
		return newTagname;
	}

	public int lastIndexOfUCL(String str) {
		for (int i = str.length() - 1; i >= 0; i--) {
			if (Character.isUpperCase(str.charAt(i))) {
				return i;
			}
		}
		return -1;
	}

	public class SourceCodeGeneratorParameters {
		public String getBaseControllerSourceCode() {
            return baseControllerSourceCode;
        }

        public void setBaseControllerSourceCode(String baseControllerSourceCode) {
            this.baseControllerSourceCode = baseControllerSourceCode;
        }

        private String basePackageName;
		private String controllerPackageName;
		private String mainControllerSourceCode;
		private String baseControllerSourceCode;
		private String miscControllerSourceCode;
		private String mainConfigSourceCode;
		private String springBootStarterClassName;
		private String springBootStarterSourceCode;
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
		private String springSecurityConfigSourceCode;
		private String springSecurityAuhenticationProvider;
		private String customLogoutSuccessHandlerSourceCode;
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

		
		public String getCustomLogoutSuccessHandlerSourceCode() {
            return customLogoutSuccessHandlerSourceCode;
        }

        public void setCustomLogoutSuccessHandlerSourceCode(String customLogoutSuccessHandlerSourceCode) {
            this.customLogoutSuccessHandlerSourceCode = customLogoutSuccessHandlerSourceCode;
        }

        public String getMainConfigSourceCode() {
            return mainConfigSourceCode;
        }

        public void setMainConfigSourceCode(String mainConfigSourceCode) {
            this.mainConfigSourceCode = mainConfigSourceCode;
        }

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

		public void setSecurityUserDetailsServiceSourceCode(String securityUserDetailsServiceSourceCode) {
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

		public void setSecurityUserDetailsSourceCode(String securityUserDetailsSourceCode) {
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

		public void setWebServiceInterfaceSourceCode(String webServiceInterfaceSourceCode) {
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

		public String getSpringBootStarterClassName() {
			return springBootStarterClassName;
		}

		public void setSpringBootStarterClassName(String springBootStarterClassName) {
			this.springBootStarterClassName = springBootStarterClassName;
		}

		public String getSpringBootStarterSourceCode() {
			return springBootStarterSourceCode;
		}

		public void setSpringBootStarterSourceCode(String springBootStarterSourceCode) {
			this.springBootStarterSourceCode = springBootStarterSourceCode;
		}

		public String getSpringSecurityConfigSourceCode() {
			return springSecurityConfigSourceCode;
		}

		public void setSpringSecurityConfigSourceCode(String springSecurityConfigSourceCode) {
			this.springSecurityConfigSourceCode = springSecurityConfigSourceCode;
		}

		public String getSpringSecurityAuhenticationProvider() {
			return springSecurityAuhenticationProvider;
		}

		public void setSpringSecurityAuhenticationProvider(String springSecurityAuhenticationProvider) {
			this.springSecurityAuhenticationProvider = springSecurityAuhenticationProvider;
		}

		public String getMiscControllerSourceCode() {
			return miscControllerSourceCode;
		}

		public void setMiscControllerSourceCode(String miscControllerSourceCode) {
			this.miscControllerSourceCode = miscControllerSourceCode;
		}

	}

}