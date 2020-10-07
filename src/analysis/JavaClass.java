package analysis;

import java.io.File;
import java.util.HashMap;

public class JavaClass extends Class {

	private final static String TYPE = ".java";
	private final static String IMPORT_PHRASE = "import ";
	
	public JavaClass(File in, String root) {
		super(in, root);
	}

	@Override
	public void process(HashMap<String, Class> reference) {
		int depth = 0;
		for(String s : this.getFileContents()) {
			depth -= s.replaceAll("\"[^\"\\}]*\\}[^\"]*\"", "").replaceAll("[^}]", "").length();
			if(s.matches("import.*")) {
				String mtch = s.substring(IMPORT_PHRASE.length()).replaceAll(";", "");
				if(reference.get(mtch) != null) {
					this.addClassAssociate(reference.get(mtch));
				}
			}
			else if(depth == 1) {
				String use = s;
				if(s.contains(";"))
					use = s.substring(0, s.lastIndexOf(";"));
				if(testInstanceVariable(use)) {
					addInstanceVariable(processInstanceVariable(use));
				}
				//else if(testFunction(use)) {		TODO: Fix this
				//	addFunction(use);
				//}
			}
			depth += s.replaceAll("\"[^\"\\{]*\\{[^\"]*\"", "").replaceAll("[^{]", "").length();
		}
	}
	
	private boolean testInstanceVariable(String in) {
		if(in.contains("="))
			in = in.substring(0, in.indexOf("="));
		return in.matches("\\s*[private,public,protected][^{]*") && !in.contains("final") && !in.contains("abstract") && !in.contains("(");
	}
	
	private boolean testFunction(String in) {
		return in.matches("\\s*[private,public,protected][^{]*\\{.*") && in.contains("(") && !in.contains("=");
	}
	
	private String processInstanceVariable(String in) {
		boolean underline = false;
		if(in.contains("static")) {
			underline = true;	//TODO: Do the formatting here
		}
		String out = in.replaceAll(" static ", " ").replaceAll(" volatile ", " ").trim();
		if(out.contains("="))
			out = out.substring(0, out.indexOf("="));

		String[] cont = out.split("\\s+");
		String vis = out.matches("\\s*private.*") ? "-" : out.matches("\\s*public.*") ? "+" : "#";
		String typ = "";
		for(int i = 1; i < cont.length-1; i++) {
			typ += cont[i] + (i + 1 < cont.length-1 ? " " : "");
		}
		String tog = vis + " " + cont[cont.length-1] + ": " + typ;
		tog = tog.replaceAll("\\<", "\\\\<");
		tog = tog.replaceAll("\\>", "\\\\>");
		tog = tog.replaceAll("\\[", "\\\\[");
		tog = tog.replaceAll("\\]", "\\\\]");
		return tog;
	}
	
	private String processFunction(String in) {
		return "";		//TODO: It's late and this is a lot of work, I'll fix it later.
		/*
		boolean underline = false;
		if(in.contains("static")) {
			underline = true;		//TODO: Do the formatting here
		}
		String out = in.replaceAll(" static ", " ").replaceAll(" volatile ", " ").replaceAll("  ", " ").trim();
		while(out.contains("( ")) {
			out = out.replaceAll("( ", "(");
		}
		while(out.contains(" )")) {
			out = out.replaceAll(" )", ")");
		}
		out = out.replaceAll("(", " ( ");
		out = out.replaceAll(")", " ) ");
		out = out.replaceAll(",", " ");
		String[] cont = out.split(" ");
		int argStart = -1;
		for(int i = 0; i < cont.length; i++) {
			if(cont[i].contains("(")) {
				argStart = i;
				break;
			}
		}
		String vis = cont[0].matches("\\s*private.*") ? "-" : cont[0].matches("\\s*public.*") ? "+" : "#";
		String name = cont[argStart-1];
		String ret = cont[1];
		
		name += "(";
		
		for(int i = argStart + 1; i < cont.length - 1; i += 2) {
			name += cont[i] + " : " + cont[i + 1];
		}
		
		return vis + name + " : " + ret;
		*/
	};
	
	public String getType() {
		return TYPE;
	}

}
