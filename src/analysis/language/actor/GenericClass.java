package analysis.language.actor;

import java.util.ArrayList;

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
	
	private ArrayList<InstanceVariable> instanceVariables;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public GenericClass(String inName, String inContext) {
		super(inName, inContext);
		instanceVariables = new ArrayList<InstanceVariable>();
		inheritance = null;
		realizations = new ArrayList<GenericInterface>();
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
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

	public ArrayList<InstanceVariable> getInstanceVariables(){
		return instanceVariables;
	}
	
}