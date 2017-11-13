package jacusa.io.copytmp;

import jacusa.io.Output;
import jacusa.io.OutputWriter;
import jacusa.io.format.BED6call;

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
import lib.data.has.hasPileupCount;
import lib.io.copytmp.CopyTmp;
import lib.util.AbstractTool;

public class CopyTmpResult<T extends AbstractData & hasPileupCount> implements CopyTmp {

	private BED6call<T> format;
	private final Output resultWriter;
	
	private final Output tmpResultWriter;
	private final BufferedReader tmpCallReader;
	
	private List<Integer> iteration2storedCalls;
	
	public CopyTmpResult(final int threadId, 
			final AbstractParameter<T> parameter) throws IOException {
	
		format = new BED6call<T>(parameter);
		this.resultWriter = parameter.getOutput();
		
		final String tmpResultFilename = createTmpResultFilename(threadId);
		tmpResultWriter = new OutputWriter(new File(tmpResultFilename));
		tmpCallReader = new BufferedReader(new FileReader(new File(tmpResultFilename)));
		
		iteration2storedCalls = new ArrayList<Integer>();
	}
	
	private String createTmpResultFilename(final int threadId) throws IOException {
		final String prefix = "calls_" + threadId + "_";
		final File file = File.createTempFile(prefix, ".gz");
		if (! AbstractTool.getLogger().isDebug()) {
			file.deleteOnExit();
		}
		return file.getCanonicalPath();
	}
	
	public void close() throws IOException {
		tmpResultWriter.close();
	}

	@Override
	public void nextIteration() {
		iteration2storedCalls.add(0);		
	}

	public void addResult(Result<T> result, List<AbstractConditionParameter<T>> conditionParameters) throws Exception {
		final String s = format.convert2String(result);
		tmpResultWriter.write(s);
		final int iteration = iteration2storedCalls.size() - 1;
		final int storedVariants = iteration2storedCalls.get(iteration) + 1;
		iteration2storedCalls.set(iteration, storedVariants);
	}
	
	@Override
	public void copy(int iteration) throws IOException {
		int copiedVariants = 0;
		final int storedVariants = iteration2storedCalls.get(iteration);

		String line;
		while (storedVariants >= copiedVariants && (line = tmpCallReader.readLine()) != null) {
			resultWriter.write(line);
			copiedVariants++;
		}
	}

}
