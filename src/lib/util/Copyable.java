package lib.util;

public interface Copyable<T extends Copyable<T>> {

	T copy();

}
