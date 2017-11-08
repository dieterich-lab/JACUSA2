package lib.io.variant;

public class VCFVariantFormat 
extends AbstractVariantFormat {

	public static final char CHAR = 'V';

	public VCFVariantFormat() {
		super(CHAR, "VCF variant output");
	}

	@Override
	public VCFVariantWriter createWriterInstance(final String filename) {
		return new VCFVariantWriter(filename, this);
	}
	
	@Override
	public String getSuffix() {
		return "vcf";
	}
}
