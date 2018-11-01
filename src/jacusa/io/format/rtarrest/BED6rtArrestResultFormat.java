package jacusa.io.format.rtarrest;

import lib.cli.parameter.AbstractParameter;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBaseCallCount;
import lib.io.AbstractResultFileFormat;
import lib.io.BEDlikeResultFileWriter;
import lib.io.BEDlikeResultFileWriter.BEDlikeResultFileWriterBuilder;
import lib.io.format.bed.DefaultBED6adder;
import lib.io.format.bed.DefaultInfoAdder;
import lib.util.Util;

public class BED6rtArrestResultFormat 
extends AbstractResultFileFormat {

	public static final char CHAR = 'A';
	
	protected BED6rtArrestResultFormat(
			final char c,
			final String desc,
			final String methodName,
			final AbstractParameter parameter) {
		
		super(c, desc, methodName, parameter);
	}

	public BED6rtArrestResultFormat(
			final String methodName, 
			final AbstractParameter parameter) {
		
		this(CHAR, "Arrest only", methodName, parameter);
	}

	@Override
	public BEDlikeResultFileWriter createWriter(final String outputFileName) {
		final BaseCallCount.AbstractParser bccParser = 
				new DefaultBaseCallCount.Parser(Util.VALUE_SEP, Util.EMPTY_FIELD);
		
		return new BEDlikeResultFileWriterBuilder(outputFileName, getParameter())
				.addBED6Adder(
						new DefaultBED6adder(getMethodName(), "pvalue"))
				.addDataAdder(
						new RTarrestDataAdder(bccParser))
				.addInfoAdder(
						new DefaultInfoAdder(getParameter()))
				.addBaseSubstition(bccParser)
				.addFilterDebug()
				.build();
		
	}

}