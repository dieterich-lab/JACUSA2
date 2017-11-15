package jacusa.io.copytmp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.result.Result;
import lib.io.AbstractResultFileWriter;
import lib.io.ResultFormat;
import lib.io.ResultWriter;
import lib.io.copytmp.CopyTmpResult;
import lib.util.AbstractTool;

public class FileCopyTmpResult<T extends AbstractData, R extends Result<T>> 
implements CopyTmpResult<T, R> {

	private AbstractResultFileWriter<T, R> resultFileWriter;

	private ResultWriter<T, R> tmpResultWriter;
	private BufferedReader tmpResultReader;

	private final List<Integer> iteration2storedResults;

	public FileCopyTmpResult(final int threadId, 
			final AbstractResultFileWriter<T, R> resultFileWriter, final ResultFormat<T, R> resultFormat) {
	
		this.resultFileWriter = resultFileWriter; 
		try {
			String tmpResultFilename = createTmpResultFilename(threadId);
			tmpResultWriter = resultFormat.createWriter(tmpResultFilename);
			tmpResultReader = new BufferedReader(new FileReader(new File(tmpResultFilename)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		iteration2storedResults = new ArrayList<Integer>(1000);
	}
	
	private String createTmpResultFilename(final int threadId) throws IOException {
		final String prefix = "calls_" + threadId + "_";
		final File file = File.createTempFile(prefix, ".gz");
		if (! AbstractTool.getLogger().isDebug()) {
			file.deleteOnExit();
		}
		return file.getCanonicalPath();
	}
	
	@Override
	public void closeTmpReader() throws IOException {
		tmpResultReader.close();
	}
	
	@Override
	public void closeTmpWriter() throws IOException {
		tmpResultWriter.close();
	}

	@Override
	public void newIteration() {
		iteration2storedResults.add(0);
	}

	public void addResult(final R result, final List<AbstractConditionParameter<T>> conditionParameters) throws Exception {
		tmpResultWriter.writeResult(result);
		
		final int iteration = iteration2storedResults.size() - 1;
		int storedResults = iteration2storedResults.get(iteration) + 1;
		iteration2storedResults.set(iteration, storedResults);
	}

	protected AbstractResultFileWriter<T, R> getResultWriter() {
		return resultFileWriter;
	}
	
	protected ResultWriter<T, R> getTmpResultWriter() {
		return tmpResultWriter;
	}
	
	@Override
	public void copy(int iteration) throws IOException {
		int copiedVariants = 0;
		final int storedVariants = iteration2storedResults.get(iteration);

		String line;
		while (storedVariants > copiedVariants && (line = tmpResultReader.readLine()) != null) {
			resultFileWriter.addLine(line);
			copiedVariants++;
		}
	}

}
