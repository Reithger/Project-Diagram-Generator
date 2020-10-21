package analysis.language.component;

public class InstanceVariable extends ClassComponent{
	
	private boolean isStatic;
	
	public InstanceVariable(String vis, String nom, String typ) {
		super(typ, nom, vis);
	}
	
	public void setStatic(boolean in) {
		isStatic = in;
	}
	
	public boolean getStatic() {
		return isStatic;
	}
	
}
