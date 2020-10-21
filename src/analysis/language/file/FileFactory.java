package analysis.language.file;

import java.io.File;

public class FileFactory {

	public static GenericFile generateFile(File f, String root) {
		String name = f.getName();
		String type = name.substring(name.lastIndexOf(".")+1);
		switch(type) {
			case "java":
				return new JavaFile(f, root);
			default: 
				return null;
		}
	}
	
}
