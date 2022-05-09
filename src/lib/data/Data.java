package lib.data;

import java.io.Serializable;

import lib.util.Copyable;
import lib.util.Mergeable;

/**
 * Defines "container/wrapper" interface of data that can be stored in parallelData.
 * @param <T> the data that is being wrapped 
 */
public interface Data<T extends Copyable<T> & Mergeable<T>> 
extends Copyable<T>, Mergeable<T>, Serializable {
	
	// TODO do we need this?
	// void init(GeneralParameter parameter) throws InstantiationException, IllegalAccessException;
	
}
