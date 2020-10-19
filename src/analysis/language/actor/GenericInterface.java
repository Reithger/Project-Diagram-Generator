package analysis.language.actor;

import java.util.ArrayList;
import java.util.HashMap;

import analysis.language.component.Function;


public class GenericInterface implements GenerateDot {

	/** Class name, pretty standard*/
	private String name;
	/** This is the package hierarchy that contains this Class*/
	private String context;
	
	private ArrayList<Function> functions;
	private ArrayList<GenericClass> associates;	//solid line, empty arrowhead
	
	public GenericInterface(String inName, String inContext) {
		name = inName;
		context = inContext;
		functions = new ArrayList<Function>();
	}
	
	public void addFunction(Function in) {
		functions.add(in);
	}
	
	public void addAssociation(GenericClass in) {
		associates.add(in);
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String generateDot(int val) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateAssociations(HashMap<String, Integer> ref) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
