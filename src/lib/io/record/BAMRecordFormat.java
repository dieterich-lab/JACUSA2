package lib.io.record;

import lib.cli.parameters.AbstractConditionParameter;

public class BAMRecordFormat 
extends AbstractRecordFormat {

	public static final char CHAR = 'B';
	public static final String DESCRIPTION = "BAM output";
	
	public BAMRecordFormat() {
		super(CHAR, DESCRIPTION);
	}

	@Override
	public BAMRecordWriter createWriterInstance(final AbstractConditionParameter<?> conditionParameter) {
		// TODO final String filename = conditionParameter.getRecordFilename();
		// final String filename = null; // TODO
		// TODO final SAMFileHeader header = conditionParameter.getSAMFileReader().get(0).getFileHeader();
		// return new BAMRecordWriter(filename, this, header);
		return null;
	}
	
	@Override
	public String getSuffix() {
		return "bam";
	}

}