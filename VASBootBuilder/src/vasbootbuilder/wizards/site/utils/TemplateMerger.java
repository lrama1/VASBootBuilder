package vasbootbuilder.wizards.site.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.event.EventCartridge;

import vasbootbuilder.utils.VASBootBuilderVelocityEvent;


public class TemplateMerger {
	private static String USER_HOME;
	static {
		USER_HOME = System.getProperty("user.home");
	}

	public static InputStream merge(String templateName, String projectName,
			String basePackageName, String controllerPackageName, String utilPackageName)  throws Exception{
		Thread thread = Thread.currentThread();
		ClassLoader loader = thread.getContextClassLoader();
		thread.setContextClassLoader(TemplateMerger.class.getClassLoader());
		try {
			Template template = loadTemplate(templateName);
			VelocityContext context = new VelocityContext();
	
			//add custom events handlers
			EventCartridge ec = new EventCartridge();
			ec.addEventHandler(new VASBootBuilderVelocityEvent());
			ec.attachToContext(context);
			
			context.put("projectName", projectName);
			context.put("basePackageName", basePackageName);
			context.put("controllerPackageName", controllerPackageName);
			context.put("utilPackageName", utilPackageName);
	
			StringWriter sw = new StringWriter();
			template.merge(context, sw);
			return new ByteArrayInputStream(sw.toString().getBytes());
		}finally{
			thread.setContextClassLoader(loader);
		}
	}

//	public static InputStream merge(String templateName,
//			String domainPackageName, String className,
//			String domainClassIdAttributeName, Map<String, Object> attrs)  throws Exception{
//
//		Template template = loadTemplate(templateName);
//		VelocityContext context = new VelocityContext();
//		context.put("domainPackageName", domainPackageName);
//		context.put("className", className);
//		context.put("domainClassIdAttributeName", domainClassIdAttributeName);
//		context.put("attrs", attrs);
//
//		StringWriter sw = new StringWriter();
//		template.merge(context, sw);
//		return new ByteArrayInputStream(sw.toString().getBytes());
//	}

	public static InputStream merge(String templateName,
			Map<String, Object> valuesToPlug)  throws Exception{
		Thread thread = Thread.currentThread();
		ClassLoader loader = thread.getContextClassLoader();
		thread.setContextClassLoader(TemplateMerger.class.getClassLoader());
		try {
		Template template = loadTemplate(templateName);

		VelocityContext context = new VelocityContext();
		for (String key : valuesToPlug.keySet()) {
			context.put(key, valuesToPlug.get(key));
		}
		
		//add custom events handlers
		EventCartridge ec = new EventCartridge();
		ec.addEventHandler(new VASBootBuilderVelocityEvent());
		ec.attachToContext(context);

		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		return new ByteArrayInputStream(sw.toString().getBytes());
		}finally{
			thread.setContextClassLoader(loader);
		}
	}

	private static Template loadTemplate(String templateName) throws Exception {
		Thread thread = Thread.currentThread();
		ClassLoader loader = thread.getContextClassLoader();
		thread.setContextClassLoader(TemplateMerger.class.getClassLoader());
		try {
		Template template = null;

		checkAndCopyTemplatesToExternal(templateName);
		
		// we are actually telling Velocity to try and load from 2 locations
		Properties properties = new Properties();
		properties.setProperty("resource.loader", "file,class");
		properties.setProperty("file.resource.loader.class",
						"org.apache.velocity.runtime.resource.loader.FileResourceLoader");
		properties.setProperty("file.resource.loader.path", USER_HOME
				+ "/vasbootbuildertemplates");
		properties.setProperty("file.resource.loader.cache", "false");
		properties.setProperty(
				"file.resource.loader.modificationCheckInterval", "0");

		properties.setProperty("class.resource.loader.class",
						"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

		
		
		Velocity.init(properties);
		template = Velocity.getTemplate(templateName);

		return template;
		}finally{
			thread.setContextClassLoader(loader);
		}
	}
	
	private static void checkAndCopyTemplatesToExternal(String fileNameToCheck)
		throws Exception{
		String baseDirectory = USER_HOME + "/vasbootbuildertemplates";
		String templateFileName = baseDirectory + fileNameToCheck;
		File templateFile = new File(templateFileName);
		if(!templateFile.exists()){
			String directoryPathToCreate = templateFileName.substring(0, templateFileName.lastIndexOf("/"));
			File dirs = new File(directoryPathToCreate);
			dirs.mkdirs();
			FileUtils.copyInputStreamToFile(TemplateMerger.class.getResourceAsStream(fileNameToCheck), templateFile);
		}
	}

}
