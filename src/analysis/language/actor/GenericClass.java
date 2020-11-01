package analysis.language.actor;

import java.util.ArrayList;

import analysis.language.component.InstanceVariable;

/**
 * The platonic ideal of a Class
 * @author Ada Clevinger
 *
 */

public class GenericClass extends GenericDefinition{

//---  Instance Variables   -------------------------------------------------------------------

	private boolean isAbstract;
	
	private GenericDefinition inheritance;
	
	private ArrayList<GenericDefinition> realizations;	//dotted line, empty arrowhead
	
	private ArrayList<InstanceVariable> instanceVariables;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public GenericClass(String inName, String inContext) {
		super(inName, inContext);
		instanceVariables = new ArrayList<InstanceVariable>();
		inheritance = null;
		realizations = new ArrayList<GenericDefinition>();
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void addInstanceVariable(InstanceVariable in) {
		instanceVariables.add(in);
	}
	
	public void addInstanceVariable(String vis, String nom, String typ, boolean isStatic) {
		InstanceVariable iv = new InstanceVariable(vis, nom, typ);
		iv.setStatic(isStatic);
		addInstanceVariable(iv);
	}
	
	public void addRealization(GenericDefinition in) {
		realizations.add(in);
	}
	
	public void setAbstract(boolean in) {
		isAbstract = in;
	}
	
	public void setInheritance(GenericDefinition ref) {
		inheritance = ref;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	//-- Instance Variables  ----------------------------------
	
	public ArrayList<InstanceVariable> getInstanceVariables() {
		return instanceVariables;
	}
	
	public InstanceVariable getInstanceVariableAt(int index) {
		return getInstanceVariables().get(index);
	}
	
	public int getNumberInstanceVariables() {
		return getInstanceVariables().size();
	}
	
	public String getInstanceVariableTypeAt(int index) {
		return getInstanceVariableAt(index).getType();
	}
	
	public String getInstanceVariableVisibilityAt(int index) {
		return getInstanceVariableAt(index).getVisibility();
	}
	
	public String getInstanceVariableNameAt(int index) {
		return getInstanceVariableAt(index).getName();
	}

	public boolean getInstanceVariableStaticAt(int index) {
		return getInstanceVariableAt(index).getStatic();
	}
	
	//-- GenericClass  ----------------------------------------
	
	public GenericDefinition getInheritance() {
		return inheritance;
	}
	
	public ArrayList<GenericDefinition> getRealizations(){
		return realizations;
	}

	public boolean getAbstract() {
		return isAbstract;
	}

}