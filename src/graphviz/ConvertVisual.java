package graphviz;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.HashSet;

import analysis.language.actor.GenerateDot;
import analysis.language.actor.GenericDefinition;
import explore.Cluster;
import explore.Explore;

public class ConvertVisual  {
	
	private final static String CONFIG_PATH = "./src/assets/config.properties";
	private final static int DPI_INCREASE = 2;
	
	private static String imagePath;
	private static String sourcePath;
	private static String settingsPath;
	
	public static void assignPaths(String img, String src, String sett) {
		imagePath = img;
		sourcePath = src;
		settingsPath = sett;
	}
	
	public static String generateUMLDiagram(String path, String filter, String name, boolean a, boolean b, boolean c) {
		File f = new File(path);
		Explore.setParameters(a, b, c);
		Explore expl = new Explore(f, filter);
		String dot = ConvertVisual.convertClassStructure(expl.getDefinitions(), expl.getClusterRoot());
		return ConvertVisual.draw(dot, name, "jpg").getAbsolutePath();
	}
	
	public static String convertClassStructure(HashMap<String, GenericDefinition> reference, Cluster root) {
		root.debugPrintOut();
		String out = "digraph G {\n";
		out += 	"\tnode[shape=record,style=filled,fillcolor=gray95];\r\n" + 
				"\tedge[dir=back, arrowtail=empty, concentrate=true];\n" +
				"\tgraph[splines = ortho, ranksep = 1, ratio = fill];\n" +
				"\trankdir = TB;";	//splines = ortho, nodesep = 1 for straight lines, looks rough, let user change how lines are displayed

		out += "\n";
		
		int val = 1;
		HashMap<String, Integer> ref = new HashMap<String, Integer>();
		for(GenericDefinition c : reference.values()) {
			ref.put(c.getName(), val);
			String lab = c.generateDot(val++);
			out += lab;
		}
		out += "\n";

		out += processClusters(out, root, ref, 1, 30, 1);
		
		out += "\n";
		for(GenericDefinition c : reference.values()) {
			out += c.generateRelationships(ref);
		}
		
		out += "}";
		return out;
	}
	
	private static String processClusters(String out, Cluster next, HashMap<String, Integer> reference, int depth, int fontSize, int penWidth) {
		if(next == null)
			return out;
		String address = next.getAddress();
		out += tabBuffer(depth) + "subgraph_cluster_" + address.replaceAll("\\.",  "_") + "{\n";
		out += tabBuffer(depth + 1) + "label = \"" + address + "\";\n";
		out += tabBuffer(depth + 1) + "fontsize = " + fontSize + ";\n";
		out += tabBuffer(depth + 1) + "penwidth = " + penWidth + ";\n";
		for(GenericDefinition gd : next.getComponents()) {
			out += tabBuffer(depth + 1) + "n" + reference.get(gd.getName()) + ";\n";
		}
		for(Cluster c : next.getChildren()) {
			out += processClusters(out, c, reference, depth + 1, fontSize - 4, penWidth + 1);
		}
		out += tabBuffer(depth) + "}\n";
		return out;
	}
	
	private static String tabBuffer(int in) {
		String out = "";
		while(in-- > 0) {
			out += "\t";
		}
		return out;
	}
	
	public static File draw(String dotData, String name, String type) {
		File folder = new File(imagePath);
		folder.mkdir();
		GraphViz gv = new GraphViz(imagePath, CONFIG_PATH);
		gv.add(dotData);
		for(int i = 0; i < DPI_INCREASE; i++) {
			gv.increaseDpi();
		}
		File out = new File(imagePath + "//" + name + "." + type);

		gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), out);
		
		File ref = new File(sourcePath + "//" + name + ".txt");
		ref.delete();
		try {
			RandomAccessFile raf = new RandomAccessFile(ref, "rw");
			raf.writeBytes(dotData);
			raf.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Done");
		return out;
	}
}

