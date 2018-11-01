package jacusa.filter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jacusa.filter.factory.AbstractFilterFactory;
import lib.method.AbstractMethod;

// TODO use at some point
public class Filter {

	private static final Map<AbstractMethod, Set<String>> MAP = new HashMap<>(10);
	
	private final String id;
	private final String desc;
	private final AbstractFilterFactory filterFactory;
	
	public Filter(
			final AbstractMethod method,
			final String id, 
			final String desc,
			final AbstractFilterFactory filterFactory) {

		checkAndAdd(method, id);
		this.id 	= id;
		this.desc 	= desc;
		this.filterFactory = filterFactory;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || ! (obj instanceof Filter)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		Filter filter = (Filter)obj;
		
		return 
				id.equals(filter.id) &&
				desc.equals(filter.desc) &&
				filterFactory.equals(filter.filterFactory);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	public String getId() {
		return id;
	}
	
	public String getDesc() {
		return desc;
	}

	public AbstractFilterFactory getInstance() {
		return filterFactory;
	}
	
	private void checkAndAdd(final AbstractMethod method, final String id) {
		if (! MAP.containsKey(method)) {
			MAP.put(method, new HashSet<>(10));
		}
		final Set<String> filters = MAP.get(method);
		if (filters.contains(id)) {
			throw new IllegalArgumentException("Duplicate ID for filter: " + id);
		}
		filters.add(id);
	}
	
}
