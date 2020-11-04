package main;

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
	private final static String PATH9 = "C:/Users/Borinor/git/Olivia Saralin RobotTurtle";
	private final static String PATH10 = "C:\\Users\\Borinor\\git\\RobotTurtle-RijaBen";
	
	public final static String ADDRESS_SETTINGS = "./Diagram/settings/";
	public final static String ADDRESS_IMAGES = "./Diagram/images/";
	public final static String ADDRESS_SOURCES = "./Diagram/sources/";
	public final static String ADDRESS_CONFIG = ADDRESS_SETTINGS + "/config.txt";
	
	private final static String NAME = "Reciprocal";
	
	public static void main(String[] args) throws Exception{
		runReal();
		//runLoose();
	}
	
	private static void runLoose() {
		ConvertVisual.assignPaths(ADDRESS_IMAGES, ADDRESS_SOURCES, ADDRESS_SETTINGS);
		ArrayList<String> ignore = new ArrayList<String>();
		ConvertVisual.generateUMLDiagram(PATH2, ignore, NAME, false, false, false);
	}
	
	private static void runReal() {
		Display disp = new Display();
	}

}
