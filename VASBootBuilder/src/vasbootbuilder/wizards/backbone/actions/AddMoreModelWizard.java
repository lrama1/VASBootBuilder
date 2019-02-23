package vasbootbuilder.wizards.backbone.actions;

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

import vasbootbuilder.wizards.site.BackboneProjectWizardPageFive;
import vasbootbuilder.wizards.site.BackboneProjectWizardPageFour;
import vasbootbuilder.wizards.site.BackboneProjectWizardPageThree;
import vasbootbuilder.wizards.site.utils.CommonUtils;
import vasbootbuilder.wizards.site.utils.TemplateMerger;

public class AddMoreModelWizard extends Wizard implements INewWizard {

	private BackboneProjectWizardPageThree pageThree;
	private BackboneProjectWizardPageFour pageFour;
	private BackboneProjectWizardPageFive pageFive;
	private IWorkbench workbench;
	private IStructuredSelection selection;
	private Properties vasBootBuilderProperties = new Properties();
	private Boolean generateSecurityCode;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;

		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof IAdaptable) {
			IProject project = (IProject) ((IAdaptable) firstElement).getAdapter(IProject.class);
			String basePath = project.getLocation().toOSString();
			try {
				vasBootBuilderProperties
						.load(new FileReader(new File(basePath + "/.settings/" + "org.bsbuilder.settings")));
			} catch (Exception e) {
				// man we really need to clean this up
				e.printStackTrace();
			}
		}
	}

	@Override
	public void addPages() {
		generateSecurityCode = Boolean.parseBoolean(vasBootBuilderProperties.getProperty("secureCodeEnabled"));
		String uiType = vasBootBuilderProperties.getProperty("uiType");
		pageThree = new BackboneProjectWizardPageThree("");
		pageFour = new BackboneProjectWizardPageFour("");
		System.out.println("********************************** " + uiType);
		pageFive = new BackboneProjectWizardPageFive("", uiType);
		addPage(pageThree);
		if (generateSecurityCode)
			addPage(pageFour);
		addPage(pageFive);
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		// CommonUtils.addFileToProject(container, path, contentStream, monitor)

		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof IAdaptable) {
			IProject project = (IProject) ((IAdaptable) firstElement).getAdapter(IProject.class);
			IContainer projectContainer = (IContainer) project;
			try {
				String projectName = project.getName();
				String basePackageName = vasBootBuilderProperties.getProperty("basePackage");
				String useMongo = vasBootBuilderProperties.getProperty("useMongo");
				String uiType = vasBootBuilderProperties.getProperty("uiType");
				if(pageFive.getUIType().equalsIgnoreCase("None")) {
				    //use the selection from wizard only if it was switched to None
				    uiType = pageFive.getUIType();
				}			
				
				String prepForHSQL = vasBootBuilderProperties.getProperty("prepForHSQL");
				
				Map<String, Object> modelAttributes = pageThree.getModelAttributes();

				// create Domain Class
				createJavaDomainClass(projectContainer, basePackageName, pageThree.getDomainClassName());

				// create SampleData
				createSampleData(projectContainer, pageThree.getDomainClassName(), modelAttributes);
				
				//add data for HSQL
				if(StringUtils.equals(prepForHSQL, "true")) {
					createSampleDataForHSQL(projectContainer, pageThree.getDomainClassName(), modelAttributes);;
				}

				// create SampleDate for Mongo
				if (StringUtils.equals(useMongo, "true")) {
					createSampleDataForMongo(projectContainer, pageThree.getDomainClassName(), modelAttributes);
				}

				// createController
				createControllerClass(projectContainer, basePackageName);

				// createService
				createServiceClass(projectContainer, basePackageName);

				// createDao
				createDaoClass(projectContainer, basePackageName, modelAttributes);

				if (uiType.equalsIgnoreCase("BackboneJS")) {
					/************** BACKBONE SPECIFIC ****************************/
					// create backbone model
					createBackboneModel(projectContainer, projectName);

					// create backbone collection
					createBackboneCollection(projectContainer, projectName);

					// create backbone edit view
					createBackboneEditView(projectContainer, projectName);

					// create backbone collection view
					createCollectionView(projectContainer, projectName);

					// create Presenter
					// createPresenter(projectContainer, projectName);

					// create Backbone Template files
					createEditAndListTemplateFiles(projectContainer, pageThree.getDomainClassName(), modelAttributes);

					//
					addNewRoutesToRouter(projectContainer, projectName);
					addNewTabsToHomePage(projectContainer, pageThree.getDomainClassName());
					/************** END OF BACKBONE SPECIFIC ****************************/
				} else if (uiType.equalsIgnoreCase("AngularJS")) {
					/************** ANGULAR SPECIFIC ****************************/
					createAngularControllers(projectContainer, projectName);
					createAngularService(projectContainer, projectName);
					createAngularTemplates(projectContainer, projectName);
					appendNewScriptsToAngular(projectContainer,
							createNewScriptsTag(projectName, pageThree.getDomainClassName()));
					appendNewRouteExpressionToAngular(projectContainer,
							createWhenExpressions(projectName, pageThree.getDomainClassName()));
					addNewTabsToAngularHomePage(projectContainer, pageThree.getDomainClassName());

					/************** END OF ANGULAR SPECIFIC ****************************/
				} else if (uiType.equalsIgnoreCase("Angular4")) {
					createAngular4Templates(projectContainer, projectName);
					addNewTabsToAngular4AppComponentPage(projectContainer, pageThree.getDomainClassName());
				} else if (uiType.equalsIgnoreCase("React")) {
					createReactTemplates(projectContainer, projectName);
				} else if (uiType.equalsIgnoreCase("VueJS")) {
					/************** VUEJS SPECIFIC ****************************/
					createVueTemplates(projectContainer, projectName);
					addNewRoutesToMainJS(projectContainer, pageThree.getDomainClassName());
					addNewTabsToVueAppPage(projectContainer, pageThree.getDomainClassName());

					String componentTag = pageThree.getDomainClassName().toLowerCase();
					insertStatementToHomeVue(projectContainer, pageThree.getDomainClassName(),
							"\\<.*-list[\\w\\W\\s]*\\>[\\w\\s]*\\</.*-list\\>",
							"\n<" + componentTag + "-list></" + componentTag + "-list>");

					insertStatementToHomeVue(projectContainer, pageThree.getDomainClassName(), "\\<script\\>",
							"\nimport " + pageThree.getDomainClassName() + "s from './" + pageThree.getDomainClassName()
									+ "s.vue'");

					insertStatementToHomeVue(projectContainer, pageThree.getDomainClassName(), "components\\s*:\\s*\\{",
							"\n'" + componentTag + "-list' : " + pageThree.getDomainClassName() + "s,\n");
					// addNewTabsToAppVue(projectContainer, projectName);
					/************** END OF VUEJS SPECIFIC ****************************/
				}

				project.refreshLocal(IProject.DEPTH_INFINITE, new NullProgressMonitor());

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return true;
	}

	private void createReactTemplates(IContainer projectContainer, String projectName) throws Exception {
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("projectName”", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());
		mapOfValues.put("fieldTypes", pageThree.getFieldTypes());
		String domainName = domainClassName.toLowerCase();
		
		// Domain Folders
		IFolder componentsFolder = projectContainer.getFolder(new Path("src/ui/src/components/"));
		IFolder containersFolder = projectContainer.getFolder(new Path("src/ui/src/containers/"));

		// Domain List
		CommonUtils.addFileToProject(componentsFolder, new Path(domainClassName + "List.js"),
				TemplateMerger.merge("/vasbootbuilder/resources/web/js/react/component/domain/DomainList-template.js",

						mapOfValues),
				new NullProgressMonitor());
		
		CommonUtils.addFileToProject(containersFolder, new Path(domainClassName + "ListContainer.js"),
				TemplateMerger.merge("/vasbootbuilder/resources/web/js/react/component/domain/DomainListContainer-template.js",

						mapOfValues),
				new NullProgressMonitor());
		//mapOfValues.put("CcomponentName", domainClassName + "List");

		// Domain Details (Editing Form)
		CommonUtils.addFileToProject(componentsFolder, new Path(domainClassName + "Edit.js"), TemplateMerger
				.merge("/vasbootbuilder/resources/web/js/react/component/domain/DomainDetail-template.js", mapOfValues),
				new NullProgressMonitor());
		//Test
		CommonUtils.addFileToProject(componentsFolder, new Path(domainClassName + "Edit.test.js"), TemplateMerger
				.merge("/vasbootbuilder/resources/web/js/react/component/domain/DomainDetailTest-template.js", mapOfValues),
				new NullProgressMonitor());
		
		CommonUtils.addFileToProject(containersFolder, new Path(domainClassName + "EditContainer.js"), TemplateMerger
				.merge("/vasbootbuilder/resources/web/js/react/component/domain/DomainDetailContainer-template.js", mapOfValues),
				new NullProgressMonitor());
		//mapOfValues.put("ComponentName", domainClassName + "Detail");
		
		// actions
		IFolder actionsContainerFolder = projectContainer.getFolder(new Path("src/ui/src/actions"));
		CommonUtils.addFileToProject(actionsContainerFolder, new Path("index.js"),
				TemplateMerger.merge("/vasbootbuilder/resources/web/js/react/actions/index-template.js",	mapOfValues),
				new NullProgressMonitor());
		
		// reducers
		IFolder reducersContainerFolder = projectContainer.getFolder(new Path("src/ui/src/reducers"));
		CommonUtils.addFileToProject(reducersContainerFolder, new Path(domainName + "Reducer.js"),
				TemplateMerger.merge(
						"/vasbootbuilder/resources/web/js/react/reducers/domain-reducer-template.js",
						mapOfValues),
				new NullProgressMonitor());
		addReducerToIndexReducer(projectContainer, domainClassName);
		
		addNewRoutesToReact(projectContainer, domainClassName);
		
		modifyMockServer(projectContainer, projectName, domainClassName);
		
		addMockData(projectContainer, domainClassName);
	}
	
	private void modifyMockServer(IContainer projectContainer, String projectName, String domainClassName) throws Exception{	    
	    IFolder mockFolder = projectContainer.getFolder(new Path("src/ui/mocks"));
	    IFile serverJSFile = mockFolder.getFile("server.js");
	    File fileToAugment = serverJSFile.getRawLocation().toFile();
	    
	    String domainObjectName = domainClassName.toLowerCase();
	    String stringToInsert = 
                "const " + domainObjectName + "s = require('./" + domainClassName + "s.json')\n" + 
                "app.get('/" + projectName + "/" + domainObjectName + "s', (req, res) =>{\n" + 
                "    return res.json(" + domainObjectName + "s)\n" + 
                "})\n";
    
        String fileContentsx = FileUtils.readFileToString(fileToAugment);
        StringBuffer buffer = new StringBuffer(fileContentsx);

        String appListenRegexString = "app.listen";
        Pattern appListenPattern = Pattern.compile(appListenRegexString, Pattern.MULTILINE );
        Matcher appListenMatcher = appListenPattern.matcher(buffer);
        if (appListenMatcher.find()) {
            buffer.insert(appListenMatcher.start(), stringToInsert);
        }
        
        InputStream modifiedFileContent = new ByteArrayInputStream(CommonUtils.prettifyJS(buffer.toString()).getBytes());
        serverJSFile.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
        serverJSFile.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());
	}
	
	private void addMockData(IContainer projectContainer, String domainClassName) throws Exception {
	    IFolder mockFolder = projectContainer.getFolder(new Path("src/ui/mocks"));
	    Map<String, Object> mapOfValues = new HashMap<String, Object>();
        mapOfValues.put("domainClassName", domainClassName);
        mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
        mapOfValues.put("attrs", pageThree.getModelAttributes());
        mapOfValues.put("fieldTypes", pageThree.getFieldTypes());
	    CommonUtils.addFileToProject(mockFolder, new Path(domainClassName +"s.json"), TemplateMerger.merge(
                "/vasbootbuilder/resources/web/js/react/other/mockdata-template.json", mapOfValues), new NullProgressMonitor());
	}
	
	private void addReducerToIndexReducer(IContainer projectContainer, String domainClassName)  throws Exception{
		IFolder jsFolder = projectContainer.getFolder(new Path("src/ui/src/reducers"));
		IFile appModuleFile = jsFolder.getFile("index.js");
		File file = appModuleFile.getRawLocation().toFile();
		String domainObjectName = domainClassName.substring(0,1).toLowerCase() + domainClassName.substring(1);

		String fileContents = FileUtils.readFileToString(file);
		StringBuffer buffer = new StringBuffer(fileContents);
		
		String importSectionRegex = "import.*";
		Pattern importSectionPatter = Pattern.compile(importSectionRegex );
		Matcher importSectionMatcher = importSectionPatter.matcher(buffer);
		int indexToInsertNewImport = 0;
		while(importSectionMatcher.find()) {
			indexToInsertNewImport =importSectionMatcher.end();
		}

		String newImport = "import { " + domainObjectName + "sReducer, " + domainObjectName + "FetchReducer } " +
				"from './" + domainObjectName + "Reducer';";
		buffer.insert(indexToInsertNewImport, "\n" + newImport);
		
		String combineReducersExport = "";
		String combineReducerRegex = "export\\s*default\\s* combineReducers.*\\);$";
		Pattern combineReducersPattern = Pattern.compile(combineReducerRegex, Pattern.DOTALL | Pattern.MULTILINE );
		Matcher combineReducersMatcher = combineReducersPattern.matcher(buffer);
		if (combineReducersMatcher.find()) {
			String currentRoutes = combineReducersMatcher.group();
			String endOfCombineReducer = "\\n\\}\\s*\\);$";
			Pattern fetchPattern = Pattern.compile(endOfCombineReducer, Pattern.DOTALL | Pattern.MULTILINE );
			Matcher fetchmatcher = fetchPattern.matcher(currentRoutes);
			if(fetchmatcher.find()) {		
				combineReducersExport = fetchmatcher.replaceAll("," + "\n    " +
						domainObjectName + "FetchReducer,\n    " + 
						domainObjectName + "sReducer"
						+ fetchmatcher.group());
			}
			
			buffer = new StringBuffer(combineReducersMatcher.replaceAll(combineReducersExport));
		}
		
		//System.out.println(updatedDispatchConst);
		InputStream modifiedFileContent = new ByteArrayInputStream(CommonUtils.prettifyJS(buffer.toString()).getBytes());
		appModuleFile.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
		appModuleFile.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());

	}

	private void addNewRoutesToReact(IContainer projectContainer, String domainClassName) throws Exception {
		String domainObjectName = domainClassName.substring(0,1).toLowerCase() + domainClassName.substring(1);
		IFolder jsFolder = projectContainer.getFolder(new Path("src/ui/src/components"));
		IFile appModuleFile = jsFolder.getFile("App.js");
		File file = appModuleFile.getRawLocation().toFile();

		String fileContents = FileUtils.readFileToString(file);
		String importRegex = "import.*\\';";
		Pattern importPattern = Pattern.compile(importRegex, Pattern.DOTALL);
		Matcher importmatcher = importPattern.matcher(fileContents);
		String importStringToAdd = "import " + domainClassName + "ListContainer from '../containers/" + domainClassName	+ "ListContainer';\n" 
				+ "import " + domainClassName + "EditContainer from '../containers/" + domainClassName + "EditContainer';\n"
				+ "import {fetchAll" + domainClassName + "s} from '../actions'; \n" ;
		if (importmatcher.find()) {
			String currentRoutes = importmatcher.group();
			fileContents = importmatcher.replaceAll(currentRoutes + "\n" + importStringToAdd);
		}

		String linkRegex = "<li.*>.*<Link.*>.*</Link>.*</li>";
		Pattern linkPattern = Pattern.compile(linkRegex, Pattern.DOTALL | Pattern.MULTILINE);
		Matcher linkmatcher = linkPattern.matcher(fileContents);
		String linkStringToAdd = "<li><Link  className=\"nav-link\" to=\"/" + domainClassName.toLowerCase() + "s\" "
				+ " onClick={() => props.fetchAll" + domainClassName + "s('/" + domainObjectName + "s?page=1&per_page=10')} >" + domainClassName
				+ "</Link></li>";
		if (linkmatcher.find()) {
			String currentRoutes = linkmatcher.group();
			fileContents = linkmatcher.replaceAll(currentRoutes + "\n" + linkStringToAdd);
		}
		String routeRegex = "<Route.*\\/>";
		Pattern routePattern = Pattern.compile(routeRegex, Pattern.DOTALL);
		Matcher routeMatcher = routePattern.matcher(fileContents);
		String routeStringToAdd = "<Route path=\"/" + domainClassName.toLowerCase() + "s\" exact component={"
				+ domainClassName + "ListContainer}/>\n"

				+ "<Route path=\"/" + domainClassName.toLowerCase() + "\" exact component={" + domainClassName
				+ "EditContainer}/>";
		if (routeMatcher.find()) {
			String currentRoutes = routeMatcher.group();
			fileContents = routeMatcher.replaceAll(currentRoutes + "\n" + routeStringToAdd);
		}
		
		//add dispatcher in mapDispatchToProps
		fileContents = addDispatcher(fileContents, domainClassName);
		
		InputStream modifiedFileContent = new ByteArrayInputStream(CommonUtils.prettifyJS(fileContents).getBytes());
		appModuleFile.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
		appModuleFile.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());
	}
	
	private String addDispatcher(String originalFileContent, String domainClassName) {
		String updatedDispatchConst = "";
		String mapDispatchToPropsRegex = "mapDispatchToProps.*\\}$";
		Pattern mapDispatchToPropsPattern = Pattern.compile(mapDispatchToPropsRegex, Pattern.DOTALL | Pattern.MULTILINE );
		Matcher mapDispatchToPropsMatcher = mapDispatchToPropsPattern.matcher(originalFileContent);
		if (mapDispatchToPropsMatcher.find()) {
			String currentRoutes = mapDispatchToPropsMatcher.group();
			
			String fetchRegex = "return.*\\)$";
			Pattern fetchPattern = Pattern.compile(fetchRegex, Pattern.DOTALL | Pattern.MULTILINE );
			Matcher fetchmatcher = fetchPattern.matcher(currentRoutes);
			if(fetchmatcher.find()) {					
				updatedDispatchConst = fetchmatcher.replaceAll(fetchmatcher.group() + ",\n" + "\t    fetchAll" + domainClassName + "s: " +
			           "(url) => dispatch(fetchAll" + domainClassName + "s(url))");
			}
			
			return mapDispatchToPropsMatcher.replaceAll(updatedDispatchConst);
		}
		return originalFileContent;
	}

	private void createAngular4Templates(IContainer projectContainer, String projectName) throws Exception {
		String domainClassName = pageThree.getDomainClassName();
		String domainName = domainClassName.toLowerCase();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());
		mapOfValues.put("fieldTypes", pageThree.getFieldTypes());
		// create the folders
		IFolder domainFolder = projectContainer.getFolder(new Path("src/ui/src/app/" + domainName));
		domainFolder.create(false, true, new NullProgressMonitor());

		IFolder domainListFolder = domainFolder.getFolder(new Path(domainName + "-list"));
		domainListFolder.create(false, true, new NullProgressMonitor());

		IFolder domainEditFolder = domainFolder.getFolder(new Path(domainName + "-edit"));
		domainEditFolder.create(false, true, new NullProgressMonitor());

		// model and service classes
		CommonUtils.addFileToProject(projectContainer.getFolder(new Path("src/ui/src/app/" + domainName)),
				new Path(domainName + ".model.ts"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/angular4/app/domain/domain.model.ts", mapOfValues),
				new NullProgressMonitor());
		CommonUtils.addFileToProject(projectContainer.getFolder(new Path("src/ui/src/app/" + domainName)),
				new Path(domainName + ".service.ts"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/angular4/app/domain/domain.service.ts", mapOfValues),
				new NullProgressMonitor());

		// edit components
		CommonUtils.addFileToProject(
				projectContainer.getFolder(new Path("src/ui/src/app/" + domainName + "/" + domainName + "-edit")),
				new Path(domainName + "-edit.component.css"),
				TemplateMerger.merge("/vasbootbuilder/resources/web/js/angular4/app/domain/domain-edit.component.css",
						mapOfValues),
				new NullProgressMonitor());
		CommonUtils.addFileToProject(
				projectContainer.getFolder(new Path("src/ui/src/app/" + domainName + "/" + domainName + "-edit")),
				new Path(domainName + "-edit.component.html"),
				TemplateMerger.merge("/vasbootbuilder/resources/web/js/angular4/app/domain/domain-edit.component.html",
						mapOfValues),
				new NullProgressMonitor());
		CommonUtils.addFileToProject(
				projectContainer.getFolder(new Path("src/ui/src/app/" + domainName + "/" + domainName + "-edit")),
				new Path(domainName + "-edit.component.spec.ts"),
				TemplateMerger.merge(
						"/vasbootbuilder/resources/web/js/angular4/app/domain/domain-edit.component.spec.ts",
						mapOfValues),
				new NullProgressMonitor());
		CommonUtils.addFileToProject(
				projectContainer.getFolder(new Path("src/ui/src/app/" + domainName + "/" + domainName + "-edit")),
				new Path(domainName + "-edit.component.ts"),
				TemplateMerger.merge("/vasbootbuilder/resources/web/js/angular4/app/domain/domain-edit.component.ts",
						mapOfValues),
				new NullProgressMonitor());

		// list components
		CommonUtils.addFileToProject(
				projectContainer.getFolder(new Path("src/ui/src/app/" + domainName + "/" + domainName + "-list")),
				new Path(domainName + "-list.component.css"),
				TemplateMerger.merge("/vasbootbuilder/resources/web/js/angular4/app/domain/domain-list.component.css",
						mapOfValues),
				new NullProgressMonitor());
		CommonUtils.addFileToProject(
				projectContainer.getFolder(new Path("src/ui/src/app/" + domainName + "/" + domainName + "-list")),
				new Path(domainName + "-list.component.html"),
				TemplateMerger.merge("/vasbootbuilder/resources/web/js/angular4/app/domain/domain-list.component.html",
						mapOfValues),
				new NullProgressMonitor());
		CommonUtils.addFileToProject(
				projectContainer.getFolder(new Path("src/ui/src/app/" + domainName + "/" + domainName + "-list")),
				new Path(domainName + "-list.component.spec.ts"),
				TemplateMerger.merge(
						"/vasbootbuilder/resources/web/js/angular4/app/domain/domain-list.component.spec.ts",
						mapOfValues),
				new NullProgressMonitor());
		CommonUtils.addFileToProject(
				projectContainer.getFolder(new Path("src/ui/src/app/" + domainName + "/" + domainName + "-list")),
				new Path(domainName + "-list.component.ts"),
				TemplateMerger.merge("/vasbootbuilder/resources/web/js/angular4/app/domain/domain-list.component.ts",
						mapOfValues),
				new NullProgressMonitor());
		addNewRoutesToAngular4Router(projectContainer, domainClassName);

	}

	private void addNewRoutesToAngular4Router(IContainer projectContainer, String domainClassName) throws Exception {
		String routesToAdd = "{ path: '" + domainClassName.toLowerCase() + "/:id', component: " + domainClassName
				+ "EditComponent },\n" + "  { path: '" + domainClassName.toLowerCase() + "s', component: "
				+ domainClassName + "ListComponent},\n";
		IFolder jsFolder = projectContainer.getFolder(new Path("src/ui/src/app"));
		IFile appModuleFile = jsFolder.getFile("app.module.ts");
		File file = appModuleFile.getRawLocation().toFile();

		String fileContents = FileUtils.readFileToString(file);

		// insert to routes expression
		// String whenRegex = "path\\s*:.*}";
		String whenRegex = "\\{\\s*path\\s*:.*}";
		Pattern whenPattern = Pattern.compile(whenRegex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		StringBuffer buffer = prependToExpressionAngular4(routesToAdd, fileContents, whenPattern);

		// insert to declarations
		whenRegex = "declarations\\s*:\\s*\\[.*";
		whenPattern = Pattern.compile(whenRegex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		String declarationsToInsert = domainClassName + "ListComponent,\n" + domainClassName + "EditComponent,\n";
		buffer = appendToExpressionAngular4(declarationsToInsert, buffer.toString(), whenPattern);

		// insert to providers
		whenRegex = "providers\\s*:\\s*\\[";
		whenPattern = Pattern.compile(whenRegex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		String providerToInsert = domainClassName + "Service, ";
		buffer = appendToExpressionAngular4(providerToInsert, buffer.toString(), whenPattern);

		String finalString = "import { " + domainClassName + "ListComponent } from './" + domainClassName.toLowerCase()
				+ "/" + domainClassName.toLowerCase() + "-list/" + domainClassName.toLowerCase() + "-list.component';\n"
				+ "import { " + domainClassName + "EditComponent } from './" + domainClassName.toLowerCase() + "/"
				+ domainClassName.toLowerCase() + "-edit/" + domainClassName.toLowerCase() + "-edit.component';\n"
				+ "import { " + domainClassName + "Service } from './" + domainClassName.toLowerCase() + "/"
				+ domainClassName.toLowerCase() + ".service';\n" + buffer.toString();
		System.out.println("+++++++++++++++++++++++++++++++++");
		System.out.println(finalString);

		InputStream modifiedFileContent = new ByteArrayInputStream(CommonUtils.prettifyJS(finalString).getBytes());
		appModuleFile.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
		appModuleFile.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());
	}

	private StringBuffer appendToExpressionAngular4(String stringToInsert, String fileContents, Pattern whenPattern) {
		int positionToInsert = -1;
		Matcher matcher = whenPattern.matcher(fileContents);

		while (matcher.find()) {
			String group = matcher.group(0);
			System.out.println(group);
			positionToInsert = matcher.end();
		}

		StringBuffer buffer = new StringBuffer(fileContents);
		if (positionToInsert > -1) {
			buffer = new StringBuffer(fileContents);
			buffer.insert(positionToInsert, stringToInsert);
		}
		return buffer;
	}

	private StringBuffer prependToExpressionAngular4(String stringToInsert, String fileContents, Pattern whenPattern) {
		int positionToInsert = -1;
		Matcher matcher = whenPattern.matcher(fileContents);

		if (matcher.find()) {
			String group = matcher.group(0);
			System.out.println(group);
			// positionToInsert = matcher.end();
			positionToInsert = matcher.start();
		}

		StringBuffer buffer = new StringBuffer(fileContents);
		if (positionToInsert > -1) {
			buffer = new StringBuffer(fileContents);
			buffer.insert(positionToInsert, stringToInsert);
		}
		return buffer;
	}

	private void addNewTabsToAngular4AppComponentPage(IContainer projectContainer, String domainClassName)
			throws Exception {
		IFolder indexFolder = projectContainer.getFolder(new Path("src/ui/src/app"));
		IFile appComponentHTMLFile = indexFolder.getFile("app.component.html");
		File file = appComponentHTMLFile.getRawLocation().toFile();
		String modifiedFile = FileUtils.readFileToString(file);

		// String fileContents = FileUtils.readFileToString(new
		// File("/home/oleng/MyStuff/tmp1/app.component.html"), "UTF8");
		// String whenRegex = "import\\s*\\{.*;";
		String whenRegex = "<li\\s*routerLink\\S*>.*</li>";
		Pattern whenPattern = Pattern.compile(whenRegex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

		int positionToInsert = -1;
		Matcher matcher = whenPattern.matcher(modifiedFile);

		while (matcher.find()) {
			String group = matcher.group(0);
			System.out.println(group);
			positionToInsert = matcher.end();
		}

		StringBuffer buffer = new StringBuffer(modifiedFile);
		if (positionToInsert > -1) {
			buffer = new StringBuffer(modifiedFile);
			// <li><router-link to='/accounts'>Accounts List</router-link></li>
			String stringToInsert = "          <li routerLinkActive=\"active\"><a routerLink=\"/"
					+ domainClassName.toLowerCase() + "s\">" + domainClassName + " List</a></li>";
			buffer.insert(positionToInsert, "\n" + stringToInsert);
		}

		modifiedFile = buffer.toString();
		InputStream modifiedFileContent = new ByteArrayInputStream(modifiedFile.getBytes());
		appComponentHTMLFile.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
		appComponentHTMLFile.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());
	}

	private void addNewTabsToVueAppPage(IContainer projectContainer, String domainClassName) throws Exception {
		IFolder indexFolder = projectContainer.getFolder(new Path("src/ui/src"));
		IFile indexJSPFile = indexFolder.getFile("App.vue");
		File file = indexJSPFile.getRawLocation().toFile();

		String modifiedFile = FileUtils.readFileToString(file);
		// String whenRegex = "\\<ul(.*?)class(.*?)\\>";
		String whenRegex = "\\<li\\>.*\\<router-link.*\\>[\\w\\s]*\\</router-link\\>\\</li\\>";
		Pattern whenPattern = Pattern.compile(whenRegex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

		Matcher matcher = whenPattern.matcher(modifiedFile);
		int positionToInsert = -1;
		if (matcher.find()) {
			System.out.println("===========>" + matcher.group());
			System.out.println("********************************************");
			positionToInsert = matcher.end();
		}

		StringBuffer buffer = new StringBuffer(modifiedFile);
		if (positionToInsert > -1) {
			buffer = new StringBuffer(modifiedFile);
			// <li><router-link to='/accounts'>Accounts List</router-link></li>
			buffer.insert(positionToInsert, "\n<li><router-link to='/" + domainClassName.toLowerCase() + "s'>"
					+ domainClassName + " List</router-link></li>");
		}

		modifiedFile = buffer.toString();

		InputStream modifiedFileContent = new ByteArrayInputStream(modifiedFile.getBytes());
		indexJSPFile.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
		indexJSPFile.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());
	}

	private void insertStatementToHomeVue(IContainer projectContainer, String domainClassName, String regex,
			String stringToInsert) throws Exception {
		IFolder indexFolder = projectContainer.getFolder(new Path("src/ui/src/components"));
		IFile indexJSPFile = indexFolder.getFile("Home.vue");
		File file = indexJSPFile.getRawLocation().toFile();

		String modifiedFile = FileUtils.readFileToString(file);
		Pattern whenPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

		Matcher matcher = whenPattern.matcher(modifiedFile);
		int positionToInsert = -1;
		if (matcher.find()) {
			System.out.println("===========>" + matcher.group());
			System.out.println("********************************************");
			positionToInsert = matcher.end();
		}

		StringBuffer buffer = new StringBuffer(modifiedFile);
		if (positionToInsert > -1) {
			buffer = new StringBuffer(modifiedFile);
			buffer.insert(positionToInsert, stringToInsert);
		}

		modifiedFile = buffer.toString();

		InputStream modifiedFileContent = new ByteArrayInputStream(modifiedFile.getBytes());
		indexJSPFile.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
		indexJSPFile.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());
	}

	private void createVueTemplates(IContainer projectContainer, String projectName) throws Exception {
		IFolder vueTemplatesFolder = projectContainer.getFolder(new Path("src/ui/src/components"));
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());
		mapOfValues.put("fieldTypes", pageThree.getFieldTypes());

		CommonUtils.addFileToProject(vueTemplatesFolder, new Path(domainClassName + ".vue"),
				TemplateMerger.merge("/vasbootbuilder/resources/web/js/vue/DomainEditor-template.vue", mapOfValues),
				new NullProgressMonitor());
		CommonUtils.addFileToProject(vueTemplatesFolder, new Path(domainClassName + "s.vue"),
				TemplateMerger.merge("/vasbootbuilder/resources/web/js/vue/DomainList-template.vue", mapOfValues),
				new NullProgressMonitor());
	}

	private void addNewRoutesToMainJS(IContainer projectContainer, String domainClassName) throws Exception {
		String routesToAdd = "  {path : '/" + domainClassName.toLowerCase() + "/:id', component: " + domainClassName
				+ "},\n" + "  {path : '/" + domainClassName.toLowerCase() + "s', component: " + domainClassName + "s}";
		IFolder jsFolder = projectContainer.getFolder(new Path("src/ui/src"));
		IFile routerFile = jsFolder.getFile("main.js");
		File file = routerFile.getRawLocation().toFile();

		String routerString = FileUtils.readFileToString(file);

		String whenRegex = "routes.*\\[(\\r|\\n|.)*]";
		Pattern whenPattern = Pattern.compile(whenRegex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

		StringBuffer buffer = new StringBuffer();

		Matcher matcher = whenPattern.matcher(routerString);
		if (matcher.find()) {
			String matchedString = matcher.group().trim();
			// System.out.println(matchedString);
			int insertPosition = matchedString.lastIndexOf(']');
			buffer.append(matchedString);
			buffer.insert(insertPosition - 1, ",\n" + routesToAdd);
			System.out.println(buffer.toString());
		}

		String finalString = "import " + domainClassName + " from './components/" + domainClassName + ".vue'\n"
				+ "import " + domainClassName + "s from './components/" + domainClassName + "s.vue'\n"
				+ routerString.replaceFirst(whenRegex, buffer.toString());
		System.out.println("+++++++++++++++++++++++++++++++++");
		System.out.println(finalString);

		InputStream modifiedFileContent = new ByteArrayInputStream(CommonUtils.prettifyJS(finalString).getBytes());
		routerFile.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
		routerFile.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());
	}

	private String createWhenExpressions(String projectName, String domainClassName) {
		String basePath = domainClassName.toLowerCase();
		String whenExpressions = ".when('/" + basePath + "s', {controller :  '" + domainClassName
				+ "ListController', templateUrl : '/" + projectName + "/resources/js/angular_templates/"
				+ domainClassName + "List.html'})\n" + ".when('/" + basePath + "/:id', {controller :  '"
				+ domainClassName + "EditController', templateUrl : '/" + projectName
				+ "/resources/js/angular_templates/" + domainClassName + "Edit.html'})\n";

		return whenExpressions;
	}

	private String createNewScriptsTag(String projectName, String domainClassName) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("<script src=\"/" + projectName + "/resources/js/angular_controllers/" + domainClassName
				+ "ListController.js\"></script>");
		stringBuffer.append("\n<script src=\"/" + projectName + "/resources/js/angular_controllers/" + domainClassName
				+ "EditController.js\"></script>");
		stringBuffer.append("\n<script src=\"/" + projectName + "/resources/js/angular_services/" + domainClassName
				+ "Service.js\"></script>");
		return stringBuffer.toString();
	}

	private void createJavaDomainClass(IContainer projectContainer, String basePackageName, String className)
			throws Exception {
		IFolder javaFolder = projectContainer.getFolder(new Path("src/main/java"));
		String domainPackageName = basePackageName + ".web.domain";
		boolean xssSelected = false;
		boolean csrfSelected = false;
		if (generateSecurityCode) {
			xssSelected = pageFour.getXssCheckbox().getSelection();
			csrfSelected = pageFour.getCsrfCheckbox().getSelection();
		}
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", pageThree.getDomainClassName());
		mapOfValues.put("domainPackageName", domainPackageName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("basePackageName", basePackageName);
		mapOfValues.put("secured", xssSelected || csrfSelected);
		mapOfValues.put("useMongo", vasBootBuilderProperties.getProperty("useMongo"));
		CommonUtils.createPackageAndClass(javaFolder, domainPackageName, className,
				pageThree.getClassSource(mapOfValues), new NullProgressMonitor());
	}

	private void createSampleData(IContainer projectContainer, String domainClassName,
			Map<String, Object> modelAttributes) throws Exception {
		/* Add Test Data in an external text file */
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		StringWriter sampleDataStringWriter = new StringWriter();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", modelAttributes);
		IFolder sampleDataFolder = projectContainer.getFolder(new Path("src/main/resources"));
		IOUtils.copy(TemplateMerger.merge("/vasbootbuilder/resources/other/sampledata.txt-template", mapOfValues),
				sampleDataStringWriter);
		CommonUtils.createPackageAndClass(sampleDataFolder, "sampledata",
				mapOfValues.get("domainClassName").toString() + "s.txt",
				CommonUtils.cleanSampleData(sampleDataStringWriter.toString()), new NullProgressMonitor());
	}

	private void createSampleDataForHSQL(IContainer projectContainer, String domainClassName,
			Map<String, Object> modelAttributes) throws Exception {
		IFolder hsqlDDLAndDMLFolder = projectContainer.getFolder(new Path("src/main/resources"));	
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", modelAttributes);
		
		StringWriter sampleHSQLDataStringWriter = new StringWriter();
		sampleHSQLDataStringWriter.append("\n\n");
		IOUtils.copy(TemplateMerger.merge("/vasbootbuilder/resources/other/data-template.sql", mapOfValues), 
				sampleHSQLDataStringWriter);
		CommonUtils.createPackageAndClass(hsqlDDLAndDMLFolder, "", "data.sql", CommonUtils.cleanSampleData(sampleHSQLDataStringWriter.toString()), 
					new NullProgressMonitor());
		
		StringWriter sampleHSQLSchemaStringWriter = new StringWriter();
		sampleHSQLSchemaStringWriter.append("\n\n");
		IOUtils.copy(TemplateMerger.merge("/vasbootbuilder/resources/other/schema-template.sql", mapOfValues), sampleHSQLSchemaStringWriter);
		CommonUtils.createPackageAndClass(hsqlDDLAndDMLFolder, "", "schema.sql", 
				CommonUtils.cleanSampleData(sampleHSQLSchemaStringWriter.toString()), new NullProgressMonitor());
	}
	
	private void createSampleDataForMongo(IContainer projectContainer, String domainClassName,
			Map<String, Object> modelAttributes) throws Exception {
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", modelAttributes);
		StringWriter sampleMongoDataStringWriter = new StringWriter();
		IFolder sampleMongoDataFolder = projectContainer.getFolder(new Path("src/main/resources/scripts"));
		IOUtils.copy(TemplateMerger.merge("/vasbootbuilder/resources/other/mongo-script.txt-template", mapOfValues),
				sampleMongoDataStringWriter);
		CommonUtils.createPackageAndClass(sampleMongoDataFolder, "sampledata",
				mapOfValues.get("domainClassName").toString() + "s.txt",
				CommonUtils.cleanSampleData(sampleMongoDataStringWriter.toString()), new NullProgressMonitor());
	}

	private void createEditAndListTemplateFiles(IContainer projectContainer, String domainClassName,
			Map<String, Object> modelAttributes) throws Exception {
		IFolder templatesFolder = projectContainer
				.getFolder(new Path("src/main/resources/public/resources/js/templates"));
		if (!templatesFolder.exists()) {
			// try another location
			templatesFolder = projectContainer.getFolder(new Path("src/main/resources/public/resources/templates"));
		}
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("attrs", modelAttributes);
		mapOfValues.put("templateType", pageFive.isJSPTemplate() ? "JSP" : "HTML");
		mapOfValues.put("fieldTypes", pageThree.getFieldTypes());
		Path editPath;
		Path listPath;
		// Path presenterTemplatePath;
		if (pageFive.isJSPTemplate()) {
			editPath = new Path(domainClassName + "EditTemplate.jsp");
			listPath = new Path(domainClassName + "ListTemplate.jsp");
			// presenterTemplatePath = new Path(domainClassName + "PresenterTemplate.jsp");
		} else {
			editPath = new Path(domainClassName + "EditTemplate.htm");
			listPath = new Path(domainClassName + "ListTemplate.htm");
			// presenterTemplatePath = new Path(domainClassName + "PresenterTemplate.htm");
		}

		CommonUtils.addFileToProject(templatesFolder, editPath, TemplateMerger
				.merge("/vasbootbuilder/resources/web/js/backbone/templates/EditTemplate.jsp-template", mapOfValues),
				new NullProgressMonitor());

		CommonUtils.addFileToProject(templatesFolder, listPath, TemplateMerger
				.merge("/vasbootbuilder/resources/web/js/backbone/templates/ListTemplate.jsp-template", mapOfValues),
				new NullProgressMonitor());

		// CommonUtils.addFileToProject(templatesFolder, presenterTemplatePath,
		// TemplateMerger.merge("/vasbootbuilder/resources/web/js/backbone/templates/PresenterTemplate.jsp-template",
		// mapOfValues), new NullProgressMonitor());

	}

	private void createControllerClass(IContainer projectContainer, String basePackageName) throws Exception {
		/* Add a Controller */

		final String controllerClassName = pageThree.getDomainClassName() + "Controller";
		String controllerPackageName = basePackageName + ".controller";
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", pageThree.getDomainClassName());
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("basePackageName", basePackageName);

		final String controllerSourceCode = pageThree.buildSourceCode(mapOfValues, "controller.java-template");
		IFolder javaFolder = projectContainer.getFolder(new Path("src/main/java"));
		CommonUtils.createPackageAndClass(javaFolder, controllerPackageName, controllerClassName, controllerSourceCode,
				new NullProgressMonitor());

	}

	private void createServiceClass(IContainer projectContainer, String basePackageName) throws Exception {
		final String serviceClassName = pageThree.getDomainClassName() + "Service";
		String servicePackageName = basePackageName + ".service";
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", pageThree.getDomainClassName());
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("basePackageName", basePackageName);
		final String serviceSourceCode = pageThree.buildSourceCode(mapOfValues, "service.java-template");
		IFolder javaFolder = projectContainer.getFolder(new Path("src/main/java"));
		CommonUtils.createPackageAndClass(javaFolder, servicePackageName, serviceClassName, serviceSourceCode,
				new NullProgressMonitor());
	}

	private void createDaoClass(IContainer projectContainer, String basePackageName,
			Map<String, Object> modelAttributes) throws Exception {
		final String daoClassName = pageThree.getDomainClassName() + "DAO";
		String daoPackageName = basePackageName + ".dao";
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", pageThree.getDomainClassName());
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		// domainPackageName
		mapOfValues.put("basePackageName", basePackageName);
		String domainPackageName = basePackageName + ".web.domain";
		mapOfValues.put("domainPackageName", domainPackageName);
		mapOfValues.put("attrs", modelAttributes);
		mapOfValues.put("useMongo", vasBootBuilderProperties.getProperty("useMongo"));
		mapOfValues.put("prepForOracle", vasBootBuilderProperties.getProperty("prepForOracle"));
		mapOfValues.put("oracleNames", pageThree.getOracleDerivedNamesForTableAndAttrs("true".equals(vasBootBuilderProperties.getProperty("prepForOracle"))));
		mapOfValues.put("prepForHSQL", vasBootBuilderProperties.getProperty("prepForHSQL"));
		
		final String daoSourceCode = pageThree.buildSourceCode(mapOfValues, "dao.java-template");

		IFolder javaFolder = projectContainer.getFolder(new Path("src/main/java"));
		CommonUtils.createPackageAndClass(javaFolder, daoPackageName, daoClassName, daoSourceCode,
				new NullProgressMonitor());

		// create mapper files
		if ("true".equals(vasBootBuilderProperties.getProperty("prepForOracle")) || 
				"true".equals(vasBootBuilderProperties.getProperty("prepForHSQL"))) {
			final String mapperSourceCode = pageThree.buildSourceCode(mapOfValues, "mapper.java-template");
			CommonUtils.createPackageAndClass(javaFolder, daoPackageName + ".mapper",
					pageThree.getDomainClassName() + "Mapper", mapperSourceCode, new NullProgressMonitor());

			IFolder resourceFolder = projectContainer.getFolder(new Path("src/main/resources"));
			CommonUtils.createPackageAndClass(resourceFolder, daoPackageName + ".mapper",
					pageThree.getDomainClassName() + "Mapper.xml",
					IOUtils.toString(
							TemplateMerger.merge("/vasbootbuilder/resources/java/mapper-template.xml", mapOfValues)),
					new NullProgressMonitor());

		}
	}

	private void createBackboneModel(IContainer projectContainer, String projectName) throws Exception {
		IFolder modelsFolder = projectContainer.getFolder(new Path("src/main/resources/public/resources/js/models"));
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());

		CommonUtils.addFileToProject(modelsFolder, new Path(domainClassName + "Model.js"),
				TemplateMerger.merge("/vasbootbuilder/resources/web/js/backbone/models/model-template.js", mapOfValues),
				new NullProgressMonitor());
	}

	private void createBackboneCollection(IContainer projectContainer, String projectName) throws Exception {
		IFolder collectionsFolder = projectContainer
				.getFolder(new Path("src/main/resources/public/resources/js/collections"));
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());
		CommonUtils.addFileToProject(collectionsFolder, new Path(domainClassName + "Collection.js"), TemplateMerger
				.merge("/vasbootbuilder/resources/web/js/backbone/collections/collection-template.js", mapOfValues),
				new NullProgressMonitor());
	}

	// LOOK HERE
	private void createBackboneEditView(IContainer projectContainer, String projectName) throws Exception {
		IFolder viewsFolder = projectContainer.getFolder(new Path("src/main/resources/public/resources/js/views"));
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());
		mapOfValues.put("templateType", pageFive.isJSPTemplate() ? "JSP" : "HTML");
		mapOfValues.put("fieldTypes", pageThree.getFieldTypes());
		CommonUtils.addFileToProject(viewsFolder, new Path(domainClassName + "EditView.js"),
				TemplateMerger.merge("/vasbootbuilder/resources/web/js/backbone/views/view-template.js", mapOfValues),
				new NullProgressMonitor());
	}

	private void createCollectionView(IContainer projectContainer, String projectName) throws Exception {
		IFolder viewsFolder = projectContainer.getFolder(new Path("src/main/resources/public/resources/js/views"));
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());
		mapOfValues.put("templateType", pageFive.isJSPTemplate() ? "JSP" : "HTML");
		CommonUtils.addFileToProject(viewsFolder, new Path(domainClassName + "CollectionView.js"), TemplateMerger
				.merge("/vasbootbuilder/resources/web/js/backbone/views/collection-view-template.js", mapOfValues),
				new NullProgressMonitor());
	}

	private void createPresenter(IContainer projectContainer, String projectName) throws Exception {
		IFolder presenterFolder = projectContainer
				.getFolder(new Path("src/main/resources/public/resources/js/presenters"));
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());
		mapOfValues.put("templateType", pageFive.isJSPTemplate() ? "JSP" : "HTML");

		CommonUtils.addFileToProject(
				presenterFolder, new Path(domainClassName + "Presenter.js"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/backbone/views/presenter-template.js", mapOfValues),
				new NullProgressMonitor());

	}

	private void addNewTabsToHomePage(IContainer projectContainer, String className) throws Exception {
		IFolder indexFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF"));
		IFile indexJSPFile = indexFolder.getFile("index.jsp");
		File file = indexJSPFile.getRawLocation().toFile();
		String regex = "<!-- MARKER FOR INSERTING -->";
		String modifiedFile = FileUtils.readFileToString(file);
		modifiedFile = modifier(modifiedFile, regex,
				"<li><a href=\"#" + className.toLowerCase() + "s" + "\" >" + className + "s" + "</a></li>\n", "");

		InputStream modifiedFileContent = new ByteArrayInputStream(modifiedFile.getBytes());
		// indexJSPFile.delete(true, new NullProgressMonitor());
		// indexJSPFile.create(modifiedFileContent, IResource.FORCE, new
		// NullProgressMonitor());
		indexJSPFile.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
		indexJSPFile.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());
	}

	private void addNewTabsToAngularHomePage(IContainer projectContainer, String className) throws Exception {
		IFolder indexFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF"));
		IFile indexJSPFile = indexFolder.getFile("index.jsp");
		File file = indexJSPFile.getRawLocation().toFile();

		String modifiedFile = FileUtils.readFileToString(file);
		// modifiedFile = modifier(modifiedFile, regex,
		// "<li><a href=\"#" + className.toLowerCase() + "s" + "\" >" + className + "s"
		// + "</a></li>\n", "");
		String whenRegex = "\\<ul(.*?)class(.*?)\\>";
		Pattern whenPattern = Pattern.compile(whenRegex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

		Matcher matcher = whenPattern.matcher(modifiedFile);
		int positionToInsert = -1;
		if (matcher.find()) {
			System.out.println("===========>" + matcher.group());
			positionToInsert = matcher.end();
		}

		// whenWriter.append("\n" + newModelTag);
		// htmlString = matcher.replaceAll("INSERTSCRIPTSHERE");
		StringBuffer buffer = new StringBuffer(modifiedFile);
		if (positionToInsert > -1) {
			buffer = new StringBuffer(modifiedFile);
			buffer.insert(positionToInsert, "<li ng-class=\"{ active: isActive('/" + className.toLowerCase()
					+ "s')}\"><a href=\"#" + className.toLowerCase() + "s\">" + className + "</a></li>");
		}

		modifiedFile = buffer.toString();

		InputStream modifiedFileContent = new ByteArrayInputStream(modifiedFile.getBytes());
		// indexJSPFile.delete(true, new NullProgressMonitor());
		// indexJSPFile.create(modifiedFileContent, IResource.FORCE, new
		// NullProgressMonitor());
		indexJSPFile.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
		indexJSPFile.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());
	}

	private void addNewRoutesToRouter(IContainer projectContainer, String projectName) throws Exception {
		IFolder jsFolder = projectContainer.getFolder(new Path("src/main/resources/public/resources/js"));
		IFile routerFile = jsFolder.getFile("router.js");
		// int lineToInsertTo = getLineToInsertNewRoutesTo(routerFile);
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
		String defineStringToInsert = ",'models/" + backboneModelName + "'" + ",'views/" + backboneModelViewName + "'"
				+ ",'collections/" + backboneCollectionName + "'" + ",'views/" + backboneCollectionViewName + "'\n";

		// insert params into the 'define'
		/*
		 * String defineModifierRegex = "define\\s*\\(\\[[\\d\\w\\s\\'\\,\\/]*\\]";
		 * String modifiedFile = modifier(file, defineModifierRegex,
		 * defineStringToInsert, "]");
		 * 
		 * //insert corresponding params into the function String
		 * functionParamStringToInsert = ", " + backboneModelName + ", " +
		 * backboneModelViewName + ", " + backboneCollectionName + ", " +
		 * backboneCollectionViewName;
		 * 
		 * String functionModifierRegex = "function\\s*\\([\\d\\w\\s\\$\\,]*\\)";
		 * modifiedFile = modifier(modifiedFile, functionModifierRegex,
		 * functionParamStringToInsert, ")");
		 */

		String modifiedFile = FileUtils.readFileToString(file);
		String routeDefinitionStringToInsert = "\n," + "\"" + domainClassName.toLowerCase() + "/:id\" : " + "\"get"
				+ domainClassName + "\",\n" + "\"" + domainClassName.toLowerCase() + "s\" : " + "\"get"
				+ domainClassName + "List\",\n" + "\"" + domainClassName.toLowerCase() + "Presenter\" : " + "\"show"
				+ domainClassName + "Presenter\"\n";
		String routeDefinitionRegex = "routes\\s*:\\s*\\{[\\*\\d\\w\\s\\\"\\'\\/:,]*\\}";
		modifiedFile = modifier(modifiedFile, routeDefinitionRegex, routeDefinitionStringToInsert, "}");

		InputStream inputStream = TemplateMerger
				.merge("/vasbootbuilder/resources/web/js/backbone/routers/router-template-fragment-03.js", mapOfValues);

		StringWriter mergeOutput = new StringWriter();
		IOUtils.copy(inputStream, mergeOutput);

		// String routeActionStringToInsert = "";
		String routeActionRegex = "[\\s\\d\\w\\'\\/]*[\\n]\\s*Backbone.history.start\\(\\);";
		modifiedFile = modifier(modifiedFile, routeActionRegex, "\n" + mergeOutput.toString(), "");

		InputStream modifiedFileContent = new ByteArrayInputStream(modifiedFile.getBytes());
		// routerFile.delete(true, new NullProgressMonitor());
		// routerFile.create(modifiedFileContent, IResource.FORCE, new
		// NullProgressMonitor());
		routerFile.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
		routerFile.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());

	}

	public String modifier(String fileContents, String expression, String stringToInsert, String stringToBefore) {
		String newFileContents = "";

		// String contents = FileUtils.readFileToString(new File(fileName));

		StringBuffer buffer = null;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(fileContents);
		if (matcher.find()) {
			String origString = matcher.group();
			buffer = new StringBuffer(origString);
			if (!stringToBefore.equals(""))
				buffer.insert(origString.lastIndexOf(stringToBefore), stringToInsert);
			else
				buffer.insert(0, stringToInsert);
		}
		// newFileContents = fileContents.replaceAll(expression,
		// buffer.toString().replace("$", "\\$"));
		newFileContents = fileContents.replaceFirst(expression, buffer.toString().replace("$", "\\$"));

		return newFileContents;
	}

	public String modifier(File file, String expression, String stringToInsert, String stringToBefore)
			throws Exception {
		String contents = FileUtils.readFileToString(file);
		return modifier(contents, expression, stringToInsert, stringToBefore);
	}

	private void createAngularControllers(IContainer projectContainer, String projectName) throws Exception {
		IFolder angularControllerFolder = projectContainer
				.getFolder(new Path("src/main/resources/public/resources/js/angular_controllers"));
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());

		CommonUtils.addFileToProject(angularControllerFolder, new Path(domainClassName + "ListController.js"),
				TemplateMerger.merge("/vasbootbuilder/resources/web/js/angular/angular_list_controller-template.js",
						mapOfValues),
				new NullProgressMonitor());
		CommonUtils.addFileToProject(angularControllerFolder, new Path(domainClassName + "EditController.js"),
				TemplateMerger.merge("/vasbootbuilder/resources/web/js/angular/angular_edit_controller-template.js",
						mapOfValues),
				new NullProgressMonitor());
	}

	private void createAngularService(IContainer projectContainer, String projectName) throws Exception {
		IFolder angularServiceFolder = projectContainer
				.getFolder(new Path("src/main/resources/public/resources/js/angular_services"));
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());

		CommonUtils.addFileToProject(
				angularServiceFolder, new Path(domainClassName + "Service.js"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/angular/angular_service-template.js", mapOfValues),
				new NullProgressMonitor());
	}

	private void createAngularTemplates(IContainer projectContainer, String projectName) throws Exception {
		IFolder angularTemplatesFolder = projectContainer
				.getFolder(new Path("src/main/resources/public/resources/js/angular_templates"));
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());

		CommonUtils.addFileToProject(
				angularTemplatesFolder, new Path(domainClassName + "List.html"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/angular/angular_list_html-template.html", mapOfValues),
				new NullProgressMonitor());
		CommonUtils.addFileToProject(
				angularTemplatesFolder, new Path(domainClassName + "Edit.html"), TemplateMerger
						.merge("/vasbootbuilder/resources/web/js/angular/angular_edit_html-template.html", mapOfValues),
				new NullProgressMonitor());
	}

	public void appendNewRouteExpressionToAngular(IContainer projectContainer, String newWhenExpression)
			throws Exception {
		IFolder jsFolder = projectContainer.getFolder(new Path("src/main/resources/public/resources/js"));
		IFile routerFile = jsFolder.getFile("angular_app.js");
		File file = routerFile.getRawLocation().toFile();

		String routerString = FileUtils.readFileToString(file);

		String whenRegex = "\\.when\\((.*?)\\)(\\;)*";
		Pattern whenPattern = Pattern.compile(whenRegex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
		// String value = routePattern.matcher(routerString).
		// System.out.println(value);
		Matcher matcher = whenPattern.matcher(routerString);
		StringWriter whenWriter = new StringWriter();
		// 1. gather all 'when' expressions

		while (matcher.find()) {
			String matchedString = matcher.group().trim();
			if (matchedString.charAt(matchedString.length() - 1) == ';') {
				matchedString = matchedString.substring(0, matchedString.length() - 1);
			}
			System.out.println("===========>" + matchedString);
			whenWriter.append("\n" + matchedString);
		}
		routerString = matcher.replaceAll("INSERTHEREPLEASE");

		// 2. obtain the 'otherwise' expression (IF IT EXISTS)
		String otherwiseRegex = "\\.otherwise\\((.*?)\\)(\\;)*";
		Pattern otherwisePattern = Pattern.compile(otherwiseRegex,
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
		Matcher otherwiseMatcher = otherwisePattern.matcher(routerString);
		StringWriter otherwiseWriter = new StringWriter();
		while (otherwiseMatcher.find()) {
			System.out.println("OTHERWISE EXPR===========>" + otherwiseMatcher.group());
			otherwiseWriter.append(otherwiseMatcher.group());
		}
		routerString = otherwiseMatcher.replaceAll("INSERTHEREPLEASE");

		// append the new route to the list of 'WHEN' EXPRESSIONS
		whenWriter.append("\n" + newWhenExpression);

		System.out.println("=================================================================================");
		System.out.println(whenWriter.toString() + " " + otherwiseWriter.toString());

		System.out.println("=================================================================================");
		int insertionPoint = routerString.indexOf("INSERTHEREPLEASE");
		StringBuffer stringBuffer = new StringBuffer(routerString);
		stringBuffer.insert(insertionPoint, whenWriter.toString() + " " + otherwiseWriter.toString());

		InputStream modifiedFileContent = new ByteArrayInputStream(
				CommonUtils.prettifyJS(stringBuffer.toString().replaceAll("INSERTHEREPLEASE", "")).getBytes());
		// routerFile.delete(true, new NullProgressMonitor());
		// routerFile.create(modifiedFileContent, IResource.FORCE, new
		// NullProgressMonitor());
		routerFile.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
		routerFile.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());

	}

	private void appendNewScriptsToAngular(IContainer projectContainer, String newScriptTag) throws Exception {
		String whenRegex = "\\<script(.*?)\\>(.*?)\\<\\/script\\>";
		Pattern whenPattern = Pattern.compile(whenRegex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
		// String value = routePattern.matcher(routerString).
		// System.out.println(value);
		IFolder jsFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF"));
		IFile index2File = jsFolder.getFile("index.jsp");
		File file = index2File.getRawLocation().toFile();

		String htmlString = FileUtils.readFileToString(file);

		Matcher matcher = whenPattern.matcher(htmlString);
		StringWriter whenWriter = new StringWriter();
		// 1. gather all 'when' expressions
		while (matcher.find()) {
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
		InputStream modifiedFileContent = new ByteArrayInputStream(finalString.getBytes());

		// index2File.delete(true, new NullProgressMonitor());
		// index2File.create(modifiedFileContent, IResource.FORCE, new
		// NullProgressMonitor());

		index2File.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
		index2File.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());
	}

	private String[] getLineToInsertNewRoutesTo(IFile routerFile) throws Exception {
		List<String> lines = new ArrayList<String>();
		InputStream inputStream = routerFile.getContents();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		String line = "";
		int lineNumber = 0;
		int blankLineNumber = 0;
		boolean blankLineFound = false;
		while ((line = bufferedReader.readLine()) != null) {
			lines.add(line + "\n");
			if (line.trim().equals("") && !blankLineFound)
				blankLineNumber = lineNumber;
			if (line.indexOf("Backbone.history.start()") > -1) {
				blankLineFound = true;
			}
			lineNumber++;
		}

		lines.add(blankLineNumber, "//HEY FOUND IT\n");
		lines.add(blankLineNumber, "\n");
		bufferedReader.close();
		// System.out.println("The last blank line before start is: " +
		// blankLineNumber);
		String[] linesToReturn = new String[lines.size()];
		lines.toArray(linesToReturn);
		return linesToReturn;
	}

}