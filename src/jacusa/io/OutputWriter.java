package jacusa.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class OutputWriter implements Output {

	private String filename;
	private BufferedWriter bw;
	
	public OutputWriter(String filename) throws IOException {
		this.filename = filename;
		bw = new BufferedWriter(new FileWriter(new File(filename)));
	}

	public OutputWriter(File file) throws IOException {
		this.filename = file.getAbsolutePath();
		bw = new BufferedWriter(new FileWriter(file));
	}

	@Override
	public String getName() {
		return "Default";
	}

	@Override
	public String getInfo() {
		return filename;
	}

	@Override
	public void write(String line) throws IOException {
		bw.write(line + "\n");
	}

	@Override
	public void close() throws IOException {
		if(bw != null) {
			bw.close();
			bw = null;
		}
	}

}
