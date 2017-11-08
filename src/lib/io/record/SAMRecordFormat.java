package lib.io.record;

import lib.cli.parameters.AbstractConditionParameter;
import htsjdk.samtools.SAMFileHeader;

public class SAMRecordFormat extends AbstractRecordFormat {

	public static final char CHAR = 'S';
	
	public SAMRecordFormat() {
		super(CHAR, "SAM output");
	}
	
	@Override
	public SAMRecordWriter createWriterInstance(final AbstractConditionParameter<?> conditionParameter) {
		// TODO final String filename = conditionParameter.getRecordFilename();
		final String filename = ""; // TODO
		final SAMFileHeader header = conditionParameter.getSAMFileReader().get(0).getFileHeader();
		return new SAMRecordWriter(filename, this, header);
	}

	@Override
	public String getSuffix() {
		return "sam";
	}
	
}