package lib.io.result;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import lib.data.AbstractData;

public abstract class AbstractResultFileWriter<T extends AbstractData> 
extends AbstractResultWriter<T> {

	private BufferedWriter bw;
	
	public AbstractResultFileWriter(final String filename, final AbstractResultFormat<T> format) {
		super(filename, format);
		try {
			bw = new BufferedWriter(new FileWriter(new File(filename)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Helper
	 * @param s Add one line "s" to BufferedWriter
	 */
	public void addLine(final String s) {
		try {
			bw.write(s + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void close() {
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
