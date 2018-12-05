package lib.cli.options;

import htsjdk.samtools.SamFiles;

import java.io.File;
import java.io.FileNotFoundException;

import lib.cli.parameter.ConditionParameter;

public class SAMPathnameArg {

	public static final char SEP = ',';

	private int conditionIndex;
	private ConditionParameter condition;
	
	public SAMPathnameArg(final int conditionIndex, ConditionParameter conditionParameter) {
		this.conditionIndex = conditionIndex;
		this.condition = conditionParameter;
	}

	public void processArg(String arg) throws FileNotFoundException {
		final String[] recordFilenames = arg.split(Character.toString(SEP));
    	for (String recordFilename : recordFilenames) {
	    	File file = new File(recordFilename);
	    	if (! file.exists()) {
	    		throw new FileNotFoundException("File (" + recordFilename + ") in not accessible!");
	    	}

	    	if (SamFiles.findIndex(file) == null) {
	    		throw new FileNotFoundException("Index for BAM file" + recordFilename + " is not accessible!");
	    	}
    	}
		condition.setRecordFilenames(recordFilenames);
	}

	public int getConditionIndex() {
		return conditionIndex;
	}
	
}
