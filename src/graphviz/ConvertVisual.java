package graphviz;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import analysis.Class;

public class ConvertVisual  {
	
	private final static String REPOSITORY_PATH = "./Diagram/";
	private final static String CONFIG_PATH = "./src/assets/config.properties";
	private final static int DPI_INCREASE = 8;
	
	public static String convertClassStructure(HashMap<String, Class> reference) {
		String out = "digraph G {\n";
		out += 	"node[shape=record];\n\n";
				/*"\tsize=\"5,5\";\r\n" + 
				"\tnode[shape=record,style=filled,fillcolor=gray95];\r\n" + 
				"\tedge[dir=back, arrowtail=empty];\n\n";
		*/
		int val = 1;
		HashMap<String, Integer> ref = new HashMap<String, Integer>();
		for(Class c : reference.values()) {
			ref.put(c.getName(), val);
			out += c.generateDot(val++);
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

