package lib.io;

import jacusa.io.copytmp.FileCopyTmpResult;
import jacusa.io.copytmp.FileCopyTmpSplitResult;
import lib.cli.parameter.GeneralParameter;
import lib.io.copytmp.CopyTmpResult;
import lib.worker.WorkerDispatcher;

/**
 * TODO add comments.
 */
public abstract class AbstractResultFileFormat extends AbstractResultFormat {

	public AbstractResultFileFormat(final char c, final String desc, 
			final String methodName, final GeneralParameter parameter) {
		super(c, desc, methodName, parameter);
	}
	
	@Override
	public CopyTmpResult createCopyTmp(final int threadId, final WorkerDispatcher workerDispatcher) {
		final AbstractResultFileWriter resultWriter = 
				(AbstractResultFileWriter)workerDispatcher.getResultWriter();
		if (getParameter().getFilteredFilename() != null) {
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

	/* TODO remove
	public void processCLI(final String line){}
	public List<ParallelDataToString> getAvailable(){
		return new ArrayList<>();
	}
	public List<ParallelDataToString> getSelected(){
		return new ArrayList<>();
	}
	*/
	
}
