package jacusa.io.copytmp;

import java.io.IOException;

import lib.data.result.Result;
import lib.io.copytmp.CopyTmpResult;

/**
 * This class enables to split result in two temporary files based on artefact filters for one thread.
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

		this.copyResult 		= copyResult;
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

	@Override
	public void addResult(final Result result) throws Exception {
		// distribute to corresponding temporary file
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
