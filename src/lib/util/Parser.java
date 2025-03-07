package lib.util;

/**
 * TODO add documentation
 */
public interface Parser<T> {

	T parse(String s);
	String wrap(T o);
	
}
