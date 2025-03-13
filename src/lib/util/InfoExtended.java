package lib.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lib.io.InputOutput;

/**
 * add documentation
 */
public class InfoExtended implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Map<String, String> siteMap;
	private final List<Map<String, String>> conditionMap;
	private final List<List<Map<String, String>>> replicateMap;
	
	public InfoExtended(final List<Integer> replicateSizes) {
		siteMap = new TreeMap<>();
		conditionMap = new ArrayList<Map<String,String>>(replicateSizes.size());
		replicateMap = new ArrayList<List<Map<String,String>>>(replicateSizes.size());
		for (int conditionIndex = 0; conditionIndex < replicateSizes.size(); ++conditionIndex) {
			conditionMap.add(new HashMap<String, String>(2));
			replicateMap.add(new ArrayList<Map<String,String>>(replicateSizes.get(conditionIndex)));
			for (int replicateIndex = 0; replicateIndex < replicateSizes.get(conditionIndex); ++replicateIndex) {
				replicateMap.get(conditionIndex).add(new HashMap<String, String>());
			}
		}
	}

	public void clear() {
		siteMap.clear();
		conditionMap.forEach(map -> map.clear());
		for (final List<Map<String,String>> replicates : replicateMap) {
			replicates.forEach(map -> map.clear());
		}
	}
	
	public void addSite(final String key) {
		siteMap.put(key, null);
	}
	
	public void addSite(final String key, final String value) {
		siteMap.put(key, value);
	}
	
	public void addCondition(final int conditionIndex, final String key, final String value) {
		conditionMap.get(conditionIndex).put(key, value);
	}
	
	public String combine() {
		final StringBuilder sb = new StringBuilder();

		/* TODO implement
		for (final String key : map.keySet()) {
			if (sb.length() > 0) {
				sb.append(SEP);
			}
			sb.append(key);
			if (map.containsKey(key) && map.get(key) != null) {
				sb.append('=');
				sb.append(map.get(key).toString());
			}
		}
		*/
		
		final String s = sb.toString();
		if (s.isEmpty()) {
			return Character.toString(InputOutput.EMPTY_FIELD);
		}
		
		return s;
	}
	
}
