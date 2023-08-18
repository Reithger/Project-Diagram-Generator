package analysis.process.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

public class FileFactory {

	public static List<GenericFile> generateFile(File f, String root) {
		String name = f.getName();
		String type = FilenameUtils.getExtension(name);
		switch(type) {
			case "java":
				JavaFile jf = new JavaFile(f, root);
				List<GenericFile> out = new ArrayList<GenericFile>();
				if(jf.detectInternalClasses()) {
					for(GenericFile gf : jf.extractInternalClasses()) {
						out.add(gf);
					}
				}
				else {
					out.add(jf);
				}
				return out;
			default: 
				return null;
		}
	}
	
}
