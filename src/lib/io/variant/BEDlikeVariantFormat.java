package lib.io.variant;

public class BEDlikeVariantFormat extends AbstractVariantFormat {

	public static final char CHAR = 'B';

	public BEDlikeVariantFormat() {
		super(CHAR, "BEDlike variant output");
	}

	@Override
	public BEDlikeVariantWriter createWriterInstance(final String filename) {
		return new BEDlikeVariantWriter(filename, this);
	}

	@Override
	public String getSuffix() {
		return "bed";
	}
	
}
