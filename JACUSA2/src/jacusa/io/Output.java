package jacusa.io;

import java.io.IOException;

public interface Output {

	String getName();
	String getInfo();
	
	void write(String line) throws IOException;
	void close() throws IOException;
	
}
