package lib.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * add documentation
 */
public class ExtendedInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Map<String, String> map;
	
	// hack
	public boolean NumericallyInstable; 
	
	public ExtendedInfo() {
		map = new TreeMap<>();
		
		NumericallyInstable = false;
	}

	public void clear() {
		map.clear();
		
		NumericallyInstable = false;
	}

	public boolean contains(final String key) {
		return map.containsKey(key);
	}
	
	public void add(final String key,
			int conditionIndex, int replicateIndex,
			final String value) {
		++conditionIndex;
		++replicateIndex;
		map.put(key + (conditionIndex) + (replicateIndex), value);
	}
	
	public void add(final String key, final String value) {
		map.put(key, value);
	}
	
	public void append(final String key, final String value) {
		map.put(key, map.getOrDefault(key, "") + value);
	}
	
	public void append(final String key, final String value, final String sep) {
		if (map.containsKey(key)) {
			map.put(key, map.get(key) + sep + value);
		} else {
			add(key, value);
		}
	}
	
	public Map<String, String> getMap() {
		return Collections.unmodifiableMap(map);
	}
	
}
