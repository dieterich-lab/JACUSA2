package lib.io;

import lib.data.AbstractData;
import lib.data.result.Result;
import lib.io.copytmp.CopyTmpResult;

public interface ResultFormat<T extends AbstractData, R extends Result<T>> {

	char getC();
	String getDesc();

	ResultWriter<T, R> createWriter(final String filename);
	CopyTmpResult<T, R> createCopyTmp(final int threadId);
}
