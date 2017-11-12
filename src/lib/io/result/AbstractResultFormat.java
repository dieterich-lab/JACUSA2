package lib.io.result;

import lib.data.AbstractData;
import lib.io.AbstractFormat;

public abstract class AbstractResultFormat<T extends AbstractData> 
extends AbstractFormat {

	public AbstractResultFormat(final char c, final String desc) {
		super(c, desc);
	}

	public abstract AbstractResultWriter<T> createWriterInstance(final String filename);

}
