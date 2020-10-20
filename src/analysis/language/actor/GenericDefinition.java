package analysis.language.actor;

public abstract class GenericDefinition implements Comparable<GenericDefinition>, GenerateDot{

	private String name;
	
	private String context;
	
	public GenericDefinition(String inName, String inContext) {
		name = inName;
		context = inContext;
	}
	
	public String getName() {
		return name;
	}
	
	public String getContext() {
		return context;
	}
	
	public String[] getContextArray() {
		return context.split("\\.");
	}

//---  Mechanics   ----------------------------------------------------------------------------

	@Override
	public int compareTo(GenericDefinition o) {
		int res = getName().compareTo(o.getName());
		if(res == 0) {
			res = getContext().compareTo(o.getContext());
		}
		return res;
	}
	
}
