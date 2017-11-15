package lib.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import lib.data.AbstractData;
import lib.data.result.Result;

public abstract class AbstractResultFileWriter<T extends AbstractData, R extends Result<T>> 
implements ResultWriter<T, R> {

	// TODO add gzip
	private final String filename;
	private BufferedWriter bw;
	
	public AbstractResultFileWriter(final String filename) {
		this.filename = filename;
		try {
			bw = new BufferedWriter(new FileWriter(new File(filename)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getFilename() {
		return filename;
	}

	/**
	 * Helper
	 * @param s Add one line "s" to BufferedWriter
	 */
	public void addLine(final String s) {
		try {
			bw.write(s + '\n');
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected BufferedWriter getBW() {
		return bw;
	}


	@Override
	public String getInfo() {
		return getFilename();
	}
	
}
