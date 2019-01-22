package test.utlis;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MDtraverse {

	private static final Pattern MD_PATTERN = 
			Pattern.compile("\\G(?:([0-9]+)|([ACTGNactgn]+)|(\\^[ACTGNactgn]+))");
	
	private final Map<Integer, String> matched2nonRefSeq;
	
	public MDtraverse(final String MD) {
		matched2nonRefSeq = init(MD);
	}
	
	private Map<Integer, String> init(final String MD) {
		final Map<Integer, String> matched2nonRefSeq = new HashMap<>();
		
		int totalMatchedBases = 0;
		final Matcher match = MD_PATTERN.matcher(MD);
		while (match.find()) {
			String matchGroup;
			if ((matchGroup = match.group(1)) != null) { 		// match(es)
	            totalMatchedBases += Integer.parseInt(matchGroup);
	        } else if ((matchGroup = match.group(2)) != null) { // mismatch(es)
	        	totalMatchedBases += matchGroup.length();
	        	matched2nonRefSeq.put(totalMatchedBases, match.group(2));
	        } else if ((matchGroup = match.group(3)) != null) { // deletion
	        	matched2nonRefSeq.put(totalMatchedBases, match.group(3));
	        	totalMatchedBases += matchGroup.length();
	        } else {
	            throw new IllegalStateException();
	        }
		}
		return matched2nonRefSeq;
	}
	
	public boolean containsBases(final int matchedBases) {
		return matched2nonRefSeq.containsKey(matchedBases);
	}
	
	public String getBases(final int matchedBases) {
		return matched2nonRefSeq.get(matchedBases);
	}
	
}
