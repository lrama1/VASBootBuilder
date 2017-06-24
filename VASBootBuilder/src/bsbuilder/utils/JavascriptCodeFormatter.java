package bsbuilder.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.mozilla.javascript.tools.shell.Main;

public class JavascriptCodeFormatter {
	//private final String 
	
	public void format() throws Exception{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		System.setOut(new PrintStream(baos));
		String[] scripts = {"/home/oleng/temp/beautify.js", "/home/oleng/temp/PersonModel.js"};
		Main.main(scripts);
		

		
		//String output = baos.toString();
		//System.out.println();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
