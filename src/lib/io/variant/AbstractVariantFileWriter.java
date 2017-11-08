package lib.io.variant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class AbstractVariantFileWriter 
extends AbstractVariantWriter {

	private BufferedWriter bw;
	
	public AbstractVariantFileWriter(final String filename, final AbstractVariantFormat format) {
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
