package lib.io.record;

import lib.cli.parameters.AbstractConditionParameter;

public class FASTQRecordFormat 
extends AbstractRecordFormat {

	public static final char CHAR = 'F';

	public FASTQRecordFormat() {
		super(CHAR, "FASTQ output");
	}
	
	@Override
	public AbstractRecordWriter createWriterInstance(final AbstractConditionParameter<?> conditionParameter) {
		// TODO return new FASTQRecordWriter(conditionParameter.getRecordFilename(), this);
		return null;
	}

	@Override
	public String getSuffix() {
		return "fastq";
	}
	
}
