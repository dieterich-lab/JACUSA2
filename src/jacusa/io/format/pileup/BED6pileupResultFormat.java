package jacusa.io.format.pileup;

import lib.cli.parameter.AbstractParameter;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBaseCallCount;
import lib.io.AbstractResultFileFormat;
import lib.io.BEDlikeResultFileWriter;
import lib.io.BEDlikeResultFileWriter.BEDlikeResultFileWriterBuilder;
import lib.io.format.bed.DefaultBED6adder;
import lib.io.format.bed.DefaultInfoAdder;
import lib.util.Util;

public class BED6pileupResultFormat 
extends AbstractResultFileFormat {

	public static final char CHAR = 'B';
	
	public BED6pileupResultFormat(
			final String methodName, 
			final AbstractParameter parameter) {
		
		super(CHAR, "Default", methodName, parameter);
	}

	@Override
	public BEDlikeResultFileWriter createWriter(final String outputFileName) {
		final BaseCallCount.AbstractParser bccParser = 
				new DefaultBaseCallCount.Parser(Util.VALUE_SEP, Util.EMPTY_FIELD);
		
		return new BEDlikeResultFileWriterBuilder(outputFileName, getParameter())
				.addBED6Adder(
						new DefaultBED6adder(getMethodName(), "stat"))
				.addDataAdder(
						new PileupDataAdder(bccParser))
				.addInfoAdder(
						new DefaultInfoAdder(getParameter()))
				.addBaseSubstition(bccParser)
				.addFilterDebug()
				.build();

	}

}