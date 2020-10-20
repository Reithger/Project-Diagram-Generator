package analysis.language.actor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import analysis.language.component.Constructor;
import analysis.language.component.Function;
import analysis.language.component.InstanceVariable;

/**
 * The platonic ideal of a Class
 * TODO: Reflexive Association!
 * @author Ada Clevinger
 *
 */

public class GenericClass extends GenericDefinition{

//---  Instance Variables   -------------------------------------------------------------------

	private boolean isAbstract;
	
	private GenericClass inheritance;
	private ArrayList<GenericInterface> realizations;	//dotted line, empty arrowhead
	private ArrayList<GenericClass> associates;	//solid line, empty arrowhead
	
	private ArrayList<Function> functions;
	private ArrayList<InstanceVariable> instanceVariables;
	
	
//---  Constructors   -------------------------------------------------------------------------
	
	public GenericClass(String inName, String inContext) {
		super(inName, inContext);
		associates = new ArrayList<GenericClass>();
		functions = new ArrayList<Function>();
		instanceVariables = new ArrayList<InstanceVariable>();
		inheritance = null;
		realizations = new ArrayList<GenericInterface>();
	}
	
//---  Operations   ---------------------------------------------------------------------------

	public String generateDot(int val) {
		String pref = "\tn" + val + " [label = <{";
		String out = formDotName() + "|";
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

	public String generateRelationships(HashMap<String, Integer> ref) {
		int val = ref.get(getName());
		String out = "";
		if(inheritance != null) {
			out = "\tn" + val + " -> n" + ref.get(inheritance.getName()) + "[arrowhead=onormal];\n";
		}
		for(GenericClass c : associates) {
			out += "\tn" + val + " -> n" + ref.get(c.getName());
			if(c.hasAssociate(this)) {
				out += "[arrowhead=none]";
			}
			else {
				out += "[arrowhead=normal]";
			}
			out += ";\n";
		}
		for(GenericInterface i : realizations) {
			out += "\tn" + val + " -> n" + ref.get(i.getName()) + "[arrowhead=onormal line=dotted];\n";
		}
		return out;
	}
	
	private String formDotName() {
		String out = getName();
		if(isAbstract){
			out = "<i>" + out + "</i>";
		}
		return out;
	}
	
	private boolean hasAssociate(GenericClass gc) {
		for(GenericClass c : associates) {
			if(this.compareTo(gc) == 0) {
				return true;
			}
		}
		return false;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void addClassAssociate(GenericClass ref) {
		if(ref == null) {
			return;
		}
		associates.add(ref);
	}
	
	public void addFunction(Function in) {
		functions.add(in);
	}
	
	public void addConstructor(Constructor in) {
		functions.add(in);
	}
	
	public void addInstanceVariable(InstanceVariable in) {
		instanceVariables.add(in);
	}
	
	public void addRealization(GenericInterface in) {
		realizations.add(in);
	}
	
	public void setAbstract(boolean in) {
		isAbstract = in;
	}
	
	public void setInheritance(GenericClass ref) {
		inheritance = ref;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public GenericClass getInheritance() {
		return inheritance;
	}
	
	public ArrayList<GenericInterface> getRealizations(){
		return realizations;
	}

	public boolean getAbstract() {
		return isAbstract;
	}

	public ArrayList<Function> getFunctions() {
		return functions;
	}
	
	public ArrayList<InstanceVariable> getInstanceVariables(){
		return instanceVariables;
	}
	
	public ArrayList<GenericClass> getClassAssociates(){
		return associates;
	}


}