package analysis.language.component;

import java.util.ArrayList;

public class Function extends ClassComponent{

//---  Instance Variables   -------------------------------------------------------------------
	
	private ArrayList<Argument> arguments;
	private boolean isAbstract;	//italics
	private boolean isStatic;	//underline
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Function(String vis, String nom, ArrayList<Argument> arg, String ret) {
		super(ret, nom, vis);
		arguments = arg;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setAbstract(boolean in) {
		isAbstract = in;
	}
	
	public void setStatic(boolean in) {
		isStatic = in;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public ArrayList<Argument> getArguments(){
		return arguments;
	}

	public boolean getAbstract() {
		return isAbstract;
	}
	
	public boolean getStatic() {
		return isStatic;
	}
	
}
