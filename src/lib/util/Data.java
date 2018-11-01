package lib.util;

import java.io.Serializable;

public interface Data<T extends Copyable<T> & Mergeable<T>> 
extends Copyable<T>, Mergeable<T>, Serializable {
	
}
