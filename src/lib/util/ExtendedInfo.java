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
	
	public ExtendedInfo() {
		map = new TreeMap<>();
	}

	public void clear() {
		map.clear();
	}

	public boolean contains(final String key) {
		return map.containsKey(key);
	}
	
	public void add(final String key, final String value) {
		map.put(key, value);
	}
	
	public Map<String, String> getMap() {
		return Collections.unmodifiableMap(map);
	}
	
}
