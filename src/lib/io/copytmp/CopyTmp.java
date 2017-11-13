package lib.io.copytmp;

import java.io.IOException;

public interface CopyTmp {

	void nextIteration();
	void copy(final int iteration) throws IOException;

	void closeTmpReader() throws IOException;
	void closeTmpWriter() throws IOException;
	
}
