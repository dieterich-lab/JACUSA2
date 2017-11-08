package lib.io.variant;

import lib.io.AbstractFormat;

public abstract class AbstractVariantFormat 
extends AbstractFormat {

	public AbstractVariantFormat(final char c, final String desc) {
		super(c, desc);
	}

	public abstract AbstractVariantWriter createWriterInstance(final String filename);

}
