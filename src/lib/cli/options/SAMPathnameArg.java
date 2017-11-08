package lib.cli.options;

import htsjdk.samtools.SamFiles;

import java.io.File;
import java.io.FileNotFoundException;

import lib.cli.parameters.AbstractConditionParameter;

public class SAMPathnameArg {

	public static final char SEP = ',';

	private int conditionIndex;
	private AbstractConditionParameter<?> condition;
	
	public SAMPathnameArg(final int conditionIndex, AbstractConditionParameter<?> paramteres) {
		this.conditionIndex = conditionIndex;
		this.condition = paramteres;
	}

	public void processArg(String arg) throws Exception {
		final String[] recordFilenames = arg.split(Character.toString(SEP));
    	for (String recordFilename : recordFilenames) {
	    	File file = new File(recordFilename);
	    	if (! file.exists()) {
	    		throw new FileNotFoundException("File (" + recordFilename + ") in not accessible!");
	    	}

	    	if (SamFiles.findIndex(file) == null) {
	    		throw new FileNotFoundException("Index for BAM file" + conditionIndex + " is not accessible!");
	    	}
    	}
    	// beware of ugly code
		condition.setRecordFilenames(recordFilenames);
	}

}
