package jacusa.io.copytmp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lib.data.result.Result;
import lib.io.AbstractResultFileWriter;
import lib.io.ResultFormat;
import lib.io.ResultWriter;
import lib.io.copytmp.CopyTmpResult;
import lib.util.AbstractTool;

/**
 * TODO add comments.
 *
 * @param 
 */
public class FileCopyTmpResult 
implements CopyTmpResult {

	// 
	private final AbstractResultFileWriter resultFileWriter;

	// temporary result files are written by this object
	private ResultWriter tmpResultWriter;
	// temporary result files are read by this object
	private BufferedReader tmpResultReader;

	// stores the number of stored result 
	// per iteration/window for this thread 
	private final List<Integer> iteration2storedResults;
	
	public FileCopyTmpResult(final int threadId, 
			final AbstractResultFileWriter resultFileWriter, 
			final ResultFormat resultFormat) {
		
		this.resultFileWriter = resultFileWriter; 
		try {
			// create temporary file for this thread
			final String tmpResultFilename = createTmpResultFilename(threadId);
			// create writer for temporary file
			tmpResultWriter = resultFormat.createWriter(tmpResultFilename);
			// create reader for temporary file
			tmpResultReader = new BufferedReader(new FileReader(new File(tmpResultFilename)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		iteration2storedResults = new ArrayList<Integer>(1000);
	}
	
	/**
	 * Helper function - create temporary file for a specific thread.
	 * TODO add comments.
	 * 
	 * @param threadId 
	 * @return
	 * @throws IOException
	 */
	private String createTmpResultFilename(final int threadId) throws IOException {
		// default prefix
		final String prefix = "jacusa2_" + threadId + "_";
		// use OS to place temporary files
		final File file = File.createTempFile(prefix, ".gz");

		if (! AbstractTool.getLogger().isDebug()) {
			// don't delete tmp files when in debug mode
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

	@Override
	public void addResult(final Result result) throws Exception {
		tmpResultWriter.writeResult(result);
		incrementStoredResults(result.getValues());
	}
	
	private void incrementStoredResults(final int lines) {
		// get current iteration
		final int iteration = iteration2storedResults.size() - 1;
		// increment counter for current iteration
		final int storedResults = iteration2storedResults.get(iteration) + lines;
		// and update iteration
		iteration2storedResults.set(iteration, storedResults);
	}
	
	@Override
	public void copy(int iteration) throws IOException {
		int copiedResults = 0;
		final int storedResults = iteration2storedResults.get(iteration);

		String line;
		while (storedResults > copiedResults && (line = tmpResultReader.readLine()) != null) {
			resultFileWriter.writeLine(line);
			copiedResults++;
		}
	}

}
