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
	
//---  Constants   ----------------------------------------------------------------------------
	
	protected final static int VISIBILITY_PUBLIC = 0;
	protected final static int VISIBILITY_PRIVATE = 1;
	protected final static int VISIBILITY_PROTECTED = 2;
	protected final static int VISIBILITY_OTHER = 3;
	
	protected final static String FULL_NAME_SEPARATOR = "/";
	
	protected final static String ASSOCIATION_STAR_IMPORT = "*";
	
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
	
//---  Operations   ---------------------------------------------------------------------------

	public void process(HashMap<String, GenericDefinition> classRef, Cluster parent) {
		HashSet<String> neighbors = parent.getCluster(context.split("\\.")).getComponents();
		if(isClassFile()) {
			processClass(classRef, neighbors);
		}
		else if(isInterfaceFile()){
			processInterface(classRef, neighbors);
		}
		else if(isEnumFile()) {
			processEnum(classRef.get(getFullName()), classRef, neighbors);
		}
	}
	
	public void processClass(HashMap<String, GenericDefinition> classRef, HashSet<String> neighbors) {
		handleInheritance(extractInheritance(), classRef);
		
		((GenericClass)gen).setAbstract(extractAbstract());
		HashSet<String> bar = handleRealizations(extractRealizations(), classRef);
		handleAssociations(neighbors, bar, classRef);
		if(getStatusFunction()) {
			extractFunctions();
		}
		
		if(getStatusInstanceVariable()) {
			extractInstanceVariables();
		}
	}

	public void processInterface(HashMap<String, GenericDefinition> classRef, HashSet<String> neighbors) {
		HashSet<String> bar = handleRealizations(extractRealizations(), classRef);
		handleAssociations(neighbors, bar, classRef);
		if(getStatusFunction()) {
			extractFunctions();
		}
	}
	
	public void processEnum(GenericDefinition in, HashMap<String, GenericDefinition> classRef, HashSet<String> neighbors) {
		HashSet<String> bar = handleRealizations(extractRealizations(), classRef);
		handleAssociations(neighbors, bar, classRef);
		if(getStatusFunction()) {
			extractFunctions();
		}
	}
	
	//-- Other  -----------------------------------------------
	
	private void handleInheritance(String parName, HashMap<String, GenericDefinition> ref) {
		if(parName == null)
			return;
		for(GenericDefinition gd : ref.values()) {
			if(gd.getName().equals(parName)) {
				((GenericClass)gen).setInheritance(gd);
				return;
			}
		}
	}
	
	private HashSet<String> handleRealizations(ArrayList<String> realiz, HashMap<String, GenericDefinition> ref) {
		HashSet<String> bar = new HashSet<String>();
		for(String s : realiz) {
			for(GenericDefinition gi : ref.values()) {
				if(gi.getName().equals(s)) {
					gen.addRealization(gi);
					bar.add(s);
				}
			}
		}
		return bar;
	}
	
	private void handleAssociations(HashSet<String> neighbors, HashSet<String> bar, HashMap<String, GenericDefinition> ref) {
		ArrayList<String> noms = extractAssociations(neighbors);
		for(String s : noms) {
			if(ref.get(s) != null) {
				if(!bar.contains(breakFullName(ref.get(s).getFullName())[1])) {	//TODO: While I only allow one association
					gen.addAssociation(ref.get(s));
				}
			}
			else if(!s.contains(ASSOCIATION_STAR_IMPORT)){
				for(GenericDefinition gd : ref.values()) {
					if(gd.getName().equals(s) && !bar.contains(gd.getName())) {
						gen.addAssociation(gd);
					}
				}
			}
			else {
				String path = s.substring(0, s.length() - 1);
				for(String con : ref.keySet()) {
					if(con.matches(path + "/.*") && !gen.hasAssociate((ref.get(con))) && !bar.contains(breakFullName(ref.get(con).getFullName())[1])) {
						gen.addAssociation(ref.get(con));
					}
				}
			}
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
	
	protected abstract String extractInheritance();
	
	protected abstract ArrayList<String> extractRealizations();
	
	protected abstract ArrayList<String> extractAssociations(HashSet<String> neighbor);
	
	//-- Support Methods  -------------------------------------
	
	protected String stripContext(String in) {
		return in.substring(in.lastIndexOf(".") + 1);
	}
	
	protected String formFullName(String context, String name) {
		return context + FULL_NAME_SEPARATOR + name;
	}
	
	protected void addFunctionToDef(int vis, String nom, String ret, ArrayList<String> argNom, ArrayList<String> argTyp, boolean statStatic, boolean statAbstract) {
		if(privateCheck(vis)) {
			gen.addFunction(interpretVisibility(vis), nom, ret, argNom, argTyp, statStatic, statAbstract);
		}
	}
	
	protected void addConstructorToDef(int vis, String nom, ArrayList<String> argNom, ArrayList<String> argTyp) {
		if(privateCheck(vis)) {
			gen.addConstructor(interpretVisibility(vis), nom, argNom, argTyp);
		}
	}
	
	protected void addInstanceVariableToClass(int vis, String typ, String nom, boolean statStatic) {
		if(privateCheck(vis)) {
			((GenericClass)gen).addInstanceVariable(interpretVisibility(vis), typ, nom, statStatic);
		}
	}
	
	private boolean privateCheck(int vis) {
		return getStatusPrivate() || vis != VISIBILITY_PRIVATE;
	}
	
	private String interpretVisibility(int in) {
		switch(in) {
			case VISIBILITY_PUBLIC: 
				return "+";
			case VISIBILITY_PRIVATE:
				return "-";
			case VISIBILITY_PROTECTED:
				return "#";
			default:
				return "?";
		}
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public static void assignProcessStates(boolean inst, boolean func, boolean priv) {
		procInstance = inst;
		procFunction = func;
		procPrivate = priv;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public ArrayList<String> getFileContents(){
		return lines;
	}

	public GenericDefinition getDefinition() {
		return gen;
	}
	
	public String[] breakFullName(String in) {
		return in.split(FULL_NAME_SEPARATOR);
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
