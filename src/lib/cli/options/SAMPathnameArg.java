package lib.cli.options;

import htsjdk.samtools.SamFiles;

import java.io.File;
import java.io.FileNotFoundException;

import lib.cli.parameter.ConditionParameter;

public class SAMPathnameArg {

	public static final char SEP = ',';

	private int condI;
	private ConditionParameter condition;
	
	public SAMPathnameArg(final int condI, ConditionParameter conditionParameter) {
		this.condI = condI;
		this.condition = conditionParameter;
	}

	/**
	 * Tested @see test.lib.cli.options.SAMPathnameArgTest
	 */
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

	public int getcondI() {
		return condI;
	}
	
}
