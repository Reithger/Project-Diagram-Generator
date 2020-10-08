package analysis.language;

import java.io.File;
import analysis.language.Class;

public class ClassFactory {

	public static Class generateClass(File f, String root) {
		String name = f.getName();
		String type = name.substring(name.lastIndexOf(".")+1);
		switch(type) {
			case "java":
				return new JavaClass(f, root);
			default: 
				return null;
		}
	}
	
}
