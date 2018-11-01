package lib.util;

public interface Parser<T> {

	T parse(String s);
	String wrap(T o);
	
}
