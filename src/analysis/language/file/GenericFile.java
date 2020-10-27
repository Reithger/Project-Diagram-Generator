package analysis.language.file;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import analysis.language.actor.GenericClass;
import analysis.language.actor.GenericDefinition;
import analysis.language.actor.GenericInterface;
import analysis.language.component.Argument;
import analysis.language.component.Constructor;
import analysis.language.component.Function;
import analysis.language.component.InstanceVariable;
import explore.Cluster;

public abstract class GenericFile {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static boolean procInstance;
	private static boolean procFunction;
	private static boolean procPrivate;
	
	private String contents;
	private ArrayList<String> lines;
	private String name;
	private String context;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public GenericFile(File in, String root) {
		lines = new ArrayList<String>();
		contents = "";
		try {
			Scanner sc = new Scanner(in);
			while(sc.hasNextLine()) {
				String nex = sc.nextLine();
				if(nex != null)
					contents += nex + "\n";
			}
			sc.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		lines = preProcess(contents);
		name = findName();
		context = in.getAbsolutePath().substring(root.length()).replaceAll("\\\\", "/");
		if(context.contains("/")) {
			context = context.substring(0, context.lastIndexOf("/"));
		}
		context = context.replaceAll("/", ".");
		if(context.equals(in.getName())) {
			context = "";
		}
	}
	
//---  Operations   ---------------------------------------------------------------------------

	public void process(HashMap<String, GenericDefinition> classRef, HashMap<String, GenericInterface> interfaceRef, Cluster parent) {
		HashSet<GenericDefinition> neighbors = parent.getCluster(context.split("\\.")).getComponents();
		if(isClassFile()) {
			processClass(classRef, interfaceRef, neighbors);
		}
		else if(isInterfaceFile()){
			processInterface(interfaceRef.get(getFullName()), classRef, neighbors);
		}
	}
	
	public void processClass(HashMap<String, GenericDefinition> classRef, HashMap<String, GenericInterface> interfaceRef, HashSet<GenericDefinition> neighbors) {
		GenericClass out = (GenericClass)(classRef.get(getFullName()));
		out.setInheritance(extractInheritance(classRef));
		out.setAbstract(extractAbstract());
		HashSet<String> bar = new HashSet<String>();
		for(GenericInterface gi : extractRealizations(interfaceRef)) {
			out.addRealization(gi);
			bar.add(gi.getFullName());
		}
		for(GenericDefinition gc : extractAssociations(classRef, neighbors)) {
			if(!gc.equals(out.getInheritance()) && !bar.contains(gc.getFullName()))		//TODO: What if a class both extends and associates with another class?
				out.addAssociation(gc);
		}
		if(getStatusFunction()) {
			for(Function f : extractFunctions()) {
				out.addFunction(f);
			}
		}
		if(getStatusInstanceVariable()) {
			for(InstanceVariable iv : extractInstanceVariables()) {
				out.addInstanceVariable(iv);
			}
		}
	}

	public void processInterface(GenericInterface in, HashMap<String, GenericDefinition> reference, HashSet<GenericDefinition> neighbors) {
		GenericInterface out = in;
		for(Function f : extractFunctions()) {
			out.addFunction(f);
		}
		for(GenericDefinition gc : extractAssociations(reference, neighbors)) {
			out.addAssociation(gc);
		}
	}
	
	//-- Subclass Implement  ----------------------------------

	public abstract boolean isClassFile();
	
	public abstract boolean isInterfaceFile();
	
	protected abstract String findName();
	
	/**
	 * According to whatever rules of the language the file is for, process the lump sum String file contents into
	 * significant lines of single spaced text.
	 * 
	 */
	
	protected abstract ArrayList<String> preProcess(String contents);

	protected abstract boolean extractAbstract();
	
	protected abstract ArrayList<Function> extractFunctions();
	
	protected abstract ArrayList<InstanceVariable> extractInstanceVariables();
	
	protected abstract GenericClass extractInheritance(HashMap<String, GenericDefinition> ref);
	
	protected abstract ArrayList<GenericInterface> extractRealizations(HashMap<String, GenericInterface> ref);
	
	protected abstract ArrayList<GenericDefinition> extractAssociations(HashMap<String, GenericDefinition> ref, HashSet<GenericDefinition> neighbor);
	
	//-- Support Methods  -------------------------------------
	
	protected String stripContext(String in) {
		return in.substring(in.lastIndexOf(".") + 1);
	}
	
	/**
	 * Assumes input tokens ArrayList<<r>String> is in format of [name, type, name, type, ...]
	 * 
	 * 
	 * @param tokens
	 * @return
	 */
	
	protected ArrayList<Argument> compileArguments(ArrayList<String> tokens){
		ArrayList<Argument> out = new ArrayList<Argument>();
		for(int i = 0; i < tokens.size(); i += 2) {
			out.add(new Argument(tokens.get(i), tokens.get(i + 1)));
		}
		return out;
	}
	
	protected Function compileFunction(String vis, String name, String ret, ArrayList<Argument> arguments, boolean statStatic, boolean statAbstract) {
		Function in = new Function(vis, name, arguments, ret);
		in.setAbstract(statAbstract);
		in.setStatic(statStatic);
		return in;
	}
	
	protected Constructor compileConstructor(String vis, String name, ArrayList<Argument> arguments) {
		return new Constructor(vis, name, arguments);
	}
	
	protected InstanceVariable compileInstanceVariable(String vis, String typ, String nom, boolean statStatic) {
		InstanceVariable in = new InstanceVariable(vis, nom, typ);
		in.setStatic(statStatic);
		return in;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public static void assignProcessStates(boolean inst, boolean func, boolean priv) {
		procInstance = inst;
		procFunction = func;
		procPrivate = priv;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	protected ArrayList<String> getFileContents(){
		return lines;
	}

	public String getName() {
		return name;
	}
	
	public String getContext() {
		return context;
	}
	
	public String getFullName() {
		return getContext() + "/" + getName();
	}
	
	protected boolean getStatusInstanceVariable() {
		return procInstance;
	}
	
	protected boolean getStatusPrivate() {
		return procPrivate;
	}
	
	protected boolean getStatusFunction() {
		return procFunction;
	}
	
}
