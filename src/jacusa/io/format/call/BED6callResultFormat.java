package jacusa.io.format.call;

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
 * This class implements an extended BED6 format to represent variants identified by "call" method. 
 */
public class BED6callResultFormat extends AbstractResultFileFormat {

	// unique char id for CLI 
	public static final char CHAR = 'B';

	private final String scoreLabel;

	//Constructor for instantiation by inheriting class
	public BED6callResultFormat(
			final Character cha,
			final String desc,
			final String methodName, 
			final GeneralParameter parameter) {
		
		super(cha, desc, methodName, parameter);
		scoreLabel = "score";
	}

	//Constructor for direct instantiation
	public BED6callResultFormat(
			final String methodName,
			final GeneralParameter parameter) {

		super(CHAR, "BED6-generic result format", methodName, parameter);
		scoreLabel = "score";
	}

	@Override
	public BEDlikeResultFileWriter createWriter(final String outputFileName) {
		final BaseCallCount.AbstractParser bccParser = 
				new DefaultBCC.Parser(InputOutput.VALUE_SEP, InputOutput.EMPTY_FIELD);
		
		BED6adder bed6adder = new DefaultBED6adder(getMethodName(), scoreLabel);
		DataAdder dataAdder = new DefaultDataAdder(bccParser);
		final BEDlikeResultFileWriterBuilder builder = new BEDlikeResultFileWriterBuilder(outputFileName, getParameter());

		builder.addBED6Adder(bed6adder);
		builder.addDataAdder(dataAdder);
		builder.addInfoAdder(new DefaultInfoAdder(getParameter()));
		return builder.build();
	}

	@Override
	public void processCLI(String line) throws IllegalArgumentException {
		if (line != null && line.length() > 0) {
			throw new IllegalArgumentException("Options are not supported: " + line);
		}
	}

}
