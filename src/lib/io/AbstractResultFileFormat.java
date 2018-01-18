package lib.io;

import jacusa.io.copytmp.FileCopyTmpResult;
import jacusa.io.copytmp.FileCopyTmpSeparatedResult;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.result.Result;
import lib.io.copytmp.CopyTmpResult;

public abstract class AbstractResultFileFormat<T extends AbstractData, R extends Result<T>> 
extends AbstractResultFormat<T, R>{

	public static final String FILE_SUFFIX = ".filtered";
	
	private final AbstractParameter<T, R> parameter;
	
	public AbstractResultFileFormat(final char c, final String desc, final AbstractParameter<T, R> parameter) {
		super(c, desc);
		this.parameter = parameter;
	}

	@Override
	public final CopyTmpResult<T, R> createCopyTmp(final int threadId) {
		if (parameter.isSeparate()) {
			
			final AbstractResultFileWriter<T, R> resultWriter = 
					(AbstractResultFileWriter<T, R>)parameter.getResultWriter();
			// add suffix to filename
			final AbstractResultFileWriter<T, R> filteredResultWriter = 
					(AbstractResultFileWriter<T, R>)createWriter(resultWriter.getFilename() + FILE_SUFFIX);

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

	protected AbstractParameter<T, R> getParameter() {
		return parameter;
	}
	
}
