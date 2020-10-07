package analysis;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public abstract class Class{
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private ArrayList<String> lines;
	private ArrayList<Class> associates;
	private ArrayList<String> functions;
	private ArrayList<String> instanceVariables;
	
	private String name;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Class(File in, String root) {
		String nom = in.getAbsolutePath();
		name = nom.substring(root.length() + 1);
		name = name.replaceAll("\\\\", ".");
		name = name.substring(0, name.indexOf(getType()));
		associates = new ArrayList<Class>();
		functions = new ArrayList<String>();
		instanceVariables = new ArrayList<String>();
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
	
	public String generateDot(int val, boolean inst, boolean func) {
		String out = "\tn" + val + " [label = \"{" + stripContext(getName()) + "|";
		if(inst) {
			for(int i = 0; i < instanceVariables.size(); i++) {
				String s = instanceVariables.get(i);
				out += s + (i + 1 < instanceVariables.size() ? "&#92;n" : "");
			}
		}
		out += "|";
		if(func) {
			for(int i = 0; i < functions.size(); i++) {
				String s =functions.get(i);
				out += s + (i + 1 < functions.size() ? "&#92;n" : "");
			}
		}
		out += "}\"];\n";
		return out;
	};
	
	public String generateAssociations(int val, HashMap<String, Integer> ref) {
		String out = "\t";
		for(Class c : associates) {
			out += "n" + val + " -> n" + ref.get(c.getName()) + ";\n";
		}
		return out;
	}
	
	protected void addClassAssociate(Class ref) {
		associates.add(ref);
	}
	
	protected void addFunction(String func) {
		functions.add(func);
	}
	
	protected void addInstanceVariable(String inst) {
		instanceVariables.add(inst);
	}
	
	public String stripContext(String in) {
		return in.substring(in.lastIndexOf(".") + 1);
	}
	
	public String getContext(String in) {
		return in.substring(0, in.lastIndexOf("."));
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public abstract String getType();
	
	public String getName() {
		return name;
	}
	
	public ArrayList<String> getFunctions() {
		return functions;
	}
	
	public ArrayList<String> getInstanceVariables(){
		return instanceVariables;
	}
	
	protected ArrayList<String> getFileContents(){
		return lines;
	}

	public ArrayList<Class> getClassAssociates(){
		return associates;
	}
	
}