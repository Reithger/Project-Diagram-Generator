package analysis.language.actor;

import java.util.ArrayList;
import java.util.HashMap;

import analysis.language.component.Function;


public class GenericInterface extends GenericDefinition{

	private ArrayList<Function> functions;
	private ArrayList<GenericClass> associates;	//solid line, empty arrowhead
	
	public GenericInterface(String inName, String inContext) {
		super(inName, inContext);
		functions = new ArrayList<Function>();
	}
	
	public void addFunction(Function in) {
		functions.add(in);
	}
	
	public void addAssociation(GenericClass in) {
		associates.add(in);
	}
	
	@Override
	public String generateDot(int val) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateRelationships(HashMap<String, Integer> ref) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
