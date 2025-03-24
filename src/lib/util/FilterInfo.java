package lib.util;

import java.util.Set;
import java.util.TreeSet;

public class FilterInfo {
	
	private final Set<String> data;
	
	public FilterInfo() {
		this.data = new TreeSet<String>();
	}
	
	public void add(char filterID) {
		data.add(Character.toString(filterID));
	}
	
	public boolean contains(char filterID) {
		return data.contains(Character.toString(filterID));
	}
	
	public String combine() {
		return Util.join(data, ',');
	}
	
}
