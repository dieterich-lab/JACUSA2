package lib.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class AbstractResultFileWriter implements ResultWriter {

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

	public String getInfo() {
		return filename;
	}

	/**
	 * Helper
	 * @param s Add one line "s" to BufferedWriter
	 */
	public void writeLine(final String s) {
		try {
			bw.write(s + '\n');
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void close() throws IOException {
		bw.close();
	}
	
}
