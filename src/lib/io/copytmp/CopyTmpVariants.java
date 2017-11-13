package lib.io.copytmp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.io.variant.AbstractVariantWriter;
import lib.util.AbstractTool;
import lib.variant.Variant;

public class CopyTmpVariants implements CopyTmp {

	private final AbstractVariantWriter variantWriter;
	
	private final AbstractVariantWriter tmpVariantWriter;
	private final BufferedReader tmpVariantReader;
	
	private List<Integer> iteration2storedVariants;
	
	public CopyTmpVariants(final int threadId, final AbstractVariantWriter variantWriter) throws IOException {
		this.variantWriter = variantWriter;
		
		final String tmpVariantFilename = createTmpVariantFilename(threadId);
		tmpVariantWriter = variantWriter.getFormat().createWriterInstance(tmpVariantFilename);
		tmpVariantReader = new BufferedReader(new FileReader(new File(tmpVariantFilename)));
		
		iteration2storedVariants = new ArrayList<Integer>();
	}
	
	private String createTmpVariantFilename(final int threadId) throws IOException {
		final String prefix = "variant_" + threadId + "_";
		final File file = File.createTempFile(prefix, ".gz");
		if (! AbstractTool.getLogger().isDebug()) {
			file.deleteOnExit();
		}
		return file.getCanonicalPath();
	}
	
	@Override
	public void closeTmpReader() throws IOException {
		// FIXME	
	}
	
	@Override
	public void closeTmpWriter() throws IOException {
		// FIXME
	}

	@Override
	public void nextIteration() {
		iteration2storedVariants.add(0);		
	}

	public void addVariants(Variant[] variants, List<AbstractConditionParameter<?>> conditionParameters) throws Exception {
		tmpVariantWriter.addVariants(variants, conditionParameters);
		final int iteration = iteration2storedVariants.size() - 1;
		final int storedVariants = iteration2storedVariants.get(iteration) + 1;
		iteration2storedVariants.set(iteration, storedVariants);
	}
	
	@Override
	public void copy(int iteration) throws IOException {
		int copiedVariants = 0;
		final int storedVariants = iteration2storedVariants.get(iteration);

		String line;
		while (storedVariants >= copiedVariants && (line = tmpVariantReader.readLine()) != null) {
			variantWriter.addLine(line);
			copiedVariants++;
		}
	}

}
