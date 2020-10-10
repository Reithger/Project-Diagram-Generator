package analysis.language;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import analysis.language.Class;

public class JavaClass extends Class {

//---  Constants   ----------------------------------------------------------------------------
	
	private final static String TYPE = ".java";
	private final static String IMPORT_PHRASE = "import ";
	private final static String REGEX_PARSE_BRACKET_OPEN = "\"[^\"\\}]*\\}[^\"]*\"";
	private final static String REGEX_PARSE_BRACKET_CLOSED = "\"[^\"\\{]*\\{[^\"]*\"";
	private final static String REGEX_PARSE_SEMICOLON = "\"[^\"\\;]*\\;[^\"]*\"";
	private final static String[] KEY_BUFFER_PHRASES = new String[] {"(", ")"};
	private final static String[] REMOVE_TERMS = new String[] {"volatile", "abstract", "static"};

//---  Constructors   -------------------------------------------------------------------------
	
	public JavaClass(File in, String root) {
		super(in, root);
	}

//---  Operations   ---------------------------------------------------------------------------
	
	@Override
	public void process(HashMap<String, Class> reference) {
		int depth = 0;
		boolean skipNextFunction = false;
		for(String s : getFileContents()) {
			if(s.contains("//"))
				s = s.substring(0, s.indexOf("//"));
			s = s.trim().replaceAll(";", "");
			depth -= s.replaceAll(REGEX_PARSE_BRACKET_OPEN, "").replaceAll("[^}]", "").length();
			if(depth == 0) {
				if(s.matches(IMPORT_PHRASE + ".*")) {
					String[] imp = s.split("\\s++");
					if(reference.get(imp[1]) != null) {
						this.addClassAssociate(reference.get(imp[1]));
					}
				}
				else {
					if(s.contains("extends")) {
						String[] use = cleanInput(s);
						int posit = indexOf(use, "extends");
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
			}
			else if(depth == 1) {
				String use = s;
				if(testInstanceVariable(use)) {
					processInstanceVariable(use);
				}
				else if(testFunction(use) && !skipNextFunction) {
					processFunction(use);
				}
				else if(use.contains("@Override")) {
					skipNextFunction = true;
				}
				else {
					skipNextFunction = false;
				}
			}
			depth += s.replaceAll(REGEX_PARSE_BRACKET_CLOSED, "").replaceAll("[^{]", "").length();
		}
	}
	
	private void processInstanceVariable(String in) {
		boolean underline = false;
		if(in.contains("static")) {
			underline = true;	//TODO: Do the formatting here
		}
		String[] cont = cleanInput(in);
		String vis = processVisibility(cont[0]);
		String typ = compileType(cont, 1);
		addInstanceVariable(compileInstanceVariable(vis, cont[cont.length - 1], typ, underline));
	}
	
	private void processFunction(String in) {
		if(!getStatusPrivate() && in.contains("private")) {
			return;
		}
		boolean stat = false;
		boolean abs = false;
		if(in.contains("static")) {
			stat = true;	//TODO: Do the formatting here
		}
		if(in.contains("abstract")) {
			abs = true;
		}
		String[] cont = cleanInput(in);
		int argStart = indexOf(cont, "(");
		String vis = processVisibility(cont[0]);
		String name = cont[argStart-1];
		String ret = compileType(cont, 1);
		ArrayList<String> args = new ArrayList<String>();
		for(int i = argStart + 1; i < cont.length - 2; i += 2) {
			String type = compileType(cont, i);
			i += type.replaceAll("[^,]", "").length();
			String nom = cont[i + 1].replaceAll(",", "");
			args.add(nom);
			args.add(type);
		}
		if(ret.equals("")) {
			addFunction(compileConstructor(vis, name, compileArguments(args)));
		}
		else {
			addFunction(compileFunction(vis, name, ret, compileArguments(args), stat, abs));
		}
	};
	
//---  Tester Methods   -----------------------------------------------------------------------
	
	private boolean testInstanceVariable(String in) {
		if(in.contains("="))
			in = in.substring(0, in.indexOf("="));
		return getStatusInstanceVariable() && in.matches("\\s*[private,public,protected][^{]*") && !in.contains("final") && !in.contains("abstract") && !in.contains("(");
	}
	
	private boolean testFunction(String in) {
		return getStatusFunction() && in.matches("\\s*[private,public,protected].*") && !in.contains(" new ") && in.contains("(") && !in.contains("=");
	}

//---  Support Methods   ----------------------------------------------------------------------
	
	private String[] cleanInput(String in) {
		String out = in.replaceAll("  ", " ").trim();
		for(String s : REMOVE_TERMS) {
			out = out.replaceAll(" " + s + " ", " ");
		}
		for(String s : KEY_BUFFER_PHRASES) {
			out = bufferCharacter(out, "\\" + s);
		}
		out = out.replaceAll("\\.\\.\\.", "... ");
		while(out.contains(" ...")) {
			out = out.replaceAll(" \\.\\.\\.", "...");
		}
		String[] fin = out.split("\\s+");
		for(int i = 0; i < fin.length; i++) {
			fin[i] = fin[i].trim();
		}
		return fin;
	}
	
	private String bufferCharacter(String out, String in) {
		while(out.contains(in + " ")) {
			out = out.replaceAll(in + " ", in);
		}
		while(out.contains(" " + in)) {
			out = out.replaceAll(" " + in, in);
		}
		return out.replaceAll(in, " " + in + " ");
	}
	
	private String compileType(String[] line, int start) {
		String out = "";
		while(line[start].contains(",")) {
			out += line[start++] + " ";
		}
		return out + line[start];
	}
	
	private String processVisibility(String in) {
		switch(in) {
			case "public":
				return "+";
			case "private":
				return "-";
			case "protected":
				return "#";
			default:
				return "?";
		}
	}

//---  Getter Methods   -----------------------------------------------------------------------
	
	public String getType() {
		return TYPE;
	}

//---  Mechanics   ----------------------------------------------------------------------------
	
	private int indexOf(String[] arr, String key) {
		for(int i = 0; i < arr.length; i++) {
			if(arr[i].equals(key)) {
				return i;
			}
		}
		return -1;
	}

}
