package jacusa.io.copytmp;

import java.io.IOException;
import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.result.Result;
import lib.io.AbstractResultFileWriter;
import lib.io.ResultFormat;
import lib.io.copytmp.CopyTmpResult;

public class FileCopyTmpSeparatedResult<T extends AbstractData, R extends Result<T>> 
implements CopyTmpResult<T, R> {


	private FileCopyTmpSeparatedResult<T, R> copyResult;
	private FileCopyTmpSeparatedResult<T, R> copyFilteredResult;
	
	public FileCopyTmpSeparatedResult(final int threadId, 
			final AbstractResultFileWriter<T, R> resultFileWriter, 
			final AbstractResultFileWriter<T, R> filteredResultFileWriter,
			final ResultFormat<T, R> resultFormat) {
		
		
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

	public void addResult(final R result, final List<AbstractConditionParameter<T>> conditionParameters) throws Exception {
		if (result.isFiltered()) {
			copyFilteredResult.addResult(result, conditionParameters);
		} else {
			copyResult.addResult(result, conditionParameters);
		}
	}
	
	@Override
	public void copy(int iteration) throws IOException {
		copyResult.copy(iteration);
		copyFilteredResult.copy(iteration);
	}

}
