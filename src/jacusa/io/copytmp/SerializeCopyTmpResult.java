package jacusa.io.copytmp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import lib.data.result.Result;
import lib.io.ResultFormat;
import lib.io.ResultWriter;
import lib.io.copytmp.CopyTmpResult;
import lib.util.AbstractTool;

/**
 * TODO add comments.
 *
 * @param <T>
 * @param <R>
 */
public class SerializeCopyTmpResult 
implements CopyTmpResult {

	// 
	private final ResultWriter resultWriter;

	// temporary result files are written by this object
	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;

	// stores the number of stored result 
	// per iteration/window for this thread 
	private final List<Integer> iteration2storedResults;
	
	private String tmpResultFilename;

	public SerializeCopyTmpResult(final int threadId, 
			final ResultWriter resultFileWriter, 
			final ResultFormat resultFormat) {
	
		// final writer
		this.resultWriter = resultFileWriter;

		try {
			// create temporary file for this thread
			tmpResultFilename = createTmpResultFilename(threadId);
			// write object(s)
			final FileOutputStream fos 		= new FileOutputStream(tmpResultFilename);
			final BufferedOutputStream bos 	= new BufferedOutputStream(fos);
			objectOutputStream				= new ObjectOutputStream(bos);
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
		final File file = File.createTempFile(prefix, ".tmp");

		if (! AbstractTool.getLogger().isDebug()) {
			// don't delete tmp files when in debug mode
			file.deleteOnExit();
		}
		return file.getCanonicalPath();
	}
	
	@Override
	public void closeTmpReader() throws IOException {
		if (objectInputStream != null) {
			objectInputStream.close();
		}
	}
	
	@Override
	public void closeTmpWriter() throws IOException {
		if (objectOutputStream != null) {
			objectOutputStream.close();
		}
	}

	@Override
	public void newIteration() {
		iteration2storedResults.add(0);
	}

	@Override
	public void addResult(final Result result) throws Exception {
		objectOutputStream.writeObject(result);
		incrementStoredResults(1);
	}
	
	private void incrementStoredResults(final int results) {
		// get current iteration
		final int iteration = iteration2storedResults.size() - 1;
		// increment counter for current iteration
		final int storedResults = iteration2storedResults.get(iteration) + results;
		// and update iteration
		iteration2storedResults.set(iteration, storedResults);
	}
	
	@Override
	public void copy(int iteration) throws IOException {
		if (objectInputStream == null) {
			// read object(s)
			final FileInputStream fis 		= new FileInputStream(tmpResultFilename);
			final BufferedInputStream bis 	= new BufferedInputStream(fis);
			objectInputStream 				= new ObjectInputStream(bis);
		}
		
		int copiedResults = 0;
		final int storedResults = iteration2storedResults.get(iteration);
		
		while (storedResults > copiedResults) {
			try {
				final Object obj = objectInputStream.readObject();
				final Result result = (Result)obj;
				// TODO filter incomplete results
				resultWriter.writeResult(result);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			copiedResults++;
		}
	}

}
