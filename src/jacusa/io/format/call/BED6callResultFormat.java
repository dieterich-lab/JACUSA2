package jacusa.io.format.call;

import lib.cli.parameter.AbstractParameter;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBaseCallCount;
import lib.io.AbstractResultFileFormat;
import lib.io.BEDlikeResultFileWriter;
import lib.io.BEDlikeResultFileWriter.BEDlikeResultFileWriterBuilder;
import lib.io.format.bed.DefaultBED6adder;
import lib.io.format.bed.DefaultInfoAdder;
import lib.util.Util;

/**
 * This class implements an extended BED6 format to represent variants identified by "call" method. 
 *
 * @param <T>
 * @param <R>
 */
public class BED6callResultFormat 
extends AbstractResultFileFormat {

	// unique char id for CLI 
	public static final char CHAR = 'B';

	public BED6callResultFormat(
			final String methodName, 
			final AbstractParameter parameter) {
		
		super(CHAR, "BED6-extended result format", methodName, parameter);
	}

	@Override
	public BEDlikeResultFileWriter createWriter(final String outputFileName) {
		final BaseCallCount.AbstractParser bccParser = 
				new DefaultBaseCallCount.Parser(Util.VALUE_SEP, Util.EMPTY_FIELD);
		
		return new BEDlikeResultFileWriterBuilder(outputFileName, getParameter())
				.addBED6Adder(
						new DefaultBED6adder(getMethodName(), "stat"))
				.addDataAdder(
						new CallDataAdder(bccParser))
				.addInfoAdder(
						new DefaultInfoAdder(getParameter()))
				.addBaseSubstition(bccParser)
				.addFilterDebug()
				.build();
	}

}
