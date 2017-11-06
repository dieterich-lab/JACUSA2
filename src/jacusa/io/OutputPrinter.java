package jacusa.io;

import java.io.IOException;

public class OutputPrinter implements Output {

	public OutputPrinter() {}
	
	@Override
	public String getName() {
		return "STDOUT";
	}

	@Override
	public String getInfo() {
		return getName();
	}

	@Override
	public void write(String line) throws IOException {
		System.out.println(line);
	}

	@Override
	public void close() throws IOException {}

}
