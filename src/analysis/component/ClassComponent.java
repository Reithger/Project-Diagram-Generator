package analysis.component;

public abstract class ClassComponent {
	
//---  Constants   ----------------------------------------------------------------------------

	private final static String[] BAD_CHARACTERS_LABELS = new String[] {"<", ">"};
	private final static String[] GOOD_CHARACTERS_LABELS = new String[] {"&lt;", "&gt;"};
	
	protected String fixForDot(String in) {
		for(int i = 0; i < BAD_CHARACTERS_LABELS.length; i++) {
			in = in.replaceAll(BAD_CHARACTERS_LABELS[i], GOOD_CHARACTERS_LABELS[i]);
		}
		return in;
	}
	
}
