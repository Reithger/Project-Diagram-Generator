package analysis.language.file;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import analysis.language.actor.GenericClass;
import analysis.language.actor.GenericInterface;

public class JavaFile extends GenericFile {

//---  Constants   ----------------------------------------------------------------------------
	
	private final static String TYPE = ".java";
	private final static String IMPORT_PHRASE = "import ";
	private final static String REGEX_PARSE_BRACKET_OPEN = "\"[^\"\\}]*\\}[^\"]*\"";
	private final static String REGEX_PARSE_BRACKET_CLOSED = "\"[^\"\\{]*\\{[^\"]*\"";
	private final static String REGEX_PARSE_SEMICOLON = "\"[^\"\\;]*\\;[^\"]*\"";
	private final static String[] KEY_BUFFER_PHRASES = new String[] {"(", ")"};
	private final static String[] REMOVE_TERMS = new String[] {"volatile", "abstract", "static"};

//---  Constructors   -------------------------------------------------------------------------
	
	public JavaFile(File in, String root) {
		super(in, root);
	}

//---  Operations   ---------------------------------------------------------------------------
	
	@Override
	protected String findName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected ArrayList<String> preProcess(String in){
		ArrayList<String> out = new ArrayList<String>();
		in = in.replaceAll("\n", " ").replaceAll("  ", " ").replaceAll("\"[^\"]\"", "").replaceAll(";", ";\n").replaceAll("{", "{\n").replaceAll("}", "\n}");
		String[] parsed = in.split("\n");
		for(String s : parsed)
			out.add(s.trim());
		return out;
	}
	
	@Override
	public boolean isClassFile() {
		for(String s : getFileContents()) {
			if(s.matches("public class [^{]{")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public GenericClass processClass(HashMap<String, GenericClass> reference) {
		GenericClass out = new GenericClass(getName(), getContext());
		int depth = 0;
		boolean skipNextFunction = false;
		for(String s : getFileContents()) {		//TODO: Instead of one pass through, do multiple while looking for specific things. Cleaner approach, if not as fast kinda
			if(s.contains("//"))
				s = s.substring(0, s.indexOf("//"));
			s = s.trim().replaceAll(";", "");
			depth -= s.replaceAll(REGEX_PARSE_BRACKET_OPEN, "").replaceAll("[^}]", "").length();
			if(depth == 0) {
				if(s.matches(IMPORT_PHRASE + ".*")) {
					String[] imp = s.split("\\s++");
					if(reference.get(imp[1]) != null) {
						out.addClassAssociate(reference.get(imp[1]));
					}
				}
				else {
					if(containsHierarchyData(s)) {
						ArrayList<String> classes = getHierarchicalClasses(s);
						
						for(GenericClass cl : out.getClassAssociates()) {
							String className = stripContext(cl.getName());
							if(classes.contains(className)) {
								classes.remove(className);
							}
						}
						
						for(String relat : classes) {
							String outN = getContext(getName()) + "." +  relat;
							if(reference.get(outN) != null) {
								out.addClassAssociate(reference.get(outN));
							}
						}
						
					}
					if(s.contains("abstract")) {
						out.setAbstract(true);
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
		return out;
	}
	
	@Override
	public boolean isInterfaceFile() {
		for(String s : getFileContents()) {
			if(s.matches("public interface [^{]{")) {
				return true;
			}
		}
		return false;
	}
	
	public GenericInterface processInterface(HashMap<String, GenericClass> reference) {
		
	}
	
	private boolean containsHierarchyData(String line) {
		return line.contains("extends") || line.contains("implements");
	}
	
	private ArrayList<String> getHierarchicalClasses(String line) {
		ArrayList<String> out = new ArrayList<String>();
		
		String[] use = cleanInput(line);
		int posit = indexOf(use, "extends");
		if(posit != -1) {
			out.add(use[posit+1].replaceAll("\\{", ""));
		}
		
		posit = indexOf(use, "implements");
		
		if(posit != -1) {
			while(++posit < use.length) {
				out.add(use[posit].replaceAll("\\{", ""));
			}
		}
		
		return out;
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
