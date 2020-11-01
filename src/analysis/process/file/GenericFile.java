package analysis.process.file;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import analysis.language.actor.GenericClass;
import analysis.language.actor.GenericDefinition;
import analysis.language.actor.GenericEnum;
import analysis.language.actor.GenericInterface;
import analysis.process.Cluster;

public abstract class GenericFile {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static boolean procInstance;
	private static boolean procFunction;
	private static boolean procPrivate;
	
	private String contents;
	private ArrayList<String> lines;
	private String name;
	private String context;
	private GenericDefinition gen;
	
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
		if(isClassFile()) {
			gen = new GenericClass(getName(), getContext());
		}
		else if(isInterfaceFile()) {
			gen = new GenericInterface(getName(), getContext());
		}
		else if(isEnumFile()) {
			gen = new GenericEnum(getName(), getContext());
		}
	}
	
	public GenericFile(ArrayList<String> inLines, String inContext) {
		lines = inLines;
		name = findName();
		context = inContext;
	}
	
//---  Operations   ---------------------------------------------------------------------------

	public void process(HashMap<String, GenericDefinition> classRef, HashMap<String, GenericDefinition> interfaceRef, Cluster parent) {
		HashSet<GenericDefinition> neighbors = parent.getCluster(context.split("\\.")).getComponents();
		if(isClassFile()) {
			processClass(classRef, interfaceRef, neighbors);
		}
		else if(isInterfaceFile()){
			processInterface(interfaceRef.get(getFullName()), classRef, neighbors);
		}
		else if(isEnumFile()) {
			processEnum(classRef.get(getFullName()), classRef, neighbors);
		}
	}
	
	public void processClass(HashMap<String, GenericDefinition> classRef, HashMap<String, GenericDefinition> interfaceRef, HashSet<GenericDefinition> neighbors) {
		GenericClass out = (GenericClass)(classRef.get(getFullName()));
		out.setInheritance(extractInheritance(classRef));
		out.setAbstract(extractAbstract());
		HashSet<String> bar = new HashSet<String>();
		for(GenericDefinition gi : extractRealizations(interfaceRef)) {
			out.addRealization(gi);
			bar.add(gi.getFullName());
		}
		for(GenericDefinition gc : extractAssociations(classRef, neighbors)) {
			if(!gc.equals(out.getInheritance()) && !bar.contains(gc.getFullName()))		//TODO: What if a class both extends and associates with another class?
				out.addAssociation(gc);
		}
		if(getStatusFunction()) {
			extractFunctions();
		}
		if(getStatusInstanceVariable()) {
			extractInstanceVariables();
		}
	}

	public void processInterface(GenericDefinition in, HashMap<String, GenericDefinition> reference, HashSet<GenericDefinition> neighbors) {
		GenericDefinition out = in;
		if(getStatusFunction()) {
			extractFunctions();
		}
		for(GenericDefinition gc : extractAssociations(reference, neighbors)) {
			out.addAssociation(gc);
		}
	}
	
	public void processEnum(GenericDefinition in, HashMap<String, GenericDefinition> reference, HashSet<GenericDefinition> neighbors) {
		for(GenericDefinition gc : extractAssociations(reference, neighbors)) {
			in.addAssociation(gc);
		}
	}
	
	//-- Subclass Implement  ----------------------------------

	public abstract boolean isClassFile();
	
	public abstract boolean isInterfaceFile();
	
	public abstract boolean isEnumFile();
	
	public abstract boolean detectInternalClasses();
	
	public abstract ArrayList<GenericFile> extractInternalClasses();
	
	protected abstract String findName();
	
	/**
	 * According to whatever rules of the language the file is for, process the lump sum String file contents into
	 * significant lines of single spaced text.
	 * 
	 */
	
	protected abstract ArrayList<String> preProcess(String contents);

	protected abstract boolean extractAbstract();
	
	protected abstract void extractFunctions();
	
	protected abstract void extractInstanceVariables();
	
	protected abstract GenericDefinition extractInheritance(HashMap<String, GenericDefinition> ref);
	
	protected abstract ArrayList<GenericDefinition> extractRealizations(HashMap<String, GenericDefinition> ref);
	
	protected abstract ArrayList<GenericDefinition> extractAssociations(HashMap<String, GenericDefinition> ref, HashSet<GenericDefinition> neighbor);
	
	//-- Support Methods  -------------------------------------
	
	protected String stripContext(String in) {
		return in.substring(in.lastIndexOf(".") + 1);
	}
	
	protected void addFunctionToDef(String vis, String name, String ret, ArrayList<String> argNom, ArrayList<String> argTyp, boolean statStatic, boolean statAbstract) {
		gen.addFunction(vis, name, ret, argNom, argTyp, statStatic, statAbstract);
	}
	
	protected void addConstructorToDef(String vis, String name, ArrayList<String> argNom, ArrayList<String> argTyp) {
		gen.addConstructor(vis, name, argNom, argTyp);
	}
	
	protected void addInstanceVariableToClass(String vis, String typ, String nom, boolean statStatic) {
		if(isClassFile()) {
			((GenericClass)gen).addInstanceVariable(vis, typ, nom, statStatic);
		}
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

	public GenericDefinition getDefinition() {
		return gen;
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
