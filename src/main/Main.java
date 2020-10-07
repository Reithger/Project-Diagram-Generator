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
 * 
 * @author Borinor
 *
 */

public class Main {

	//TODO: Make a program that takes another program and generates a UML diagram for reference; parse Import statements, extends/implements, collate class list from project, etc.
	
	private final static String PATH = "C:/Users/Borinor/git/Finite-State-Machine-Model/src/";
	private final static String NAME = "FSM Model";

	public static void main(String[] args) {
		File f = new File(PATH);
		Explore expl = new Explore(f);
		String dot = ConvertVisual.convertClassStructure(expl.getClassStructure());
		ConvertVisual.draw(dot, NAME, "jpg");
	}
	

}
