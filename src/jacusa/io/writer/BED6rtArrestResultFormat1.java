package jacusa.io.writer;

import jacusa.cli.parameters.hasStatistic;
import jacusa.io.copytmp.FileCopyTmpResult;

import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.hasReadInfoCount;
import lib.data.has.hasReferenceBase;
import lib.data.result.Result;
import lib.io.AbstractResultFileWriter;
import lib.io.AbstractResultFormat;
import lib.io.ResultWriter;
import lib.io.copytmp.CopyTmpResult;

public class BED6rtArrestResultFormat1<T extends AbstractData & hasReferenceBase & hasReadInfoCount, R extends Result<T> & hasStatistic> 
extends AbstractResultFormat<T, R> {

	public static final char CHAR = 'A';

	private AbstractParameter<T, R> parameter;
	
	protected BED6rtArrestResultFormat1(
			final char c,
			final String desc,
			final AbstractParameter<T, R> parameter) {
		super(c, desc);
		
		this.parameter = parameter;
	}

	public BED6rtArrestResultFormat1(final AbstractParameter<T, R> parameter) {
		this(CHAR, "Format 1 - arrest only", parameter);
	}

	@Override
	public ResultWriter<T, R> createWriter(final String filename) {
		return new BED6rtArrestResultWriter1<T, R>(filename, parameter);
	}

	@Override
	public CopyTmpResult<T, R> createCopyTmp(final int threadId) {
		return new FileCopyTmpResult<T, R>(threadId, 
				(AbstractResultFileWriter<T, R>)parameter.getResultWriter(), this);
	}
}