package jacusa.io.writer;

import jacusa.io.copytmp.FileCopyTmpResult;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.hasPileupCount;
import lib.data.result.Result;
import lib.io.AbstractResultFileWriter;
import lib.io.AbstractResultFormat;
import lib.io.copytmp.CopyTmpResult;

public class BED6pileupResultFormat<T extends AbstractData & hasPileupCount, R extends Result<T>> 
extends AbstractResultFormat<T, R> {

	public static final char CHAR = 'B';
	private AbstractParameter<T, R> parameter;
	
	public BED6pileupResultFormat(final AbstractParameter<T, R> parameter) {
		super(CHAR, "Default");
		
		this.parameter = parameter;
	}

	@Override
	public BED6pileupResultWriter<T, R> createWriter(final String filename) {
		return new BED6pileupResultWriter<T, R>(filename, parameter);
	}

	@Override
	public CopyTmpResult<T, R> createCopyTmp(final int threadId) {
		return new FileCopyTmpResult<T, R>(threadId, 
				(AbstractResultFileWriter<T, R>)parameter.getResultWriter(), this);
	}

}