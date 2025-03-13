package jacusa.io.format;

import lib.cli.parameter.GeneralParameter;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBCC;
import lib.io.AbstractResultFileFormat;
import lib.io.BEDlikeResultFileWriter;
import lib.io.BEDlikeResultFileWriter.BEDlikeResultFileWriterBuilder;
import lib.io.InputOutput;
import lib.io.format.bed.DefaultBED6adder;
import lib.io.format.bed.DefaultInfoAdder;

/**
 * This class implements an extended BED6 format to represent variants identified by "call" method. 
 */
public class BED6resultFormat extends AbstractResultFileFormat {

	// unique char id for CLI 
	public static final char CHAR = 'B';

	//Constructor for instantiation by inheriting class
	public BED6resultFormat(
			final Character cha,
			final String desc,
			final String methodName, 
			final GeneralParameter parameter) {
		
		super(cha, desc, methodName, parameter);
	}

	//Constructor for direct instantiation
	public BED6resultFormat(
			final String methodName,
			final GeneralParameter parameter) {

		super(CHAR, "BED6-generic result format", methodName, parameter);
	}

	@Override
	public BEDlikeResultFileWriter createWriter(final String outputFileName) {
		final BaseCallCount.AbstractParser bccParser = 
				new DefaultBCC.Parser(InputOutput.VALUE_SEP, InputOutput.EMPTY_FIELD);
		
		return new BEDlikeResultFileWriterBuilder(outputFileName, getParameter())
				.addBED6Adder(new DefaultBED6adder(getMethodName(), "score"))
				.addDataAdder(new DefaultDataAdder(bccParser))
				.addInfoAdder(new DefaultInfoAdder(getParameter()))
				.build();
	}

	@Override
	public void processCLI(String line) throws IllegalArgumentException {
		if (line != null && line.length() > 0) {
			throw new IllegalArgumentException("Options are not supported: " + line);
		}
	}

}
