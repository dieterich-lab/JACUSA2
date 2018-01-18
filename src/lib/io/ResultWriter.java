package lib.io;


import java.io.Closeable;
import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.result.Result;

public interface ResultWriter<T extends AbstractData, R extends Result<T>> 
extends Closeable {

	public abstract void writeHeader(final List<AbstractConditionParameter<T>> conditionParameter);
	public abstract void writeResult(final R result);

	public String getInfo();

}
