package lib.io;

import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;

public abstract class AbstractWriter 
implements Cloneable {

	private String filename;
	private AbstractFormat format;

	public AbstractWriter(final String filename, AbstractFormat format) {
		this.filename = filename;
		this.format = format;
	}

	public String getFilename() {
		return filename;
	}

	public AbstractFormat getFormat() {
		return format;
	}

	public abstract void addHeader(final List<AbstractConditionParameter<?>> conditions); 
	public abstract void close();
	
}
