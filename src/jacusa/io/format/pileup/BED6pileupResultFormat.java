package jacusa.io.format.pileup;

import jacusa.io.format.DefaultDataAdder;
import lib.cli.parameter.GeneralParameter;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBCC;
import lib.io.AbstractResultFileFormat;
import lib.io.BEDlikeResultFileWriter;
import lib.io.BEDlikeResultFileWriter.BEDlikeResultFileWriterBuilder;
import lib.io.InputOutput;
import lib.io.format.bed.BED6adder;
import lib.io.format.bed.DataAdder;
import lib.io.format.bed.DefaultBED6adder;
import lib.io.format.bed.DefaultInfoAdder;

/**
 * This class implements an extended BED6 format to represent variants identified by "pileup" method.
 * It is very similar to BED6callResultFormat.
 */

public class BED6pileupResultFormat extends AbstractResultFileFormat {

	public static final char CHAR = 'B';
	
	public BED6pileupResultFormat(
			final String methodName, 
			final GeneralParameter parameter) {
		
		super(CHAR, "Default", methodName, parameter);
	}

	@Override
	public BEDlikeResultFileWriter createWriter(final String outputFileName) {
		final BaseCallCount.AbstractParser bccParser = 
				new DefaultBCC.Parser(InputOutput.VALUE_SEP, InputOutput.EMPTY_FIELD);

		return new BEDlikeResultFileWriterBuilder(outputFileName, getParameter())
				.addBED6Adder(new DefaultBED6adder(getMethodName(), "stat"))
				.addDataAdder(new DefaultDataAdder(bccParser))
				.addInfoAdder(new DefaultInfoAdder(getParameter()))
				.build();
	}
	
	// FIXME

}