package analysis.component;

import analysis.DotPrintable;

public class InstanceVariable extends ClassComponent implements DotPrintable{
	
	private String visibility;
	private String name;
	private String type;
	
	public InstanceVariable(String vis, String nom, String typ) {
		visibility = vis;
		name = nom;
		type = typ;
	}
	
	@Override
	public String getDot() {
		String out = visibility + name + " : " + type;
		return fixForDot(out);
	}
	
}
