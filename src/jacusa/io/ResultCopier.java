package jacusa.io;

import lib.data.AbstractData;
import lib.data.result.Result;
import lib.io.ResultWriter;

public interface ResultCopier<T extends AbstractData, R extends Result<T>> {

	void copy(int instances, final ResultWriter<T, R> resultWriter);
	
}
