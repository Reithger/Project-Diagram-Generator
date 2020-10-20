package explore;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import analysis.language.actor.GenerateDot;
import analysis.language.actor.GenericClass;
import analysis.language.actor.GenericDefinition;
import analysis.language.actor.GenericInterface;
import analysis.language.file.FileFactory;
import analysis.language.file.GenericFile;

public class Explore{
	
//---  Instance Variables   -------------------------------------------------------------------
		
	private HashMap<String, GenericFile> files;
	private HashMap<String, GenericClass> classes;
	private HashMap<String, GenericInterface> interfaces;
	private Cluster parent;
	private String rootPath;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Explore(File root, String partialCut) {
		rootPath = root.getAbsolutePath();
		files = new HashMap<String, GenericFile>();
		classes = new HashMap<String, GenericClass>();
		interfaces = new HashMap<String, GenericInterface>();
		parent = new Cluster(new String[] {});
		partialCut = partialCut.replaceAll("\\.", "/");
		File use = new File(root.getAbsolutePath() + "/" + partialCut);
		explore(use);
		for(GenericFile f : files.values()) {
			f.process(classes, interfaces, parent);
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
				GenericFile f = FileFactory.generateFile(look, rootPath);
				if(f == null) {
					return;
				}
				if(f.isClassFile()) {
					GenericClass gc = new GenericClass(f.getName(), f.getContext());
					classes.put(gc.getName(), gc);
					parent.addComponent(gc.getContextArray(), gc);
				}
				else if(f.isInterfaceFile()) {
					GenericInterface gi = new GenericInterface(f.getName(), f.getContext());
					interfaces.put(gi.getName(), gi);
					parent.addComponent(gi.getContextArray(), gi);
				}
			}
		}
	}
		
//---  Getter Methods   -----------------------------------------------------------------------
	
	public HashMap<String, GenericClass> getClasses(){
		return classes;
	}
	
	public HashMap<String, GenericInterface> getInterfaces(){
		return interfaces;
	}
	
	public HashMap<String, GenericDefinition> getDefinitions(){
		HashMap<String, GenericDefinition> out = new HashMap<String, GenericDefinition>();
		for(String s : classes.keySet())
			out.put(s, classes.get(s));
		for(String s : interfaces.keySet())
			out.put(s, interfaces.get(s));
		return out;
	}
	
	public Cluster getClusterRoot(){
		return parent;
	}
	
//--  Setter Methods   ------------------------------------------------------------------------
	
	public static void setParameters(boolean inst, boolean func, boolean priv) {
		GenericFile.assignProcessStates(inst, func, priv);
	}
	
}
