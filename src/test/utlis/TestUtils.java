package test.utlis;

import java.util.Set;

public abstract class TestUtils {

	public static <T> boolean equalSets(Set<T> expected, Set<T> actual) {
		if (expected.size() != actual.size()) {
			return false;
		}

		for (final T e : expected) {
			if (! actual.contains(e)) {
				return false;
			}
		}

		return true;
	}

	public static <T> String collapseSet(final Set<T> set, final char sep) {
		return collapseSet(set, Character.toString(sep));
	}
	
	public static <T> String collapseSet(final Set<T> set, final String sep) {
		final StringBuilder sb = new StringBuilder();
		for (final T e : set) {
			if (sb.length() > 0) {
				sb.append(sep.toString());
			}
			sb.append(e);
		}
		
		return sb.toString();
	}
	
}
