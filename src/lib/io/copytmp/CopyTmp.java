package lib.io.copytmp;

import java.io.Closeable;
import java.io.IOException;

public interface CopyTmp extends Closeable {

	public void nextIteration();
	public void copy(final int iteration) throws IOException;

}
