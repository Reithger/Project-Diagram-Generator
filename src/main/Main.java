package main;

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

//Single line test

public class Main {

	public final static String ADDRESS_SETTINGS = "./Diagram/settings/";
	public final static String ADDRESS_IMAGES = "./Diagram/images/";
	public final static String ADDRESS_SOURCES = "./Diagram/sources/";
	public final static String ADDRESS_CONFIG = ADDRESS_SETTINGS + "/config.txt";
	
	private final static String NAME = "SVI Library UML Feb 14";
	
	public static void main(String[] args) throws Exception{
		runReal();

		
		/*String root = "C:/Users/Borinor/git/";
		String saveName = "UML - ";
		
		File source = new File(root);
		
		for(String s : source.list()) {
			if(s.contains("RobotTurtles")) {
				String path = root + s;
				File check = new File(path);
				boolean src = false;
				for(String t : check.list()) {
					if(t.equals("src")) {
						src = true;
					}
				}
				runLoose(path + (src ? "/src/" : ""), saveName + s);
			}
		}*/
	}
	
	private static void runLoose(String path, String name, String ... rem) {
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
