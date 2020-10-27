package explore;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import analysis.language.actor.GenericClass;
import analysis.language.actor.GenericDefinition;
import analysis.language.actor.GenericInterface;
import analysis.language.file.FileFactory;
import analysis.language.file.GenericFile;

public class Explore{
	
//---  Instance Variables   -------------------------------------------------------------------
		
	private ArrayList<GenericFile> files;
	private HashMap<String, GenericClass> classes;
	private HashMap<String, GenericInterface> interfaces;
	private Cluster parent;
	private String rootPath;
	
	private HashSet<String> ignore;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Explore(File root) {
		rootPath = root.getAbsolutePath();
		ignore = new HashSet<String>();
		files = new ArrayList<GenericFile>();
		classes = new HashMap<String, GenericClass>();
		interfaces = new HashMap<String, GenericInterface>();
		parent = new Cluster(new String[] {});
		if(rootPath.charAt(rootPath.length() - 1) != '/') {
			rootPath += "/";
		}
	}

//---  Operations   ---------------------------------------------------------------------------
	
	public void ignorePackage(String path) {
		ignore.add(path);
	}
	
	public void run() {
		File use = new File(rootPath);
		explore(use);
		for(GenericFile f : files) {
			f.process(getDefinitionMapping(), interfaces, parent);
		}
	}
	
	private void explore(File root) {
		for(String s : root.list()) {
			File look = new File(root.getAbsolutePath() + "/" + s);
			if(!look.exists()) {
				continue;
			}
			if(look.isDirectory() && !ignore(look.getAbsolutePath())) {
				explore(look);
			}
			else if(look.isFile()){
				GenericFile f = FileFactory.generateFile(look, rootPath);
				if(f == null) {
					continue;
				}
				if(f.isClassFile()) {
					GenericClass gc = new GenericClass(f.getName(), f.getContext());
					classes.put(gc.getFullName(), gc);
					parent.addComponent(gc.getContextArray(), gc);
				}
				else if(f.isInterfaceFile()) {
					GenericInterface gi = new GenericInterface(f.getName(), f.getContext());
					interfaces.put(gi.getFullName(), gi);
					parent.addComponent(gi.getContextArray(), gi);
				}
				files.add(f);
			}
		}
	}
	
	private boolean ignore(String path) {
		return ignore.contains(formPackagePath(path));
	}
	
	private String formPackagePath(String path) {
		String out = path.substring(rootPath.length());
		out = out.replaceAll("\\\\", ".");
		return out;
	}
		
//---  Getter Methods   -----------------------------------------------------------------------
	
	public Collection<GenericClass> getClasses(){
		return classes.values();
	}
	
	public Collection<GenericInterface> getInterfaces(){
		return interfaces.values();
	}
	
	public ArrayList<GenericDefinition> getDefinitions(){
		ArrayList<GenericDefinition> out = new ArrayList<GenericDefinition>();
		for(String s : classes.keySet())
			out.add(classes.get(s));
		for(String s : interfaces.keySet())
			out.add(interfaces.get(s));
		return out;
	}
	
	public HashMap<String, GenericDefinition> getDefinitionMapping(){
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
