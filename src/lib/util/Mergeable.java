package lib.util;

public interface Mergeable<T extends Mergeable<T>> {

	void merge(T o);
}
