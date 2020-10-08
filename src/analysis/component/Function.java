package analysis.component;

import java.util.ArrayList;
import analysis.DotPrintable;
import analysis.component.Argument;

public class Function extends ClassComponent implements DotPrintable{

//---  Instance Variables   -------------------------------------------------------------------
	
	private String visibility;
	private String name;
	private String returnType;
	private ArrayList<Argument> arguments;
	private boolean isAbstract;	//italics
	private boolean isStatic;	//underline
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Function(String vis, String nom, ArrayList<Argument> arg, String ret) {
		visibility = vis;
		name = nom;
		arguments = arg;
		returnType = ret;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setAbstract(boolean in) {
		isAbstract = in;
	}
	
	public void setStatic(boolean in) {
		isStatic = in;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	@Override
	public String getDot() {
		String out = visibility + name + "(";
		for(int i = 0; i < arguments.size(); i++){
			out += arguments.get(i).getDot() + (i + 1 < arguments.size() ? ", " : "");
		}
		out += ")" + getReturnType();

		out = fixForDot(out);
		
		if(isAbstract) {
			out = "<u>" + out + "</u>";
		}
		if(isStatic) {
			out = "<i>" + out + "</i>";
		}
		return out;
	}

	protected String getReturnType() {
		return " : " + returnType;
	}
	
}
