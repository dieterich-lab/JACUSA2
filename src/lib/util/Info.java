package lib.util;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import lib.io.InputOutput;

public class Info implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final char SEP = ';'; 
	private Map<String, StringBuilder> map;
	
	public Info() {
		map = new TreeMap<String, StringBuilder>();
	}

	public void add(final String key) {
		map.put(key, null);
	}
	
	public void remove(final String key) {
		map.remove(key);
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
			return Character.toString(InputOutput.EMPTY_FIELD);
		}
		
		final StringBuilder sb = new StringBuilder();
		
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
		
		return sb.toString();
	}

	public boolean contains(final String key) {
		return map.containsKey(key);
	}
	
	public void addAll(final Info info) {
		map.putAll(info.map);
	}

	public boolean isEmpty() {
		return map.size() == 0;
	}
	
}
