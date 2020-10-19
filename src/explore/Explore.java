package explore;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import analysis.language.actor.GenericClass;
import analysis.language.actor.GenericInterface;
import analysis.language.file.FileFactory;
import analysis.language.file.GenericFile;

public class Explore{
	
//---  Instance Variables   -------------------------------------------------------------------
		
	private HashMap<String, GenericFile> files;
	private HashMap<String, GenericClass> classes;
	private HashMap<String, GenericInterface> interfaces;
	private HashSet<String> clusters;
	private String rootPath;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Explore(File root, String partialCut) {
		rootPath = root.getAbsolutePath();
		files = new HashMap<String, GenericFile>();
		classes = new HashMap<String, GenericClass>();
		interfaces = new HashMap<String, GenericInterface>();
		clusters = new HashSet<String>();
		partialCut = partialCut.replaceAll("\\.", "/");
		File use = new File(root.getAbsolutePath() + "/" + partialCut);
		explore(use);
		for(GenericFile f : files.values()) {
			f.process(classes);
		}
		//TODO: Reintegrate: clusters.add(c.getContext(c.getName()));
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
				}
				else if(f.isInterfaceFile()) {
					GenericInterface gi = new GenericInterface(f.getName(), f.getContext());
					interfaces.put(gi.getName(), gi);
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
	
	public HashSet<String> getClusters(){
		return clusters;
	}
	
//--  Setter Methods   ------------------------------------------------------------------------
	
	public static void setParameters(boolean inst, boolean func, boolean priv) {
		GenericFile.assignProcessStates(inst, func, priv);
	}
	
}

	 