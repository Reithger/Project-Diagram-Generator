package main;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;

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

	private final static String PATH = "C:/Users/Borinor/git/Finite-State-Machine-Model/src/";
	private final static String PATH2 = "C:/Users/Borinor/eclipse-workspace/Project Diagram Generator/src/";
	private final static String PATH3 = "C:/Users/Borinor/git/SoftwareVisualInterface/src";
	private final static String PATH4 = "C:/Users/Borinor/git/ZombieApocalypse/src"; 
	private final static String PATH5 = "C:/Users/Borinor/git/Mr.Jack/src"; 
	private final static String PATH6 = "C:/Users/Borinor/eclipse-workspace/OOExample1/src/"; 
	private final static String PATH7 = "C:/Users/Borinor/eclipse-workspace/Pixel Art Program V3.0/src";
	private final static String PATH8 = "C:/Users/Borinor/git/RobotTurtle/src";
	private final static String PATH9 = "C:/Users/Borinor/eclipse-workspace/Pixel Art Program V3.0/src";
	private final static String PATH10 = "C:/Users/Borinor/git/RobotTurtle-Marcia/milestone 4";
	
	public final static String ADDRESS_SETTINGS = "./Diagram/settings/";
	public final static String ADDRESS_IMAGES = "./Diagram/images/";
	public final static String ADDRESS_SOURCES = "./Diagram/sources/";
	public final static String ADDRESS_CONFIG = ADDRESS_SETTINGS + "/config.txt";
	
	private final static String NAME = "SVI UML Newest";
	
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
		
		//runLoose("C:\\Users\\Borinor\\eclipse-workspace\\RobotTurtles - Alex\\src", "UML - RobotTurtles - Alex");
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
