package lib.io;

import jacusa.io.copytmp.FileCopyTmpResult;
import jacusa.io.copytmp.FileCopyTmpSplitResult;
import lib.cli.parameter.GeneralParameter;
import lib.io.copytmp.CopyTmpResult;
import lib.worker.WorkerDispatcher;

/**
 * TODO add comments.
 *
 * @param <T>
 * @param <R>
 */
public abstract class AbstractResultFileFormat 
implements ResultFormat {

	// unique (per method) char that identifies a result format
	private final char c;
	// description that shown in help on command line 
	private final String desc;

	private final String methodName;
	private final GeneralParameter parameter;
	
	public AbstractResultFileFormat(final char c, final String desc, 
			final String methodName, final GeneralParameter parameter) {
		this.c = c;
		this.desc = desc;
		
		this.methodName = methodName;
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

	public GeneralParameter getParameter() {
		return parameter;
	}

	public String getMethodName() {
		return methodName;
	}
	
	@Override
	public CopyTmpResult createCopyTmp(final int threadId, final WorkerDispatcher workerDispatcher) {
		final AbstractResultFileWriter resultWriter = 
				(AbstractResultFileWriter)workerDispatcher.getResultWriter();
		if (parameter.splitFiltered()) {
			final AbstractResultFileWriter filteredResultWriter = 
					(AbstractResultFileWriter)workerDispatcher.getFilteredResultWriter();

			// result will be split
			return new FileCopyTmpSplitResult(
					new FileCopyTmpResult(threadId, resultWriter, this),
					new FileCopyTmpResult(threadId, filteredResultWriter, this) );
		} else {
			return new FileCopyTmpResult(threadId, resultWriter, this);
		}
	}
	
}
