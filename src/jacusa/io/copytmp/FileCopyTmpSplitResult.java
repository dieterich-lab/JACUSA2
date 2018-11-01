package jacusa.io.copytmp;

import java.io.IOException;

import lib.data.result.Result;
import lib.io.copytmp.CopyTmpResult;

/**
 * TODO add comments. 
 * Used, when results should be split based on artefact filters for one thread
 *
 * @param 
 * @param <R>
 */
public class FileCopyTmpSplitResult 
implements CopyTmpResult {

	// handles NOT filtered results
	private final FileCopyTmpResult copyResult;
	// handles filtered results
	private final FileCopyTmpResult copyFilteredResult;
	
	public FileCopyTmpSplitResult(
			final FileCopyTmpResult copyResult,
			final FileCopyTmpResult copyFilteredResult) {

		this.copyResult = copyResult;
		this.copyFilteredResult = copyFilteredResult;
	}
	
	@Override
	public void closeTmpReader() throws IOException {
		copyResult.closeTmpReader();
		copyFilteredResult.closeTmpReader();
	}
	
	@Override
	public void closeTmpWriter() throws IOException {
		copyResult.closeTmpWriter();
		copyFilteredResult.closeTmpWriter();
	}

	@Override
	public void newIteration() {
		copyResult.newIteration();
		copyFilteredResult.newIteration();
	}

	public void addResult(final Result result) throws Exception {
		if (result.isFiltered()) {
			copyFilteredResult.addResult(result);
		} else {
			copyResult.addResult(result);
		}
	}
	
	@Override
	public void copy(int iteration) throws IOException {
		copyResult.copy(iteration);
		copyFilteredResult.copy(iteration);
	}

}
