package analysis.language;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import analysis.component.Argument;
import analysis.component.Constructor;
import analysis.component.Function;
import analysis.component.InstanceVariable;

public abstract class Class{

//---  Instance Variables   -------------------------------------------------------------------
	
	private static boolean procInstance;
	private static boolean procFunction;
	private static boolean procPrivate;

	private String name;
	private ArrayList<String> lines;
	private ArrayList<Class> associates;
	private ArrayList<Function> functions;
	private ArrayList<InstanceVariable> instanceVariables;
	
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Class(File in, String root) {
		String nom = in.getAbsolutePath();
		name = nom.substring(root.length() + 1);
		name = name.replaceAll("\\\\", ".");
		name = name.substring(0, name.indexOf(getType()));
		associates = new ArrayList<Class>();
		functions = new ArrayList<Function>();
		instanceVariables = new ArrayList<InstanceVariable>();
		try {
			Scanner sc = new Scanner(in);
			lines = new ArrayList<String>();
			while(sc.hasNextLine()) {
				lines.add(sc.nextLine());
			}
			sc.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
//---  Operations   ---------------------------------------------------------------------------

	public abstract void process(HashMap<String, Class> reference);
	
	public String generateDot(int val) {
		String pref = "\tn" + val + " [label = <{";
		String out = stripContext(getName()) + "|";
		for(int i = 0; i < instanceVariables.size(); i++) {
			out += instanceVariables.get(i).getDot() + (i + 1 < instanceVariables.size() ? "<BR/>" : "");
		}
		out += "|";
		for(int i = 0; i < functions.size(); i++) {
			out += functions.get(i).getDot() + (i + 1 < functions.size() ? "<BR/>" : "");
		}

		String post = "}>];\n";
		return pref + out + post;
	};
	
	
	public String generateAssociations(HashMap<String, Integer> ref) {
		int val = ref.get(getName());
		String out = "\t";
		for(Class c : associates) {
			out += "n" + val + " -> n" + ref.get(c.getName()) + ";\n";
		}
		return out;
	}

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
	
	protected void addClassAssociate(Class ref) {
		if(ref == null) {
			return;
		}
		associates.add(ref);
	}
	
	protected void addFunction(Function in) {
		functions.add(in);
	}
	
	protected void addConstructor(Constructor in) {
		functions.add(in);
	}
	
	protected void addInstanceVariable(InstanceVariable in) {
		instanceVariables.add(in);
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	protected boolean getStatusInstanceVariable() {
		return procInstance;
	}
	
	protected boolean getStatusPrivate() {
		return procPrivate;
	}
	
	protected boolean getStatusFunction() {
		return procFunction;
	}
	
	public abstract String getType();
	
	public String getName() {
		return name;
	}
	
	public String getContext(String in) {
		if(!in.contains("."))
			return "";
		return in.substring(0, in.lastIndexOf("."));
	}
	
	public ArrayList<Function> getFunctions() {
		return functions;
	}
	
	public ArrayList<InstanceVariable> getInstanceVariables(){
		return instanceVariables;
	}
	
	protected ArrayList<String> getFileContents(){
		return lines;
	}

	public ArrayList<Class> getClassAssociates(){
		return associates;
	}
	
}