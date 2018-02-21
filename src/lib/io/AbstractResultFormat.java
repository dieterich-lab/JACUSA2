package lib.io;

import jacusa.io.copytmp.FileCopyTmpResult;
import jacusa.io.copytmp.FileCopyTmpSeparatedResult;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.result.Result;
import lib.io.copytmp.CopyTmpResult;

public abstract class AbstractResultFormat<T extends AbstractData, R extends Result<T>> 
implements ResultFormat<T, R>{

	private final char c;
	private final String desc;


	private final AbstractParameter<T, R> parameter;
	
	public AbstractResultFormat(final char c, final String desc, final AbstractParameter<T, R> parameter) {
		this.c = c;
		this.desc = desc;
		
		this.parameter = parameter;
	}

	@Override
	public final char getC() {
		return c;
	}

	@Override
	public final String getDesc() {
		return desc;
	}

	public AbstractParameter<T, R> getParameter() {
		return parameter;
	}
	
	@Override
	public CopyTmpResult<T, R> createCopyTmp(final int threadId) {
		if (parameter.isSeparate()) {
			
			final AbstractResultFileWriter<T, R> resultWriter = 
					(AbstractResultFileWriter<T, R>)parameter.getResultWriter();
			// add suffix to filename
			final AbstractResultFileWriter<T, R> filteredResultWriter = 
					(AbstractResultFileWriter<T, R>)parameter.getFilteredResultWriter();

			// result will be separated
			return new FileCopyTmpSeparatedResult<T, R>(threadId, 
					resultWriter,
					filteredResultWriter,
					this);
		} else {
			return new FileCopyTmpResult<T, R>(threadId, 
					(AbstractResultFileWriter<T, R>)parameter.getResultWriter(), this);
		}
	}
	
}
