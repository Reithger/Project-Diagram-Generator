package analysis.component;

import analysis.DotPrintable;

public class InstanceVariable extends ClassComponent implements DotPrintable{
	
	private String visibility;
	private String name;
	private String type;
	private boolean isStatic;
	
	public InstanceVariable(String vis, String nom, String typ) {
		visibility = vis;
		name = nom;
		type = typ;
	}
	
	public void setStatic(boolean in) {
		isStatic = in;
	}
	
	@Override
	public String getDot() {
		String out = visibility + name + " : " + type;
		out = fixForDot(out);
		if(isStatic) {
			out = "<i>" + out + "</i>";
		}
		return out;
	}
	
}
