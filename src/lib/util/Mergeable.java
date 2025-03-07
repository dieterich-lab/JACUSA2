package lib.util;

/**
 * TODO add documentation
 */
public interface Mergeable<T extends Mergeable<T>> {

	void merge(T o);
}
