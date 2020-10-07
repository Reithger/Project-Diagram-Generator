package graphviz;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.HashSet;

import analysis.Class;

public class ConvertVisual  {
	
	private final static String REPOSITORY_PATH = "./Diagram/";
	private final static String CONFIG_PATH = "./src/assets/config.properties";
	private final static int DPI_INCREASE = 8;
	
	//TODO: Line formatting (straight lines, 90 degree turns)
	
	public static String convertClassStructure(HashMap<String, Class> reference, HashSet<String> clusters, boolean instanceVar, boolean funct) {
		String out = "digraph G {\n";
		out += 	"\tnode[shape=record,style=filled,fillcolor=gray95];\r\n" + 
				"\tedge[dir=back, arrowtail=empty];\n\n";

		out += "\n";
		
		int val = 1;
		HashMap<String, Integer> ref = new HashMap<String, Integer>();
		for(Class c : reference.values()) {
			ref.put(c.getName(), val);
			out += c.generateDot(val++, instanceVar, funct);
		}
		out += "\n";
		
		for(String s: clusters) {
			String[] pack = s.split("\\.");
			String nom = "";
			for(int i = 0; i < pack.length; i++) {
				nom += (i == 0 ? "" : "_") + pack[i];
				String tab = "";
				for(int j = 0; j < i + 1; j++) {
					tab += "\t";
				}
				out += tab + "subgraph cluster_" + nom + "{\n";
				out += tab + "\tlabel = \"" + nom + "\";\n";
			}
			String tab = "";
			for(int j = 0; j < pack.length; j++) {
				tab += "\t";
			}
			for(String c : ref.keySet()) {
				if(c.contains(s))
					out += tab + "\tn" + ref.get(c) + ";\n";
			}
			for(int i = 0; i < pack.length; i++) {
				for(int j = pack.length - i - 1; j >= 0; j--) {
					out += "\t";
				}
				out += "}\n";
			}
		}
		
		out += "\n";
		for(Class c : reference.values()) {
			out += c.generateAssociations(ref.get(c.getName()), ref);
		}
		
		out += "}";
		return out;
	}
	
	public static File draw(String dotData, String name, String type) {
		File folder = new File(REPOSITORY_PATH);
		folder.mkdir();
		GraphViz gv = new GraphViz(REPOSITORY_PATH, CONFIG_PATH);
		gv.add(dotData);
		for(int i = 0; i < DPI_INCREASE; i++) {
			gv.increaseDpi();
		}
		File out = new File(REPOSITORY_PATH + "//" + name + "." + type);

		gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), out);
		
		File ref = new File(REPOSITORY_PATH + "//" + name + ".txt");
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

