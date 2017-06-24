package bsbuilder.wizards.site.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.wst.jsdt.core.ToolFactory;
import org.eclipse.wst.jsdt.core.formatter.CodeFormatter;
import org.eclipse.wst.jsdt.core.formatter.DefaultCodeFormatterConstants;
import org.w3c.tidy.Tidy;

public class CommonUtils {

	public static void createPackageAndClass(IFolder parentFolder, String packageName, 
			String className, String sourceCode, IProgressMonitor monitor ) throws Exception{
		String[] path = packageName.split("\\.");
		for(int i = 0; i < path.length; i++){
			IFolder javaSrc = parentFolder.getFolder(new Path(path[i]));
			if(!javaSrc.exists())
				javaSrc.create(false, true, new NullProgressMonitor());
			parentFolder = javaSrc;
		}		
		String filename = "";
		if(className.indexOf(".") == -1)
			filename = className + ".java";
		else
			filename = className;
			
		addFileToProject(parentFolder, new Path(filename),
				new ByteArrayInputStream(sourceCode.getBytes()), monitor);		
	}
	
	/**
	 * Adds a new file to the project.
	 * 
	 * @param container
	 * @param path
	 * @param contentStream
	 * @param monitor
	 * @throws CoreException
	 */
	public static void addFileToProject(IContainer container, Path path,
			InputStream contentStream, IProgressMonitor monitor)
			throws CoreException, IOException {
		final IFile file = container.getFile(path);
		if(path.toString().endsWith(".js") || path.toString().endsWith(".vue")){
			StringWriter writer = new StringWriter();
			IOUtils.copy(contentStream, writer);
			contentStream = new ByteArrayInputStream(prettifyJS(writer.toString()).getBytes());
		}else if(path.toString().endsWith(".java")){
			StringWriter writer = new StringWriter();
			IOUtils.copy(contentStream, writer);
			contentStream = new ByteArrayInputStream(prettifyJava(writer.toString()).getBytes());
		}
		
		if (file.exists()) {
			file.setContents(contentStream, true, true, monitor);
		} else {
			file.create(contentStream, true, monitor);
		}
	}
	
	public static String linesToString(List<String> lines, String lineSeparator){
		StringWriter stringWriter = new StringWriter();
		for(String line : lines){
			stringWriter.append(line + lineSeparator);
		}
		return stringWriter.toString();
	}
	
	public static String prettifyJava(String source) {
		org.eclipse.jdt.core.formatter.CodeFormatter formatter = getDefaultFormatter();
		// format the source code, at this time always use LF as line separator
		org.eclipse.jface.text.IDocument document = new org.eclipse.jface.text.Document(
				source);
		org.eclipse.text.edits.TextEdit textEdit = formatter
				.format(org.eclipse.jdt.core.formatter.CodeFormatter.K_COMPILATION_UNIT,
						source, 0, source.length(), 0, "\n"); //$NON-NLS-1$

		if (textEdit != null) {
			try {
				textEdit.apply(document);
				return document.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return source;
	}
	
	public static String prettifyJS(String js){
	    Map<?, ?> setting = DefaultCodeFormatterConstants.getEclipseDefaultSettings();

	    CodeFormatter formatter = ToolFactory.createCodeFormatter(setting);
	    TextEdit edit = formatter.format(CodeFormatter.K_JAVASCRIPT_UNIT, js,
	            0, js.length(), 0, "\n");
	    if (edit == null)
	        return js;
	    IDocument doc = new Document(js);
	    try {
	        edit.apply(doc);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return js;
	    }
	    return doc.get();
	}
	
	
	
	@SuppressWarnings("unchecked")
	private static org.eclipse.jdt.core.formatter.CodeFormatter getDefaultFormatter() {
		org.eclipse.jdt.core.formatter.CodeFormatter sourceFormatter = null; 
		
		// Take default Eclipse formatting options
		Map<Object, Object> options = org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants
				.getEclipseDefaultSettings();

		// Initialize the compiler settings
		options.put(org.eclipse.jdt.core.JavaCore.COMPILER_COMPLIANCE,
				org.eclipse.jdt.core.JavaCore.VERSION_1_5);
		options
				.put(
						org.eclipse.jdt.core.JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
						org.eclipse.jdt.core.JavaCore.VERSION_1_5);
		options.put(org.eclipse.jdt.core.JavaCore.COMPILER_SOURCE,
				org.eclipse.jdt.core.JavaCore.VERSION_1_5);

		// Instantiate the default code formatter with the given options
		sourceFormatter = org.eclipse.jdt.core.ToolFactory
				.createCodeFormatter(options);
		
		return sourceFormatter;
	}
	
	public static String cleanSampleData(String sampleData) throws IOException{
		//return StringUtils.trim(sampleData);
		//return sampleData.replaceAll("\\t", "").replaceAll(" ", "");
		BufferedReader reader = new BufferedReader(new StringReader(sampleData));
		StringWriter stringWriter = new StringWriter();
		String line = "";
		while((line = reader.readLine()) != null){
			stringWriter.append(StringUtils.trim(line) + "\n");
		}
		return stringWriter.toString();
	}

}
