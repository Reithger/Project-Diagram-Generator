package analysis.language;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

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
	
	protected void addFunction(Function func) {
		if(func == null) {
			return;
		}
		functions.add(func);
	}
	
	protected void addInstanceVariable(InstanceVariable inst) {
		if(inst == null) {
			return;
		}
		instanceVariables.add(inst);
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