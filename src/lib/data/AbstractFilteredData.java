package lib.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractFilteredData<T> {

	private final Map<Character, T> map;
	
	public AbstractFilteredData() {
		map = new HashMap<Character, T>();
	}
	
	public AbstractFilteredData(final AbstractFilteredData<T> src) {
		this();
		
		for (final char c : src.getFilters()) {
			map.put(c, copy(src.get(c)));
		}
	}
	
	public void add(final char c, final T filteredData) {
		if (map.containsKey(c)) {
			throw new IllegalArgumentException();
		}
		
		map.put(c, filteredData);
	}
	
	public boolean contains(final char c) {
		return map.containsKey(c);
	}
	
	public T get(final char c) {
		return map.get(c);
	}
	
	public Set<Character> getFilters() {
		return map.keySet();
	}
	
	public abstract AbstractFilteredData<T> copy();
	protected abstract T copy(T data);
	public abstract void merge(AbstractFilteredData<T> src);
	
}
