package main;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import explore.Explore;
import graphviz.ConvertVisual;

/**
 * 
 * 
 * Hierarchy:
 *  - Explore
 *  - Class
 *  
 * TODO: Options for 'splines = ortho' and 'nodesep = 1' separately
 * TODO: JavaClass needs to specify inheritance/directed association/etc. in associates
 * TODO: Show package hierarchy for easy means of filtering what packages are examined (already possible, just remember to make it look nice for the user)
 * TODO: A UI, embed GraphViz?
 * 
 * @author Ada Clevinger
 *
 */

public class Main {

	private final static String PATH = "C:/Users/Borinor/git/Finite-State-Machine-Model/src/";
	private final static String PATH2 = "C:/Users/Borinor/eclipse-workspace/Project Diagram Generator/src/";
	private final static String PATH3 = "C:/Users/Borinor/git/SoftwareVisualInterface/src";
	private final static String PATH4 = "C:/Users/Borinor/git/ZombieApocalypse/src"; 
	private final static String NAME = "FSM Model";
	
	public static void main(String[] args) {
		File f = new File(PATH);
		Explore.setParameters(false,  false,  false);
		Explore expl = new Explore(f, "");
		String dot = ConvertVisual.convertClassStructure(expl.getClassStructure(), expl.getClusters());
		ConvertVisual.draw(dot, NAME, "jpg");
	}
	

}
