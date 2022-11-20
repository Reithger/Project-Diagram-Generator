package main;

import java.io.*;
import java.util.ArrayList;

import image.ConvertVisual;
import ui.Display;

/**
 * 
 * 
 * Hierarchy:
 *  - Explore
 *  - Class
 *  
 * TODO: A UI, embed GraphViz? c
 * 
 * @author Ada Clevinger
 *
 */

public class Main <T> {

	public final static String ADDRESS_SETTINGS = "./Diagram/settings/";
	public final static String ADDRESS_IMAGES = "./Diagram/images/";
	public final static String ADDRESS_SOURCES = "./Diagram/sources/";
	public final static String ADDRESS_CONFIG = ADDRESS_SETTINGS + "/config.txt";
	
	public static void main(String[] args) throws Exception{
		if (args.length == 0) runReal();
		else {
		
			String root = args[0];
			String saveName = "UML - ";
			
			File source = new File(root);
			
			for (String s : source.list()) {
				String path = root + File.separator + s;
				File check = new File(path);
				boolean src = false;
                if (check.isDirectory() && s.equals("src")) {
                    src = true;
                }
				if (src) runLoose(path, saveName + source.getName());
			}
			
			//runLoose(PATH3, NAME);
		}
	}
	
	private static <T> void runLoose(String path, String name, String ... rem) {
		ConvertVisual.assignPaths(ADDRESS_IMAGES, ADDRESS_SOURCES, ADDRESS_SETTINGS);
		ArrayList<String> ignore = new ArrayList<String>();
		for(String s : rem) {
			ignore.add(s);
		}
		ConvertVisual.generateUMLDiagram(path, ignore, name, false, false, false);
	}
	
	private static void runReal() {
		Display disp = new Display();
	}

}
