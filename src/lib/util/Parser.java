package lib.util;

/**
 * TODO
 */
public interface Parser<T> {

	T parse(String s);
	String wrap(T o);
	
}
