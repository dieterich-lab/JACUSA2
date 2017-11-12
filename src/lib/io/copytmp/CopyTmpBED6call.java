package lib.io.copytmp;

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
import lib.io.variant.AbstractVariantWriter;
import lib.util.AbstractTool;
import lib.variant.Variant;

public class CopyTmpBED6call<T extends AbstractData & hasPileupCount> implements CopyTmp {

	private BED6call<T> format;
	
	private final AbstractResultWriter resultWriter;
	
	private final AbstractResultWriter tmpResultWriter;
	private final BufferedReader tmpCallReader;
	
	private List<Integer> iteration2storedCalls;
	
	public CopyTmpBED6call(final int threadId, 
			final AbstractParameter<T> parameters, 
			final AbstractResultWriter resultWriter) throws IOException {
		this.resultWriter = resultWriter;
	
		format = new BED6call<T>(parameters);
		
		final String tmpResultFilename = createTmpResultFilename(threadId);
		tmpResultWriter = resultWriter.getFormat().createWriterInstance(tmpResultFilename);
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

	public void addCall(Result<T> result, List<AbstractConditionParameter<?>> conditionParameters) throws Exception {
		tmpResultWriter.addCall(variants, conditionParameters);
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
			resultWriter.addLine(line);
			copiedVariants++;
		}
	}

}
