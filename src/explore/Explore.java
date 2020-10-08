package explore;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import analysis.language.Class;
import analysis.language.ClassFactory;

public class Explore{
	
//---  Instance Variables   -------------------------------------------------------------------
		
	private HashMap<String, Class> classes;
	private HashSet<String> clusters;
	private String rootPath;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Explore(File root, String partialCut) {
		rootPath = root.getAbsolutePath();
		classes = new HashMap<String, Class>();
		clusters = new HashSet<String>();
		partialCut = partialCut.replaceAll("\\.", "/");
		File use = new File(root.getAbsolutePath() + "/" + partialCut);
		explore(use);
		for(Class c : classes.values()) {
			c.process(classes);
			clusters.add(c.getContext(c.getName()));
		}
	}

//---  Operations   ---------------------------------------------------------------------------
	
	private void explore(File root) {
		for(String s : root.list()) {
			File look = new File(root.getAbsolutePath() + "/" + s);
			if(look.isDirectory()) {
				explore(look);
			}
			else {
				Class c = ClassFactory.generateClass(look, rootPath);
				if(c != null) {
					classes.put(c.getName(), c);
				}
			}
		}
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public HashMap<String, Class> getClassStructure(){
		return classes;
	}
	
	public HashSet<String> getClusters(){
		return clusters;
	}
	
//--  Setter Methods   ------------------------------------------------------------------------
	
	public static void setParameters(boolean inst, boolean func, boolean priv) {
		Class.assignProcessStates(inst, func, priv);
	}
	
}

	