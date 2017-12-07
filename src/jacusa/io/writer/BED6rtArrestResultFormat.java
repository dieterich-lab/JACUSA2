package jacusa.io.writer;

import jacusa.cli.parameters.hasStatistic;
import jacusa.io.copytmp.FileCopyTmpResult;

import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReadInfoCount;
import lib.data.has.hasReferenceBase;
import lib.data.result.Result;
import lib.io.AbstractResultFileWriter;
import lib.io.AbstractResultFormat;
import lib.io.ResultWriter;
import lib.io.copytmp.CopyTmpResult;

public class BED6rtArrestResultFormat<T extends AbstractData & hasBaseCallCount & hasReferenceBase & hasReadInfoCount, R extends Result<T> & hasStatistic> 
extends AbstractResultFormat<T, R> {

	public static final char CHAR = 'B';

	private AbstractParameter<T, R> parameter;
	
	protected BED6rtArrestResultFormat(
			final char c,
			final String desc,
			final AbstractParameter<T, R> parameter) {
		super(c, desc);
		
		this.parameter = parameter;
	}

	public BED6rtArrestResultFormat(final AbstractParameter<T, R> parameter) {
		this(CHAR, "Default", parameter);
	}

	@Override
	public ResultWriter<T, R> createWriter(final String filename) {
		return new BED6rtArrestResultWriter<T, R>(filename, parameter);
	}

	@Override
	public CopyTmpResult<T, R> createCopyTmp(final int threadId) {
		return new FileCopyTmpResult<T, R>(threadId, 
				(AbstractResultFileWriter<T, R>)parameter.getResultWriter(), this);
	}
}