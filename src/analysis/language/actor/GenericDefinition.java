package analysis.language.actor;

import java.util.ArrayList;

import analysis.language.component.Constructor;
import analysis.language.component.Function;

public abstract class GenericDefinition implements Comparable<GenericDefinition>{

	private String name;
	private String context;
	private ArrayList<GenericDefinition> associates;
	private ArrayList<Function> functions;
	
	public GenericDefinition(String inName, String inContext) {
		name = inName;
		context = inContext;
		associates = new ArrayList<GenericDefinition>();
		functions = new ArrayList<Function>();
	}
	
	public void addAssociation(GenericDefinition ref) {
		if(ref == null) {
			return;
		}
		associates.add(ref);
	}

	public ArrayList<Function> getFunctions() {
		return functions;
	}
	
	public void addFunction(Function in) {
		functions.add(in);
	}
	
	public void addConstructor(Constructor in) {
		functions.add(in);
	}

	public String getName() {
		return name;
	}
	
	public ArrayList<GenericDefinition> getClassAssociates(){
		return associates;
	}
	
	public String getContext() {
		return context;
	}
	
	public String[] getContextArray() {
		return context.split("\\.");
	}
	
	public String getFullName() {
		return getContext() + "/" + getName();
	}
	
	public boolean hasAssociate(GenericDefinition gc) {
		for(GenericDefinition c : associates) {
			if(c.compareTo(gc) == 0) {
				return true;
			}
		}
		return false;
	}

//---  Mechanics   ----------------------------------------------------------------------------

	@Override
	public int compareTo(GenericDefinition o) {
		return getFullName().compareTo(o.getFullName());
	}
	
	public boolean equals(GenericDefinition gd) {
		if(gd == null)
			return false;
		return getFullName().equals(gd.getFullName());
	}
	
}
