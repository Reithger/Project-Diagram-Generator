package image;

import java.util.ArrayList;

import analysis.language.component.Argument;
import analysis.language.component.Function;
import analysis.language.component.InstanceVariable;

public class DotComponent {

	private final static String[] BAD_CHARACTERS_LABELS = new String[] {"<", ">"};
	private final static String[] GOOD_CHARACTERS_LABELS = new String[] {"&lt;", "&gt;"};
	
	public static String dotFunction(Function f) {
		String out = f.getVisibility() + f.getName() + "(";
		ArrayList<Argument> args = f.getArguments();
		for(int i = 0; i < args.size(); i++){
			Argument arg = args.get(i);
			out += dotArgument(arg) + (i + 1 < args.size() ? ", " : "");
		}
		out += ")";
		String ret = f.getType();

		if(ret != null) {
			out += " : " + ret;
		}
		
		out = fixForDot(out);
		
		if(f.getAbstract()) {
			out = "<u>" + out + "</u>";
		}
		if(f.getStatic()) {
			out = "<i>" + out + "</i>";
		}
		return out;
	}
	
	public static String dotArgument(Argument arg) {
		return arg.getName() + " : " + arg.getType();
	}
	
	public static String dotInstanceVariable(InstanceVariable iv) {
		String out = iv.getVisibility() + iv.getName() + " : " + iv.getType();
		out = fixForDot(out);
		if(iv.getStatic()) {
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
