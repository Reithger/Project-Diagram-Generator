package analysis.process.file;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import analysis.language.actor.GenericDefinition;

public class JavaFile extends GenericFile {

//---  Constants   ----------------------------------------------------------------------------
	
	private final static String TYPE = ".java";
	private final static String[] KEY_BUFFER_PHRASES = new String[] {"(", ")"};
	private final static String[] REMOVE_TERMS = new String[] {"volatile", "abstract", "static"};
	private final static String REGEX_VISIBILITY_FILE_DEF = "((public|private|protected) )?";

//---  Constructors   -------------------------------------------------------------------------
	
	public JavaFile(File in, String root) {
		super(in, root);
	}
	
	public JavaFile(ArrayList<String> lines, String context) {
		super(lines, context);
	}

//---  Operations   ---------------------------------------------------------------------------

	@Override
	public boolean detectInternalClasses() {
		int counter = 0;
		for(String s : getFileContents()) {
			if(isFileDefinition(s)) {
				counter++;
			}
		}
		return counter > 1;
	}

	@Override
	protected ArrayList<String> preProcess(String in){
		ArrayList<String> out = new ArrayList<String>();
		while(in.contains("\\\\")) {
			in = in.replace("\\\\", "");		//remove instances of \\ (double backslashes) as being redundant to important \" searching
		}
		in = in.replaceAll("\\\\\"", "");		//remove \" String occurrences
		in = in.replaceAll("\"[^\"]*?\"", "\"\"");	//remove String literals
		in = in.replaceAll("//.*?\n", "\n");	//remove comments
		in = in.replaceAll("(?<=@.*)\n", ";\n");	//Buffer @ lines preceding something to be on a separate line
		in = in.replaceAll("\n", " ");			//remove new lines, add space gaps
		in = in.replaceAll("/\\*.*?\\*/", "");	//remove multi-line comments (/* ... */) with non-greedy regex (? symbol) for minimal removal
		in = in.replaceAll("\t", "");			//remove tabs
		in = in.replaceAll("  ", " ");			//shorten whitespace
		in = in.replaceAll(";", ";\n");			//add newlines back in at all ;
		in = in.replaceAll("\\{", "\\{\n");		//add newlines around {
		in = in.replaceAll("\\}", "\n}\n");		//add newlines around }

		in = bufferCharacter(in, "\\{");
		in = bufferCharacter(in, "\\}");
		in = bufferCharacter(in, "\\)");
		in = bufferCharacter(in, "\\(");
		while(in.contains("  ")) {
			in = in.replaceAll("  ", " ");
		}
		in = in.replaceAll("(\n|$) ", "\n");
		String[] parsed = in.trim().split("\n");
		for(String s : parsed) {
			if(s != null && s.trim() != null && !s.trim().equals("")) {
				out.add(s.trim());
			}
		}
		return out;
	}

	private String removeEquals(String in) {
		if(in.contains("=")) {
			return in.substring(in.indexOf("="));
		}
		return in;
	}
	
	private String processImportName(String line) {
		String[] use = cleanInput(line)[1].split("\\.");
		String nom = use[use.length - 1];
		String context = use[0];
		for(int i = 1; i < use.length - 1; i++) {
			context += "." + use[i];
		}
		return formFullName(context, nom);
	}
	
	private String formFullName(String context, String name) {
		return context + "/" + name;
	}
	
	private void processInstanceVariable(String in) {
		in = removeEquals(in);
		boolean underline = false;
		if(in.contains("static")) {
			underline = true;	//TODO: Do the formatting here
		}
		String[] cont = cleanInput(in);
		String vis = processVisibility(cont[0]);
		String typ = compileType(cont, 1);
		addInstanceVariableToClass(vis, cont[cont.length - 1], typ, underline);
	}
	
	private void processFunction(String in) {
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
		String ret = argStart == 2 ? "" : compileType(cont, 1);
		ArrayList<String> argNom = new ArrayList<String>();
		ArrayList<String> argTyp = new ArrayList<String>();
		for(int i = argStart + 1; i < cont.length - 2; i += 2) {
			if(cont[i].equals(")"))
				break;
			String type = compileType(cont, i);
			i += type.replaceAll("[^,]", "").length();
			String nom = cont[i + 1].replaceAll(",", "");
			argNom.add(nom);
			argTyp.add(type);
		}
		if(ret.equals("")) {
			addConstructorToDef(vis, name, argNom, argTyp);
		}
		else {
			addFunctionToDef(vis, name, ret, argNom, argTyp, stat, abs);
		}
	};

	//-- Extraction  ------------------------------------------
	
	@Override
	public ArrayList<GenericFile> extractInternalClasses(){
		ArrayList<GenericFile> out = new ArrayList<GenericFile>();
		String context = getContext();
		ArrayList<String> headerInfo = new ArrayList<String>();
		for(String s : getFileContents()) {
			if(s.matches("import .*")) {
				headerInfo.add(s);
			}
		}
		searchFile(getFileContents(), out, headerInfo, 0, context);
		for(int i = 0; i < out.size(); i++) {
			if(out.get(i).getName() == null) {
				out.remove(i);
				i--;
			}
		}
		return out;
	}
	
	private int searchFile(ArrayList<String> contents, ArrayList<GenericFile> out, ArrayList<String> header, int currLine, String context) {
		if(currLine >= contents.size()) {
			return currLine;
		}
		ArrayList<String> lines = new ArrayList<String>();
		for(String s : header) {
			lines.add(s);
		}
		int dep = 0;
		boolean active = false;
		for(int i = currLine; i < contents.size(); i++) {
			String line = contents.get(i);
			if(isFileDefinition(line) && i != currLine) {
				if(!active) {
					i = searchFile(contents, out, header, i, context);
				}
				else {
					active = true;
				}
			}
			else {
				lines.add(line);
			}
			if(active) {
				if(line.contains("{")) {
					dep++;
				}
				if(line.contains("}")) {
					dep--;
				}
				if(dep == 0) {
					out.add(new JavaFile(lines, context));
					return i + 1;
				}
			}
		}
		out.add(new JavaFile(lines, context));
		return contents.size();
	}
	
	@Override
	protected boolean extractAbstract() {
		for(String line : getFileContents()) {
			if(line.matches("public abstract class .*")) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void extractFunctions() {
		boolean skip = false;
		for(String line : getFileContents()) {
			if(skip) {
				skip = false;
				continue;
			}
			if(testFunction(line)) {
				processFunction(line);
			}
			else if(line.contains("@Override")) {
				skip = true;
			}
		}
	}

	@Override
	protected void extractInstanceVariables() {
		for(String line : getFileContents()) {
			if(testInstanceVariable(line)) {
				processInstanceVariable(line);
			}
		}
	}

	@Override
	protected GenericDefinition extractInheritance(HashMap<String, GenericDefinition> ref) {
		for(String line : getFileContents()) {
			if(isClassDefinition(line) && line.contains("extends")){
				String[] use = cleanInput(line);
				int posit = indexOf(use, "extends");
				String name = use[posit + 1];
				for(GenericDefinition gd : ref.values()) {
					if(gd.getName().equals(name)) {
						return gd;
					}
				}
			}
		}
		return null;
	}

	@Override
	protected ArrayList<GenericDefinition> extractRealizations(HashMap<String, GenericDefinition> ref) {
		ArrayList<GenericDefinition> out = new ArrayList<GenericDefinition>();
		for(String line : getFileContents()) {
			if(isClassDefinition(line) && line.contains("implements")){
				String[] use = cleanInput(line);
				int posit = indexOf(use, "implements");
				while(++posit < use.length) {
					String name = use[posit];
					for(GenericDefinition gi : ref.values()) {
						if(gi.getName().equals(name)) {
							out.add(gi);
						}
					}
				}
			}
		}
		return out;
	}

	@Override
	protected ArrayList<GenericDefinition> extractAssociations(HashMap<String, GenericDefinition> ref, HashSet<GenericDefinition> neighbors) {
		ArrayList<GenericDefinition> out = new ArrayList<GenericDefinition>();
		for(String line : getFileContents()) {
			if(line.matches("import .*;")){
				String name = processImportName(line);
				if(!name.contains("*")) {
					GenericDefinition use = ref.get(name);
					if(use != null && !out.contains(use))
						out.add(use);
				}
				else {
					String cont = name.substring(0, name.length() - 2);
					for(String s : ref.keySet()) {
						if(s.matches(cont + "/.*") && !out.contains(ref.get(s))) {
							out.add(ref.get(s));
						}
					}
				}
			}
			else {
				for(GenericDefinition gd : neighbors) {
					if(isInPackageDependency(line, gd.getName()) && ref.get(gd.getFullName()) != null && !out.contains(gd)) {
						if(!findName().equals(gd.getName()) || (!isFileDefinition(line) && !isConstructor(line)))
							out.add(ref.get(gd.getFullName()));
					}
				}
			}
		}
		return out;
	}
	
	@Override
	protected String findName() {
		for(String line : getFileContents()) {
			if(isFileDefinition(line)){
				String[] use = cleanInput(line);
				int posit = indexOf(use, "class");
				posit = (posit == -1 ? indexOf(use, "interface") : posit);
				posit = (posit == -1 ? indexOf(use, "enum") : posit);
				return use[posit + 1];
			}
		}
		return null;
	}
	
	//-- Analyze Line  ----------------------------------------

	protected boolean isFileDefinition(String line) {
		return line.matches(REGEX_VISIBILITY_FILE_DEF + "(abstract )?(class|interface|enum) .*");
	}
	
	private boolean isInPackageDependency(String line, String name) {
		return line.matches(".*([^a-zA-Z\\d]+|^)" + name + "[^a-zA-Z\\d]+.*") && !line.matches("package .*");
	}
	
	private boolean isClassDefinition(String line) {
		return line.matches(REGEX_VISIBILITY_FILE_DEF + "(abstract )?class .*");
	}
	
	private boolean isConstructor(String line) {
		return line.matches(REGEX_VISIBILITY_FILE_DEF + getName() + "\\s*\\(.*");
	}

	@Override
	public boolean isClassFile() {
		for(String s : getFileContents()) {
			if(s.matches(REGEX_VISIBILITY_FILE_DEF + "(abstract )?class .*")) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isInterfaceFile() {
		for(String s : getFileContents()) {
			if(s.matches(REGEX_VISIBILITY_FILE_DEF + "interface .*")) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isEnumFile() {
		for(String s : getFileContents()) {
			if(s.matches(REGEX_VISIBILITY_FILE_DEF + "(abstract )?enum .*")) {
				return true;
			}
		}
		return false;
	}
	
//---  Tester Methods   -----------------------------------------------------------------------
	
	private boolean testInstanceVariable(String in) {
		in = removeEquals(in);
		return getStatusInstanceVariable() && !(!getStatusPrivate() && in.matches("private.*")) && in.matches("(private|public|protected)[^{]*") && !in.contains("final") && !in.contains("abstract") && !in.contains("(");
	}
	
	private boolean testFunction(String in) {
		return getStatusFunction() && !(!getStatusPrivate() && in.matches("private.*")) && in.matches("(private|public|protected).*") && !in.contains(" new ") && in.contains("(") && !in.contains("=");
	}

//---  Support Methods   ----------------------------------------------------------------------
	
	private String[] cleanInput(String in) {
		String out = in.replaceAll("  ", " ").replaceAll(";", "").trim();
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
