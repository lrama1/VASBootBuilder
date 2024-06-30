package vasbootbuilder.wizards.backbone.actions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
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
				
				String prepForOracle = vasBootBuilderProperties.getProperty("prepForOracle");
				String prepForHSQL = vasBootBuilderProperties.getProperty("prepForHSQL");
				
				Map<String, Object> modelAttributes = pageThree.getModelAttributes();

				// create Domain Class
				createJavaDomainClass(projectContainer, basePackageName, pageThree.getDomainClassName());

				// create SampleData
				createSampleData(projectContainer, pageThree.getDomainClassName(), modelAttributes);
				
				//add data for HSQL
				if(StringUtils.equals(prepForHSQL, "true")) {
					createSampleDataForHSQL(projectContainer, pageThree.getDomainClassName(), 
					        prepForOracle,
					        prepForHSQL,
					        modelAttributes);;
				}

				// create SampleDate for Mongo
				if (StringUtils.equals(useMongo, "true")) {
					createSampleDataForMongo(projectContainer, pageThree.getDomainClassName(), modelAttributes);
				}

				// createController
				createControllerClass(projectContainer, basePackageName);
				
				// createControllerTest
				createControllerTestClass(projectContainer, basePackageName);

				// createService
				createServiceClass(projectContainer, basePackageName);

				// createDao
				createDaoClass(projectContainer, basePackageName, modelAttributes);

				if (uiType.equalsIgnoreCase("Angular4")) {
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
		mapOfValues.put("projectNameâ€�", projectName);
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
		CommonUtils.addFileToProject(componentsFolder, new Path(domainClassName + "List.test.js"),
                TemplateMerger.merge("/vasbootbuilder/resources/web/js/react/component/domain/DomainListTest-template.js",
                        mapOfValues),
                new NullProgressMonitor());
		
		CommonUtils.addFileToProject(containersFolder, new Path(domainClassName + "ListContainer.js"),
				TemplateMerger.merge("/vasbootbuilder/resources/web/js/react/component/domain/DomainListContainer-template.js",
						mapOfValues),
				new NullProgressMonitor());
		
		CommonUtils.addFileToProject(containersFolder,
                new Path(domainClassName + "ListContainer.test.js"),
                TemplateMerger.merge(
                        "/vasbootbuilder/resources/web/js/react/component/domain/DomainListContainerTest-template.js",
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
		CommonUtils.addFileToProject(containersFolder, new Path(domainClassName + "EditContainer.test.js"), TemplateMerger
                .merge("/vasbootbuilder/resources/web/js/react/component/domain/DomainDetailContainerTest-template.js", mapOfValues),
                new NullProgressMonitor());
		//mapOfValues.put("ComponentName", domainClassName + "Detail");
		
		// actions
		IFolder actionsContainerFolder = projectContainer.getFolder(new Path("src/ui/src/actions"));
		CommonUtils.addFileToProject(actionsContainerFolder, new Path(domainName + ".js"),
				TemplateMerger.merge("/vasbootbuilder/resources/web/js/react/actions/index-template.js", mapOfValues),
				new NullProgressMonitor());
		CommonUtils.addFileToProject(actionsContainerFolder, new Path(domainName + ".test.js"),
                TemplateMerger.merge("/vasbootbuilder/resources/web/js/react/actions/actionTest-template.js",  mapOfValues),
                new NullProgressMonitor());
		
		// reducers
		IFolder reducersContainerFolder = projectContainer.getFolder(new Path("src/ui/src/reducers"));
		CommonUtils.addFileToProject(reducersContainerFolder, new Path(domainName + ".js"),
				TemplateMerger.merge(
						"/vasbootbuilder/resources/web/js/react/reducers/domain-reducer-template.js",
						mapOfValues),
				new NullProgressMonitor());
		CommonUtils.addFileToProject(reducersContainerFolder, new Path(domainName + ".test.js"),
                TemplateMerger.merge(
                        "/vasbootbuilder/resources/web/js/react/reducers/domain-reducer-test-template.js",
                        mapOfValues),
                new NullProgressMonitor());
		
		addReducerToIndexReducer(projectContainer, domainClassName);
		
		addNewRoutesToReact(projectContainer, domainClassName);
		addActionsToAppContainer(projectContainer, domainClassName);
		
		modifyMockServer(projectContainer, projectName, domainClassName, mapOfValues);
		
		addMockData(projectContainer, domainClassName);
	}
	
	private void modifyMockServer(IContainer projectContainer, String projectName, String domainClassName,
	        Map<String, Object> mapOfValues) throws Exception{	    
	    IFolder mockFolder = projectContainer.getFolder(new Path("src/ui/mocks"));
	    IFile serverJSFile = mockFolder.getFile("server.js");
	    File fileToAugment = serverJSFile.getRawLocation().toFile();
	    
	    String domainObjectName = domainClassName.toLowerCase();
	    String domainClassIdAttributeName = (String)mapOfValues.get("domainClassIdAttributeName");
	    String stringToInsert = 
                "const " + domainObjectName + "s = require('./" + domainClassName + "s.json')\n" + 
                "app.get('/" + projectName + "/" + domainObjectName + "s', (req, res) =>{\n" + 
                "    return res.json(" + domainObjectName + "s)\n" + 
                "})\n" +
                "" +
                "app.get('/" + projectName + "/" + domainObjectName + "/:" + domainClassIdAttributeName + "', (req, res) =>{\n" + 
                "    const returnVal = jsonQuery('rows[" + domainClassIdAttributeName + "=' + req.params." + domainClassIdAttributeName + " + ']',{data: " + domainObjectName + "s})\n" + 
                "    return res.json(returnVal.value)\n" + 
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

		String newImport = "import { " + domainObjectName + "s, " + domainObjectName + " } " +
				"from './" + domainObjectName.toLowerCase() + "';";
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
						domainObjectName + ",\n    " + 
						domainObjectName + "s"
						+ fetchmatcher.group());
			}
			
			buffer = new StringBuffer(combineReducersMatcher.replaceAll(combineReducersExport));
		}
		
		//System.out.println(updatedDispatchConst);
		InputStream modifiedFileContent = new ByteArrayInputStream(CommonUtils.prettifyJS(buffer.toString()).getBytes());
		appModuleFile.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
		appModuleFile.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());

	}

	private void addActionsToAppContainer(IContainer projectContainer, String domainClassName) throws Exception {
	    String domainObjectName = domainClassName.substring(0,1).toLowerCase() + domainClassName.substring(1);
        IFolder jsFolder = projectContainer.getFolder(new Path("src/ui/src/containers"));
        IFile appContainerModuleFile = jsFolder.getFile("AppContainer.js");
        File file = appContainerModuleFile.getRawLocation().toFile();

        String fileContents = FileUtils.readFileToString(file);
        String importRegex = "import.*\\'";
        Pattern importPattern = Pattern.compile(importRegex, Pattern.DOTALL);
        Matcher importmatcher = importPattern.matcher(fileContents);
        
        
        String importStringToAdd = 
                "import {fetchAll" + domainClassName + "s} from '../actions/" + domainObjectName.toLowerCase() + "'; \n" ;
        if (importmatcher.find()) {
            String currentRoutes = importmatcher.group();
            fileContents = importmatcher.replaceAll(currentRoutes + "\n" + importStringToAdd);
        }else{
            importRegex = "import.*\\';";
            importPattern = Pattern.compile(importRegex, Pattern.DOTALL);
            importmatcher = importPattern.matcher(fileContents);
            
            String currentRoutes = importmatcher.group();
            fileContents = importmatcher.replaceAll(currentRoutes + "\n" + importStringToAdd);
        }
        
        //add dispatcher in mapDispatchToProps
        fileContents = addDispatcher(fileContents, domainClassName);
        
        InputStream modifiedFileContent = new ByteArrayInputStream(CommonUtils.prettifyJS(fileContents).getBytes());
        appContainerModuleFile.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
        appContainerModuleFile.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());
	}
	
	private void addNewRoutesToReact(IContainer projectContainer, String domainClassName) throws Exception {
		String domainObjectName = domainClassName.substring(0,1).toLowerCase() + domainClassName.substring(1);
		IFolder jsFolder = projectContainer.getFolder(new Path("src/ui/src"));
		IFile appModuleFile = jsFolder.getFile("App.js");
		File file = appModuleFile.getRawLocation().toFile();
		String fileContents = FileUtils.readFileToString(file);
		
		String withMenuAdded = addMenu(fileContents, domainClassName, domainObjectName);
		String withRoute = addRoute(withMenuAdded, domainClassName, domainObjectName);
		String withImports = addImportsToApp(withRoute, domainClassName, domainObjectName);
		
		InputStream modifiedFileContent = new ByteArrayInputStream(CommonUtils.prettifyJS(withImports).getBytes());
		appModuleFile.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
		appModuleFile.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());
		
	}
	
	private String addMenu(String fileContents, String domainClassName, String domainObjectName) {
		String newFileContents = "";
		String stringToAdd = 
			"\n,{ label: '" + domainClassName + "s', icon: 'pi pi-fw pi-id-card', to: '/" 
					+ domainObjectName + "s', command: () => props.fetchAll" + domainClassName+ "s()}";
		
		String menuRegex = "const\\s*menu\\s*=.*?\\];";
		Pattern menuPattern = Pattern.compile(menuRegex, Pattern.DOTALL);
		Matcher menuMatcher = menuPattern.matcher(fileContents);
		
		int indexToInsert = -1;
		int lengthOfMatched = -1;
		if(menuMatcher.find()) {
			indexToInsert = menuMatcher.start();			
			lengthOfMatched = menuMatcher.group().length();
			String menuDeclaration = menuMatcher.group();
			System.out.println(menuDeclaration);
			
			String itemsRegex = "items\\s*:.*?\\]";
			Pattern itemsPattern = Pattern.compile(itemsRegex, Pattern.DOTALL);
			Matcher itemsMatcher = itemsPattern.matcher(menuDeclaration);
			if(itemsMatcher.find()) {
				String items =itemsMatcher.group();
				int itemsIndex = itemsMatcher.start();
				String newItems = items.substring(0, items.lastIndexOf(']')) + stringToAdd + items.substring(items.lastIndexOf(']'));
                
				menuDeclaration = menuDeclaration.substring(0, itemsIndex) +
						newItems + menuDeclaration.substring(itemsIndex + itemsMatcher.group().length());
				
				newFileContents = fileContents.substring(0, indexToInsert) +
						menuDeclaration + fileContents.substring(indexToInsert + lengthOfMatched);
			}			
		}
		return newFileContents;		
	}
	
	private String addRoute(String fileContents, String domainClassName, String domainObjectName) {
		String newFileContents = "";
		String stringToAdd = 
				",\n{ path: '/" + domainObjectName+ "s', component: "+ domainClassName + "ListContainer, meta: { breadcrumb: [{ parent: 'UI Kit', label: '"+ domainClassName +"' }] } }" +
					",\n{ path: '/" + domainObjectName +"', component: " + domainClassName + "EditContainer, meta: { breadcrumb: [{ parent: 'UI Kit', label: '" + domainClassName + "' }] } }"
						;
		
		String routerRegex = "const\\s*routers\\s*=.*?\\];";
		Pattern routerPattern = Pattern.compile(routerRegex, Pattern.DOTALL);
		Matcher routerMatcher = routerPattern.matcher(fileContents);
		
		int indexToInsert = -1;
		int lengthOfMatched = -1;		
		if(routerMatcher.find()) {
			indexToInsert = routerMatcher.start();			
			lengthOfMatched = routerMatcher.group().length();
			String routerDeclaration = routerMatcher.group();
			System.out.println(routerDeclaration);
			String newRouterDeclaration =
					routerDeclaration.substring(0, routerDeclaration.lastIndexOf(']') ) + stringToAdd +
						routerDeclaration.substring(routerDeclaration.lastIndexOf(']'));	
							;

			newFileContents = fileContents.substring(0, indexToInsert) + newRouterDeclaration + 
					    fileContents.substring(indexToInsert + lengthOfMatched);
			
		}
		return newFileContents;
	}
	
	private String addImportsToApp(String fileContents, String domainClassName, String domainObjectName) {
		String importRegex = "import.*?[\"\'];";
		String newFileContents = "";
		String importStringToAdd = "\nimport " + domainClassName + "ListContainer from './containers/" + domainClassName + "ListContainer';" 
				+ "\nimport " + domainClassName + "EditContainer from './containers/" + domainClassName + "EditContainer';";
		
		Pattern importPattern = Pattern.compile(importRegex, Pattern.DOTALL);
		Matcher importmatcher = importPattern.matcher(fileContents);
		String currentImportStatement = "";
		int indexOfMatched = -1;
		int offset = -1;
		while(importmatcher.find()) {
			currentImportStatement = importmatcher.group();		
			indexOfMatched = importmatcher.start();
			offset = currentImportStatement.length();
		}
		newFileContents = fileContents.substring(0, indexOfMatched + offset) + importStringToAdd +
				fileContents.substring(indexOfMatched + offset);
		return newFileContents;		
	}
	
	/*private void addNewRoutesToReact(IContainer projectContainer, String domainClassName) throws Exception {
		String domainObjectName = domainClassName.substring(0,1).toLowerCase() + domainClassName.substring(1);
		IFolder jsFolder = projectContainer.getFolder(new Path("src/ui/src"));
		IFile appModuleFile = jsFolder.getFile("App.js");
		File file = appModuleFile.getRawLocation().toFile();

		String fileContents = FileUtils.readFileToString(file);
		String importRegex = "import.*\\';";
		Pattern importPattern = Pattern.compile(importRegex, Pattern.DOTALL);
		Matcher importmatcher = importPattern.matcher(fileContents);
		String importStringToAdd = "import " + domainClassName + "ListContainer from '../containers/" + domainClassName	+ "ListContainer';\n" 
				+ "import " + domainClassName + "EditContainer from '../containers/" + domainClassName + "EditContainer';\n";
		if (importmatcher.find()) {
			String currentRoutes = importmatcher.group();
			fileContents = importmatcher.replaceAll(currentRoutes + "\n" + importStringToAdd);
		}

		String linkRegex = "<li.*>.*<Link.*>.*</Link>.*</li>";
		Pattern linkPattern = Pattern.compile(linkRegex, Pattern.DOTALL | Pattern.MULTILINE);
		Matcher linkmatcher = linkPattern.matcher(fileContents);
		String linkStringToAdd = "<li><Link  className=\"nav-link\" to=\"/" + domainClassName.toLowerCase() + "s\" "
				+ " onClick={() => props.fetchAll" + domainClassName + "s('/" + domainObjectName.toLowerCase() + "s?page=1&per_page=10')} >" + domainClassName
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
		//fileContents = addDispatcher(fileContents, domainClassName);
		
		InputStream modifiedFileContent = new ByteArrayInputStream(CommonUtils.prettifyJS(fileContents).getBytes());
		appModuleFile.setContents(modifiedFileContent, IFile.FORCE, new NullProgressMonitor());
		appModuleFile.refreshLocal(IFile.DEPTH_ZERO, new NullProgressMonitor());
	}*/
	
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
		mapOfValues.put("prepForHSQL", vasBootBuilderProperties.getProperty("prepForHSQL"));
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
	        String prepForOracle,
	        String prepForHSQL,
			Map<String, Object> modelAttributes) throws Exception {
		IFolder hsqlDDLAndDMLFolder = projectContainer.getFolder(new Path("src/main/resources"));	
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", domainClassName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("prepForOracle", prepForOracle);
		mapOfValues.put("prepForHSQL", prepForHSQL);
		mapOfValues.put("oracleNames", 
                pageThree.getOracleDerivedNamesForTableAndAttrs("true".equals(prepForOracle)
                        || "true".equals(prepForHSQL)));
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
	
	private void createControllerTestClass(IContainer projectContainer, String basePackageName) throws Exception {
		/* Add a Controller */

		final String controllerClassName = pageThree.getDomainClassName() + "ControllerTest";
		String controllerPackageName = basePackageName + ".controller";
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("domainClassName", pageThree.getDomainClassName());
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("basePackageName", basePackageName);

		final String controllerSourceCode = pageThree.buildSourceCode(mapOfValues, "controllerTest.java-template");
		IFolder javaFolder = projectContainer.getFolder(new Path("src/test/java"));
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
		final String daoClassName = pageThree.getDomainClassName() + "Repository";
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
		mapOfValues.put("oracleNames", 
		        pageThree.getOracleDerivedNamesForTableAndAttrs("true".equals(vasBootBuilderProperties.getProperty("prepForOracle"))
		                || "true".equals(vasBootBuilderProperties.getProperty("prepForHSQL"))));
		mapOfValues.put("prepForHSQL", vasBootBuilderProperties.getProperty("prepForHSQL"));
		
		final String daoSourceCode = pageThree.buildSourceCode(mapOfValues, "jpa-repository.java-template");

		IFolder javaFolder = projectContainer.getFolder(new Path("src/main/java"));
		CommonUtils.createPackageAndClass(javaFolder, daoPackageName, daoClassName, daoSourceCode,
				new NullProgressMonitor());

		// create mapper files
		/*if ("true".equals(vasBootBuilderProperties.getProperty("prepForOracle")) || 
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

		}*/
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
	

}