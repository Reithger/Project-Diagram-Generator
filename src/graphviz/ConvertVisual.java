package graphviz;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.HashSet;

import analysis.language.Class;
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
		String dot = ConvertVisual.convertClassStructure(expl.getClassStructure(), expl.getClusters());
		return ConvertVisual.draw(dot, name, "jpg").getAbsolutePath();
	}
	
	public static String convertClassStructure(HashMap<String, Class> reference, HashSet<String> clusters) {
		String out = "digraph G {\n";
		out += 	"\tnode[shape=record,style=filled,fillcolor=gray95];\r\n" + 
				"\tedge[dir=back, arrowtail=empty, concentrate=true];\n" +
				"\tgraph[splines = ortho, ranksep = 1, ratio = fill];\n" +
				"rankdir = TB;";	//splines = ortho, nodesep = 1 for straight lines, looks rough, let user change how lines are displayed

		out += "\n";
		
		int val = 1;
		HashMap<String, Integer> ref = new HashMap<String, Integer>();
		for(Class c : reference.values()) {
			ref.put(c.getName(), val);
			String lab = c.generateDot(val++);
			out += lab;
		}
		out += "\n";

		int maxSize = 30;
		int minSize = 16;
		
		for(String s: clusters) {
			String[] pack = s.split("\\.");
			String nom = "";
			int fontSize = maxSize;
			int penWidth = 1;
			for(int i = 0; i < pack.length; i++) {
				nom += (i == 0 ? "" : "_") + pack[i];
				out += tabBuffer(i+1) + "subgraph cluster_" + nom + "{\n";
				out += tabBuffer(i+1) + "\tlabel = \"" + nom.replaceAll("_", "\\.") + "\";\n";
				out += tabBuffer(i+1) + "\tfontsize = " + fontSize + ";\n";
				out += tabBuffer(i+1) + "\tpenwidth = " + penWidth + ";\n";
				fontSize -= 4;
				penWidth += (penWidth + 1 > 6 ? 0 : 1);
			}
			for(String c : ref.keySet()) {
				if(c.contains(s))
					out += tabBuffer(pack.length) + "\tn" + ref.get(c) + ";\n";
			}
			for(int i = 0; i < pack.length; i++) {
				out += tabBuffer(pack.length - i - 1) + "}\n";
			}
		}
		
		out += "\n";
		for(Class c : reference.values()) {
			out += c.generateAssociations(ref);
		}
		
		out += "}";
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

