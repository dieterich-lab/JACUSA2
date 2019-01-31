package lib.data.filter;

import java.util.Set;

import lib.data.Data;

public interface FilteredDataContainer<F extends FilteredDataContainer<F, T>, T extends Data<T>> 
extends Data<F> {

	F add(char c, T filteredData);
	F update(char c, T filteredData);
	T get(char c);
	boolean isEmpty();
	boolean contains(char c);
	Set<Character> getFilters();

}
