package jacusa.io.format.call;

import java.util.ArrayList;
import java.util.List;

import jacusa.io.format.BaseSub2BCCadder;
import jacusa.io.format.StratifiedDataAdder;
import lib.cli.options.filter.has.BaseSub;
import lib.cli.parameter.GeneralParameter;
import lib.data.DataType;
import lib.data.count.BaseSub2BaseCallCount;
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
 * This class implements an extended BED6 format to represent variants
 * identified by "call" method.
 */
public class BED6BaseCallCountResultFormat extends AbstractResultFileFormat {

	// unique char id for CLI
	public static final char CHAR = 'B';

	private final String scoreLabel;

	public BED6BaseCallCountResultFormat(final String methodName, final GeneralParameter parameter, final String desc,
			final String scoreLabel) {

		super(CHAR, desc, methodName, parameter);
		this.scoreLabel = scoreLabel;
	}

	@Override
	public BEDlikeResultFileWriter createWriter(final String outputFileName) {
		final BaseCallCount.AbstractParser bccParser = new DefaultBCC.Parser(InputOutput.VALUE_SEP,
				InputOutput.EMPTY_FIELD);

		final BEDlikeResultFileWriterBuilder builder = new BEDlikeResultFileWriterBuilder(outputFileName,
				getParameter());

		final BED6adder bed6adder = new DefaultBED6adder(getMethodName(), scoreLabel);
		builder.addBED6Adder(bed6adder);

		DataAdder dataAdder = new BaseCallDataAdder(DataType.get("default", BaseCallCount.class), bccParser);
		if (!getParameter().getReadTags().isEmpty()) {
			final List<BaseSub> baseSubs = new ArrayList<>(getParameter().getReadTags());
			dataAdder = new StratifiedDataAdder(dataAdder, new BaseSub2BCCadder(
					DataType.get("default", BaseSub2BaseCallCount.class), bccParser, baseSubs, dataAdder));
		}
		builder.addDataAdder(dataAdder);

		builder.addInfoAdder(new DefaultInfoAdder(getParameter()));
		return builder.build();
	}

}
