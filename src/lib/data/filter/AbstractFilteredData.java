package lib.data.filter;

import java.util.Set;

import lib.data.Data;
import lib.data.count.AbstractMappedData;

/**
 * TODO
 * 
 * @param <F>
 * @param <T>
 */
abstract public class AbstractFilteredData<T extends AbstractFilteredData<T, V>, V extends Data<V>>
		extends AbstractMappedData<T, Character, V> {

	private static final long serialVersionUID = 1L;

	public AbstractFilteredData(final Class<V> valueClass) {
		super(Character.class, valueClass);
	}

	public AbstractFilteredData(T o) {
		super(o);
	}

	public AbstractFilteredData<T, V> add(final char c, final V filteredData) {
		if (map.containsKey(c)) {
			throw new IllegalArgumentException();
		}

		map.put(c, filteredData);
		return this;
	}

	public AbstractFilteredData<T, V> update(final char c, final V filteredData) {
		if (!map.containsKey(c)) {
			throw new IllegalArgumentException();
		}
		map.put(c, filteredData);
		return this;
	}

	public boolean contains(final char c) {
		return map.containsKey(c);
	}

	public V get(final char c) {
		return map.get(c);
	}

	public Set<Character> getFilters() {
		return map.keySet();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (final char c : getFilters()) {
			sb.append("Filter ");
			sb.append(c);
			sb.append(':');
			sb.append('\n');
			sb.append(get(c).toString());
			sb.append('\n');
		}
		return sb.toString();
	}

	/* TODO
	public abstract static class AbstractParser<F extends FilteredDataContainer<F, T>, T extends Data<T>>
			implements lib.util.Parser<F> {

		public static final char FILTER_SEP = ',';
		public static final char FILTER_DATA_SEP = '=';
		public static final char EMPTY = '*';

		private final char filterSep;
		private final char filterDataSep;

		public AbstractParser(final char filterSep, final char filterDataSep) {
			this.filterSep = filterSep;
			this.filterDataSep = filterDataSep;
		}

		public AbstractParser() {
			this(FILTER_SEP, FILTER_DATA_SEP);
		}

		public String wrap(final FilteredDataContainer<F, T> filteredData) {
			final StringBuilder sb = new StringBuilder();
			if (filteredData.isEmpty()) {
				sb.append(EMPTY);
			} else {
				boolean first = true;
				for (final char c : filteredData.getFilters()) {
					if (!first) {
						sb.append(FILTER_SEP);
					} else {
						first = false;
					}
					sb.append(Character.toString(c));
					sb.append(FILTER_DATA_SEP);
					sb.append(wrapFilteredElement(filteredData.get(c)));
				}
			}
			return sb.toString();
		}

		protected abstract String wrapFilteredElement(T filteredElement);

		protected void parse(String s, FilteredDataContainer<F, T> filteredDataContainer) {
			if (s.equals(Character.toString(EMPTY))) {
				return;
			}

			// expected format chr:start-end:strand -1 0 +1
			final Pattern pattern = Pattern
					.compile("([A-Za-z]+)" + filterDataSep + "([^" + filterSep + filterDataSep + "]+)");
			final int filters = s.split(Character.toString(filterSep)).length;

			final Matcher match = pattern.matcher(s);
			while (match.find()) {
				final String g1 = match.group(1);
				if (g1.length() != 1) {
					throw new IllegalArgumentException("Cannot parse filter from: " + s);
				}
				final char c = g1.charAt(0);
				T filteredData = parseFilteredData(match.group(2));
				filteredDataContainer.add(c, filteredData);
			}
			if (filters != filteredDataContainer.getFilters().size()) {
				throw new IllegalArgumentException("Size of parsed filteredData != filters: " + s);
			}
			if (filteredDataContainer.getFilters().isEmpty()) {
				throw new IllegalArgumentException("Cannot parse filter from: " + s);
			}
		}

		protected abstract T parseFilteredData(String s);

	}
	*/
}
