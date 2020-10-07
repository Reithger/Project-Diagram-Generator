package analysis;

import java.io.File;
import java.util.ArrayList;
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
		ArrayList<String> contents = getFileContents();
		boolean skipNextFunction = false;
		for(int i = 0; i < contents.size(); i++) {
			String s = contents.get(i);
			depth -= s.replaceAll("\"[^\"\\}]*\\}[^\"]*\"", "").replaceAll("[^}]", "").length();
			if(s.matches("import.*")) {
				String[] imp = s.replaceAll(";", "").split("\\s++");
				if(reference.get(imp[1]) != null) {
					this.addClassAssociate(reference.get(imp[1]));
				}
			}
			else if(depth == 1) {
				String use = s;
				if(s.contains(";"))
					use = s.substring(0, s.lastIndexOf(";"));
				if(testInstanceVariable(use)) {
					addInstanceVariable(processInstanceVariable(use));
				}
				else if(testFunction(use) && !skipNextFunction) {
					addFunction(processFunction(use));
				}
				else if(use.contains("@Override")) {
					skipNextFunction = true;
				}
				else {
					skipNextFunction = false;
				}
			}
			else if(depth == 0){
				if(s.contains("extends")) {
					String[] use = s.trim().split("\\s+");
					int posit = -1;
					for(int j = 0; j < use.length; j++) {
						if(use[j].equals("extends")) {
							posit = j;
						}
					}
					String nom = use[posit+1].replaceAll("\\{", "");
					boolean found = false;
					for(Class cl : this.getClassAssociates()) {
						if(stripContext(cl.getName()).equals(nom)) {
							found = true;
						}
					}
					if(!found) {
						String outN = getContext(getName()) + "." +  nom;
						if(reference.get(outN) != null) {
							addClassAssociate(reference.get(outN));
						}
					}
				}
				if(s.contains("implements")) {
					
				}
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
		boolean underline = false;
		if(in.contains("static")) {
			underline = true;	//TODO: Do the formatting here
		}
		String out = in.replaceAll(" static ", " ").replaceAll(" volatile ", " ").replaceAll("  ", " ").trim();
		while(out.contains("( ")) {
			out = out.replaceAll("( ", "(");
		}
		while(out.contains(" )")) {
			out = out.replaceAll(" )", ")");
		}
		out = out.replaceAll("\\(", " ( ");
		out = out.replaceAll("\\)", " ) ");
		String[] cont = out.split("\\s+");
		int argStart = -1;
		for(int i = 0; i < cont.length; i++) {
			cont[i] = cont[i].trim();
			if(cont[i].contains("(")) {
				argStart = i;
			}
		}
		String vis = cont[0].matches("\\s*private.*") ? "-" : cont[0].matches("\\s*public.*") ? "+" : "#";
		String name = cont[argStart-1];
		String ret = (argStart - 1 == 1 ? "" : cont[1]);
		for(int i = 2; i < argStart - 1; i++) {
			ret += " " + cont[i];
		}
		
		name += "(";
		for(int i = argStart + 1; i < cont.length - 2; i += 2) {
			if(cont[i].equals(")")) {
				break;
			}
			String type = cont[i];
			while(cont[i].contains(",")) {
				type += " " + cont[++i];
			}
			name += cont[i + 1].replaceAll(",", "") + " : " + type + (i + 2 < cont.length - 2 ? ", " : "");
		}
		name += ")";
		String tog = vis + name + " : " + ret;
		tog = tog.replaceAll("\\<", "\\\\<");
		tog = tog.replaceAll("\\>", "\\\\>");
		tog = tog.replaceAll("\\[", "\\\\[");
		tog = tog.replaceAll("\\]", "\\\\]");
		tog = tog.replaceAll("\\(", "\\\\(");
		tog = tog.replaceAll("\\)", "\\\\)");
		tog = tog.replaceAll("\\:", "\\\\:");
		return tog;
	};
	
	public String getType() {
		return TYPE;
	}

}
