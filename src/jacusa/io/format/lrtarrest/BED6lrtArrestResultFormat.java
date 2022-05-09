package jacusa.io.format.lrtarrest;

import jacusa.io.copytmp.SerializeCopyTmpResult;
import lib.cli.parameter.GeneralParameter;
import lib.data.DataType;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBCC;
import lib.data.storage.lrtarrest.ArrestPosition2BaseCallCount;
import lib.io.AbstractResultFormat;
import lib.io.BEDlikeResultFileWriter;
import lib.io.BEDlikeResultFileWriter.BEDlikeResultFileWriterBuilder;
import lib.io.InputOutput;
import lib.io.copytmp.CopyTmpResult;
import lib.io.format.bed.DefaultBED6adder;
import lib.io.format.bed.DefaultInfoAdder;
import lib.worker.WorkerDispatcher;

/**
 * This class implements an extended BED6 format to represent linked variants
 * and read arrest events by "lrt-arrest" method. A result consists of multiple
 * lines with identical BED6 genome coordinates but different "arrest_pos"
 * column.
 */
public class BED6lrtArrestResultFormat extends AbstractResultFormat {

	public static final char CHAR = 'L';

	protected BED6lrtArrestResultFormat(final char c, final String desc, final String methodName,
			final GeneralParameter parameter) {
		super(c, desc, methodName, parameter);
	}

	public BED6lrtArrestResultFormat(final String methodName, final GeneralParameter parameter) {

		this(CHAR, "Linkage arrest to base substitution", methodName, parameter);
	}

	@Override
	public BEDlikeResultFileWriter createWriter(final String outputFileName) {
		final BaseCallCount.AbstractParser bccParser = new DefaultBCC.Parser(InputOutput.VALUE_SEP,
				InputOutput.EMPTY_FIELD);

		final DataType<ArrestPosition2BaseCallCount> ap2bccDt = DataType.get("default", ArrestPosition2BaseCallCount.class);
		return new BEDlikeResultFileWriterBuilder(outputFileName, getParameter())
				.addBED6Adder(new LRTarrestBED6adder(ap2bccDt, new DefaultBED6adder(getMethodName(), "pvalue")))
				.addDataAdder(new LRTarrestDataAdder(ap2bccDt, bccParser)).addInfoAdder(new DefaultInfoAdder(getParameter()))
				.build();
	}

	@Override
	public CopyTmpResult createCopyTmp(final int threadId, final WorkerDispatcher workerDispatcher) {

		return new SerializeCopyTmpResult(threadId, workerDispatcher.getResultWriter(), this);
	}

}
