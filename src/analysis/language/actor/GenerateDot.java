package analysis.language.actor;

import java.util.HashMap;

public interface GenerateDot {

	public abstract String generateDot(int val);
	
	public abstract String generateRelationships(HashMap<String, Integer> ref);
	
}
