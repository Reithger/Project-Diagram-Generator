package analysis.language.component;

import java.util.ArrayList;

public class Constructor extends Function{

	public Constructor(String vis, String nom, ArrayList<Argument> arg) {
		super(vis, nom, arg, null);
	}
	
	@Override
	public String getReturnType() {
		return "";
	}
	
}
