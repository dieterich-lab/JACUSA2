package jacusa.io.copytmp;

import jacusa.io.Output;
import jacusa.io.OutputWriter;
import jacusa.io.format.RTArrestResultFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.Result;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReadInfoCount;
import lib.io.copytmp.CopyTmp;
import lib.util.AbstractTool;

// FIXME
public class CopyTmpRTArrestResult<T extends AbstractData & hasBaseCallCount & hasReadInfoCount> implements CopyTmp {

	private RTArrestResultFormat<T> format;
	private final Output resultWriter;
	
	private final Output tmpResultWriter;
	private final BufferedReader tmpResultReader;
	
	private List<Integer> iteration2storedResults;
	
	public CopyTmpRTArrestResult(final int threadId, 
			final AbstractParameter<T> parameter) throws IOException {
	
		format = new RTArrestResultFormat<T>(parameter);
		this.resultWriter = parameter.getOutput();
		
		final String tmpResultFilename = createTmpResultFilename(threadId);
		tmpResultWriter = new OutputWriter(new File(tmpResultFilename));
		tmpResultReader = new BufferedReader(new FileReader(new File(tmpResultFilename)));
		
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
	public void nextIteration() {
		iteration2storedResults.add(0);
	}

	public void addResult(Result<T> result, List<AbstractConditionParameter<T>> conditionParameters) throws Exception {
		final String s = format.convert2String(result);
		tmpResultWriter.write(s);
		
		final int iteration = iteration2storedResults.size() - 1;
		int storedResults = iteration2storedResults.get(iteration) + 1;
		iteration2storedResults.set(iteration, storedResults);
	}
	
	@Override
	public void copy(int iteration) throws IOException {
		int copiedVariants = 0;
		final int storedVariants = iteration2storedResults.get(iteration);

		String line;
		while (storedVariants > copiedVariants && (line = tmpResultReader.readLine()) != null) {
			resultWriter.write(line);
			copiedVariants++;
		}
	}

}
