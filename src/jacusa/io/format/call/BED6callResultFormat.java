package jacusa.io.format.call;

import java.util.ArrayList;
import java.util.List;

import jacusa.io.format.BaseSubstitutionBED6adder;
import jacusa.io.format.BaseSubstitutionDataAdder;
import jacusa.io.format.StratifiedDataAdder;
import lib.cli.options.has.HasReadSubstitution.BaseSubstitution;
import lib.cli.parameter.GeneralParameter;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBaseCallCount;
import lib.io.AbstractResultFileFormat;
import lib.io.BEDlikeResultFileWriter;
import lib.io.BEDlikeResultFileWriter.BEDlikeResultFileWriterBuilder;
import lib.io.format.bed.BED6adder;
import lib.io.format.bed.DataAdder;
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
			final GeneralParameter parameter) {
		
		super(CHAR, "BED6-extended result format", methodName, parameter);
	}

	@Override
	public BEDlikeResultFileWriter createWriter(final String outputFileName) {
		final BaseCallCount.AbstractParser bccParser = 
				new DefaultBaseCallCount.Parser(Util.VALUE_SEP, Util.EMPTY_FIELD);
		
		BED6adder bed6adder = new DefaultBED6adder(getMethodName(), "stat");
		DataAdder dataAdder = new CallDataAdder(bccParser); 
		if (getParameter().getReadSubstitutions().size() > 0) {
			final List<BaseSubstitution> baseSubs = new ArrayList<>(getParameter().getReadSubstitutions()); 
			bed6adder = new BaseSubstitutionBED6adder(baseSubs, bed6adder);
			dataAdder = new StratifiedDataAdder(
					dataAdder, 
					new BaseSubstitutionDataAdder(bccParser, baseSubs, dataAdder));
		}
		
		return new BEDlikeResultFileWriterBuilder(outputFileName, getParameter())
				.addBED6Adder(bed6adder)
				.addDataAdder(dataAdder)
				.addInfoAdder(new DefaultInfoAdder(getParameter()))
				.build();
	}

}
