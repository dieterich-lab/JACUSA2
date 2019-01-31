package lib.data;

import java.io.Serializable;

import lib.util.Copyable;
import lib.util.Mergeable;

public interface Data<T extends Copyable<T> & Mergeable<T>> 
extends Copyable<T>, Mergeable<T>, Serializable {
	
}
