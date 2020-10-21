package image;

import java.util.ArrayList;
import java.util.HashMap;

import analysis.language.actor.GenericClass;
import analysis.language.actor.GenericDefinition;
import analysis.language.actor.GenericInterface;
import analysis.language.component.Function;
import analysis.language.component.InstanceVariable;
import explore.Cluster;
import explore.Explore;

public class DotProcess {

	private static final String HTML_LT = "&lt;";
	private static final String HTML_GT = "&gt;";
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static Explore explore;
	private static HashMap<String, Integer> reference;
	private static int count;
	
//---  Static Assignment   --------------------------------------------------------------------
	
	public static void setProject(Explore in) {
		explore = in;
	}

//---  Operations   ---------------------------------------------------------------------------

	public static String generateDot() {
		reference = new HashMap<String, Integer>();
		count = 0;
		String out = processInitiation();

		out += processClasses();
		
		out += processInterfaces();

		out = processClusters(out, explore.getClusterRoot(), 1, 30, 1);
		
		out += processAssociations();
		
		out += "\n}";

		return out;
	}
	
	private static String processInitiation() {	//Can manipulate here for adjusting draw settings
		String out = "digraph G {\n";
		out += 	"\tnode[shape=record,style=filled,fillcolor=gray95];\r\n" + 
				"\tedge[concentrate=true];\n" +
				"\tgraph[splines = ortho, ranksep = 1, ratio = fill, color=blue];\n" +
				"\trankdir = TB;\n";	//splines = ortho, nodesep = 1 for straight lines, looks rough, let user change how lines are displayed
		out += "\n";
		return out;
	}
	
	private static String processClasses() {
		String out = "";
		for(GenericClass gc : explore.getClasses()) {
			reference.put(gc.getFullName(), count++);
			out += generateClassDot(gc, reference.get(gc.getFullName()));
		}
		return out;
	}
	
	private static String processInterfaces() {
		String out = "";
		for(GenericInterface gc : explore.getInterfaces()) {
			reference.put(gc.getFullName(), count++);
			out += generateInterfaceDot(gc, reference.get(gc.getFullName()));
		}
		return out;
	}
	
	private static String processAssociations() {
		String out = "";
		for(GenericClass c : explore.getClasses()) {
			out += generateDotClassAssociations(c);
		}
		for(GenericInterface c : explore.getInterfaces()) {
			out += generateDotInterfaceAssociations(c);
		}
		return out;
	}
	
	//-- GenericClass  ----------------------------------------

	public static String generateClassDot(GenericClass gc, int val) {
		String pref = "\tn" + val + " [label = <{";
		String out = formDotName(gc) + "|";
		ArrayList<InstanceVariable> ivs =  gc.getInstanceVariables();
		for(int i = 0; i < ivs.size(); i++) {
			InstanceVariable iv = ivs.get(i);
			out += DotComponent.dotInstanceVariable(iv) + (i + 1 < ivs.size() ? "<BR/>" : "");
		}
		out += "|";
		ArrayList<Function> funcs = gc.getFunctions();
		for(int i = 0; i < funcs.size(); i++) {
			Function func = funcs.get(i);
			out += DotComponent.dotFunction(func) + (i + 1 < funcs.size() ? "<BR/>" : "");
		}
		String post = "}>];\n";
		return pref + out + post;
	};
	
	public static String generateDotClassAssociations(GenericClass gc) {
		int val = reference.get(gc.getFullName());
		String out = "";
		if(gc.getInheritance() != null) {
			out = "\tn" + val + " -> n" + reference.get(gc.getInheritance().getFullName()) + "[arrowhead=onormal];\n";
		}
		out += generateDotAssociations(gc);
		for(GenericInterface i : gc.getRealizations()) {
			out += "\tn" + val + " -> n" + reference.get(i.getFullName()) + "[arrowhead=onormal, style=dashed];\n";
		}
		return out;
	}
	
	private static String formDotName(GenericClass gc) {
		String out = gc.getName();
		if(gc.getAbstract()){
			out = "<i>" + out + "</i>";
		}
		return out;
	}

	//-- GenericInterface  ------------------------------------

	public static String generateInterfaceDot(GenericInterface gi, int val) {
		String pref = "\tn" + val + " [label = <{";
		String out = formInterfaceName() + "<BR/>" + gi.getName() + "|";
		out += "|";
		ArrayList<Function> funcs = gi.getFunctions();
		for(int i = 0; i < funcs.size(); i++) {
			Function func = funcs.get(i);
			out += DotComponent.dotFunction(func) + (i + 1 < funcs.size() ? "<BR/>" : "");
		}
		String post = "}>];\n";
		return pref + out + post;
	}

	private static String formInterfaceName() {
		return HTML_LT + HTML_LT + "interface" + HTML_GT + HTML_GT ;
	}
	
	public static String generateDotInterfaceAssociations(GenericInterface gi) {
		return generateDotAssociations(gi);
	}
	
	//-- GenericDefinition  -----------------------------------

	protected static String generateDotAssociations(GenericDefinition gd) {
		String out = "";
		for(GenericDefinition c : gd.getClassAssociates()) {
			out += "\tn" + reference.get(gd.getFullName()) + " -> n" + reference.get(c.getFullName());
			if(c.hasAssociate(gd)) {
				out += "[arrowhead=none]";
			}
			else {
				out += "[arrowhead=normal]";
			}
			out += ";\n";
		}
		return out;
	}

	//-- Clusters  --------------------------------------------
	
	private static String processClusters(String out, Cluster next, int depth, int fontSize, int penWidth) {
		if(next == null)
			return out;
		String address = next.getAddress();
		out += tabBuffer(depth) + "subgraph cluster_" + address.replaceAll("\\.",  "_") + "{\n";
		out += tabBuffer(depth + 1) + "label = \"" + address + "\";\n";
		out += tabBuffer(depth + 1) + "fontsize = " + fontSize + ";\n";
		out += tabBuffer(depth + 1) + "penwidth = " + penWidth + ";\n";
		for(GenericDefinition gd : next.getComponents()) {
			out += tabBuffer(depth + 1) + "n" + reference.get(gd.getFullName()) + ";\n";
		}
		for(Cluster c : next.getChildren()) {
			out = processClusters(out, c, depth + 1, fontSize - 4, penWidth + 1);
		}
		out += tabBuffer(depth) + "}\n";
		return out;
	}

	private static String tabBuffer(int in) {
		String out = "";
		while(in-- > 0) {
			out += "\t";
		}
		return out;
	}
	
}
