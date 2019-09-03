package lib.util;

/**
 * TODO
 */
public interface Mergeable<T extends Mergeable<T>> {

	void merge(T o);
}
