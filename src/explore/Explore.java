package explore;
import java.io.File;
import java.util.HashMap;

import analysis.Class;
import analysis.ClassFactory;

public class Explore{
		
	private HashMap<String, Class> classes;
	private String rootPath;
	
	public Explore(File root) {
		rootPath = root.getAbsolutePath();
		classes = new HashMap<String, Class>();
		explore(root);
		for(Class c : classes.values()) {
			c.process(classes);
		}
	}
	
	private void explore(File root) {
		for(String s : root.list()) {
			File look = new File(root.getAbsolutePath() + "/" + s);
			if(look.isDirectory()) {
				explore(look);
			}
			else {
				Class c = ClassFactory.generateClass(look, rootPath);
				if(c != null)
					classes.put(c.getName(), c);
			}
		}
	}
	
	public HashMap<String, Class> getClassStructure(){
		return classes;
	}
	
}

	