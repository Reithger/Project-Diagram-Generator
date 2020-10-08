package analysis.component;

import analysis.DotPrintable;

public class Argument implements DotPrintable{

	private String name;
	private String type;
	
	public Argument(String nom, String typ) {
		name = nom;
		type = typ;
	}
	
	@Override
	public String getDot() {
		return name + " : " + type;
	}
	
}
