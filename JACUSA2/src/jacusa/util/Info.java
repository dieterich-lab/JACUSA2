package jacusa.util;

import java.util.Map;
import java.util.TreeMap;

public class Info {

	private static final char SEP = ';'; 
	private Map<String, StringBuilder> map;
	
	public Info() {
		map = new TreeMap<String, StringBuilder>();
	}

	public void add(final String key) {
		map.put(key, null);
	}

	public void add(final String key, final String value) {
		StringBuilder sb;
		if (! map.containsKey(key)) {
			sb = new StringBuilder();
			map.put(key, sb);
		}
		sb = map.get(key);
		sb.append(value);
	}
	
	public String combine() {
		if (isEmpty()) {
			return "*";
		}
		
		final StringBuilder sb = new StringBuilder();
		
		for (String key : map.keySet()) {
			if (sb.length() > 0) {
				sb.append(SEP);
			}
			sb.append(key);
			if (map.get(key) != null) {
				sb.append('=');
				sb.append(map.get(key).toString());
			}
		}
		
		return sb.toString();
	}

	public void addAll(Info info) {
		map.putAll(info.map);
	}

	public boolean isEmpty() {
		return map.size() == 0;
	}
	
}
