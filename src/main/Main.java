package main;

import java.util.Arrays;
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
			ArrayList<String> argList = new ArrayList<>(Arrays.asList(args));
			String root = findArgData(argList, "-root=");
			String saveName = findArgData(argList, "-savename=");
			boolean inst = containsArg(argList, "-i");
			boolean func = containsArg(argList, "-f");
			boolean priv = containsArg(argList, "-p");
			runLoose(root, saveName, inst, func, priv, argList.toArray(new String[0]));
		}
	}

	private static String findArgData(ArrayList<String> args, String argPrefix) {
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i).startsWith(argPrefix)) {
				String arg = args.remove(i);
				return arg.substring(argPrefix.length());
			}
		}
		throw new IllegalArgumentException(argPrefix + " not specified");
	}

	private static boolean containsArg(ArrayList<String> args, String arg) {
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i).equals(arg)) {
				args.remove(i);
				return true;
			}
		}
		return false;
	}
	
	private static <T> void runLoose(String path, String name, boolean inst, boolean func, boolean priv, String ... rem) {
		ConvertVisual.assignPaths(ADDRESS_IMAGES, ADDRESS_SOURCES, ADDRESS_SETTINGS);
		ArrayList<String> ignore = new ArrayList<String>();
		for(String s : rem) {
			ignore.add(s);
		}
		ConvertVisual.generateUMLDiagram(path, ignore, name, inst, func, priv);
	}
	
	private static void runReal() {
		Display disp = new Display();
	}

}
