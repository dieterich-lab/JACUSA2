package jacusa.io.format.call;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jacusa.io.format.BaseSubstitutionBED6adder;
import jacusa.io.format.BaseSubstitutionDeletionCountAdder;
import jacusa.io.format.BaseSubstitution2BaseCallCountAdder;
import jacusa.io.format.CombinedDataAdder;
import jacusa.io.format.DeletionCountDataAdder;
import jacusa.io.format.StratifiedDataAdder;
import lib.cli.options.filter.has.HasReadSubstitution.BaseSubstitution;
import lib.cli.parameter.GeneralParameter;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBaseCallCount;
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
				new DefaultBaseCallCount.Parser(InputOutput.VALUE_SEP, InputOutput.EMPTY_FIELD);
		
		BED6adder bed6adder = new DefaultBED6adder(getMethodName(), "stat");
		DataAdder dataAdder = new CallDataAdder(bccParser);
		final BEDlikeResultFileWriterBuilder builder = new BEDlikeResultFileWriterBuilder(outputFileName, getParameter());
		
		if (getParameter().getReadSubstitutions().size() > 0) {
			final List<BaseSubstitution> baseSubs = new ArrayList<>(getParameter().getReadSubstitutions());
			bed6adder = new BaseSubstitutionBED6adder(baseSubs, bed6adder);
			dataAdder = new StratifiedDataAdder(
					dataAdder, 
					new BaseSubstitution2BaseCallCountAdder(bccParser, baseSubs, dataAdder));
			if (getParameter().showDeletionCount()) {
				final DataAdder delDataAder = new DeletionCountDataAdder();
				builder.addDataAdder(
						new CombinedDataAdder(
								Arrays.asList(								
										dataAdder,
										new StratifiedDataAdder(
											delDataAder, 
											new BaseSubstitutionDeletionCountAdder(baseSubs, delDataAder)))));
			}
		} else {
			if (getParameter().showDeletionCount()) {
				builder.addDataAdder(new CombinedDataAdder(Arrays.asList(dataAdder, new DeletionCountDataAdder())));
			} else {
				builder.addDataAdder(dataAdder);
			}	
		}

		builder.addBED6Adder(bed6adder);
		builder.addInfoAdder(new DefaultInfoAdder(getParameter()));
		return builder.build();
	}

}
