package image;

public class DotComponent {

	private final static String[] BAD_CHARACTERS_LABELS = new String[] {"<", ">"};
	private final static String[] GOOD_CHARACTERS_LABELS = new String[] {"&lt;", "&gt;"};
	
	public static String dotFunction(String vis, String name, String type, String[] argName, String[] argType, boolean isAbstract, boolean isStatic) {
		String out = vis + name + "(";
		for(int i = 0; i < argName.length; i++){
			out += dotArgument(argName[i], argType[i]) + (i + 1 < argName.length ? ", " : "");
		}
		out += ")";
		String ret = type;

		if(ret != null) {
			out += " : " + ret;
		}
		
		out = fixForDot(out);
		
		if(isAbstract) {
			out = "<u>" + out + "</u>";
		}
		if(isStatic) {
			out = "<i>" + out + "</i>";
		}
		return out;
	}
	
	public static String dotArgument(String nom, String typ) {
		return nom + " : " + typ;
	}
	
	public static String dotInstanceVariable(String vis, String name, String type, boolean isStatic) {
		String out = vis + name + " : " + type;
		out = fixForDot(out);
		if(isStatic) {
			out = "<i>" + out + "</i>";
		}
		return out;
	}
	
	private static String fixForDot(String in) {
		for(int i = 0; i < BAD_CHARACTERS_LABELS.length; i++) {
			in = in.replaceAll(BAD_CHARACTERS_LABELS[i], GOOD_CHARACTERS_LABELS[i]);
		}
		return in;
	}
}
