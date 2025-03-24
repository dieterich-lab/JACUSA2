package lib.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * add documentation
 */
public class ExtendedInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Set<String> KEYS = new TreeSet<String>();
	public static final Set<String> REGISTERED_KEYS = Collections.unmodifiableSet(KEYS);

	public static void registerKey(final String key) {
		KEYS.add(key);
	}

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
	
	public Map<String, String> getRegisteredKeyValues() {
		return Collections.unmodifiableMap(map);
	}
	
}
