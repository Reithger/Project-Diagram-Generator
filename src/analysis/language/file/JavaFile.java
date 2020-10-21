package analysis.language.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import analysis.language.actor.GenericClass;	//Let GenericFile deal with these
import analysis.language.actor.GenericDefinition;
import analysis.language.actor.GenericInterface;
import analysis.language.component.Function;
import analysis.language.component.InstanceVariable;

public class JavaFile extends GenericFile {

//---  Constants   ----------------------------------------------------------------------------
	
	private final static String TYPE = ".java";
	private final static String[] KEY_BUFFER_PHRASES = new String[] {"(", ")"};
	private final static String[] REMOVE_TERMS = new String[] {"volatile", "abstract", "static"};

//---  Constructors   -------------------------------------------------------------------------
	
	public JavaFile(File in, String root) {
		super(in, root);
	}

//---  Operations   ---------------------------------------------------------------------------

	@Override
	public boolean isClassFile() {
		for(String s : getFileContents()) {
			if(s.matches("public (abstract )?class.*")) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isInterfaceFile() {
		for(String s : getFileContents()) {
			if(s.matches("public interface .*")) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected ArrayList<String> preProcess(String in){
		ArrayList<String> out = new ArrayList<String>();
		in = in.replaceAll("//[^\n]*\n", "\n");
		in = in.replaceAll("\n", " ").replaceAll("\t", "").replaceAll("  ", " ").replaceAll("\"[^\"]\"", "").replaceAll(";", ";\n").replaceAll("\\*/",  "\n").replaceAll("\\{", "\\{\n").replaceAll("\\}", "");
		in = bufferCharacter(in, "\\{");
		in = bufferCharacter(in, "\\}");
		in = bufferCharacter(in, "\\)");
		in = bufferCharacter(in, "\\(");
		String[] parsed = in.split("\n");
		for(String s : parsed)
			if(s != null && !s.trim().equals(""))
				out.add(s.trim());
		return out;
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
	protected ArrayList<Function> extractFunctions() {
		ArrayList<Function> out = new ArrayList<Function>();		//TODO: Preprocess merges @Override into function line which invalidates it
		for(String line : getFileContents()) {
			if(testFunction(line)) {
				out.add(processFunction(line));
			}
		}
		return out;
	}

	@Override
	protected ArrayList<InstanceVariable> extractInstanceVariables() {
		ArrayList<InstanceVariable> out = new ArrayList<InstanceVariable>();
		for(String line : getFileContents()) {
			if(testInstanceVariable(line)) {
				out.add(processInstanceVariable(line));
			}
		}
		return out;
	}

	@Override
	protected GenericClass extractInheritance(HashMap<String, GenericDefinition> ref) {
		for(String line : getFileContents()) {
			if(isClassDefinition(line) && line.contains("extends")){
				String[] use = cleanInput(line);
				int posit = indexOf(use, "extends");
				String name = use[posit + 1];
				for(GenericDefinition gd : ref.values()) {
					if(gd.getName().equals(name)) {
						return (GenericClass)gd;
					}
				}
			}
		}
		return null;
	}

	@Override
	protected ArrayList<GenericInterface> extractRealizations(HashMap<String, GenericInterface> ref) {
		ArrayList<GenericInterface> out = new ArrayList<GenericInterface>();
		for(String line : getFileContents()) {
			if(isClassDefinition(line) && line.contains("implements")){
				String[] use = cleanInput(line);
				int posit = indexOf(use, "implements");
				while(++posit < use.length) {
					String name = use[posit];
					for(GenericInterface gi : ref.values()) {
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
				GenericDefinition use = ref.get(name);
				if(use != null && !out.contains(use))
					out.add(use);
			}
			else {
				for(GenericDefinition gd : neighbors) {
					if(line.matches(".*[^a-zA-Z\\d]+" + gd.getName() + "[^a-zA-Z\\d]+.*") && ref.get(gd.getFullName()) != null && !out.contains(gd)) {
						if(!findName().equals(gd.getName()) || (!isClassDefinition(line) && !isConstructor(line)))
							out.add(ref.get(gd.getFullName()));
					}
				}
			}
		}
		return out;
	}
	
	private boolean isClassDefinition(String line) {
		return line.matches("public (abstract )?class.*");
	}
	
	private boolean isConstructor(String line) {
		return line.matches("(public|private|protected) " + getName() + "\\s*\\(.*");
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

	@Override
	protected String findName() {
		for(String line : getFileContents()) {
			if(line.matches("public (abstract )?(class|interface).*")){
				String[] use = cleanInput(line);
				int posit = indexOf(use, "class");
				posit = (posit == -1 ? indexOf(use, "interface") : posit);
				return use[posit + 1];
			}
		}
		return null;
	}

	private InstanceVariable processInstanceVariable(String in) {
		in = removeEquals(in);
		boolean underline = false;
		if(in.contains("static")) {
			underline = true;	//TODO: Do the formatting here
		}
		String[] cont = cleanInput(in);
		String vis = processVisibility(cont[0]);
		String typ = compileType(cont, 1);
		return compileInstanceVariable(vis, cont[cont.length - 1], typ, underline);
	}
	
	private Function processFunction(String in) {
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
		ArrayList<String> args = new ArrayList<String>();
		for(int i = argStart + 1; i < cont.length - 2; i += 2) {
			if(cont[i].equals(")"))
				break;
			String type = compileType(cont, i);
			i += type.replaceAll("[^,]", "").length();
			String nom = cont[i + 1].replaceAll(",", "");
			args.add(nom);
			args.add(type);
		}
		if(ret.equals("")) {
			return compileConstructor(vis, name, compileArguments(args));
		}
		else {
			return compileFunction(vis, name, ret, compileArguments(args), stat, abs);
		}
	};
	
//---  Tester Methods   -----------------------------------------------------------------------
	
	private boolean testInstanceVariable(String in) {
		in = removeEquals(in);
		return getStatusInstanceVariable() && !(!getStatusPrivate() && in.matches("private.*")) && in.matches("\\s*(private|public|protected)[^{]*") && !in.contains("final") && !in.contains("abstract") && !in.contains("(");
	}
	
	private boolean testFunction(String in) {
		return getStatusFunction() && !(!getStatusPrivate() && in.matches("private.*")) && in.matches("\\s*(private|public|protected).*") && !in.contains(" new ") && in.contains("(") && !in.contains("=");
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
