package jacusa.io.format.lrtarrest;

import jacusa.io.copytmp.SerializeCopyTmpResult;
import lib.cli.parameter.GeneralParameter;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBaseCallCount;
import lib.io.AbstractResultFormat;
import lib.io.BEDlikeResultFileWriter;
import lib.io.BEDlikeResultFileWriter.BEDlikeResultFileWriterBuilder;
import lib.io.InputOutput;
import lib.io.copytmp.CopyTmpResult;
import lib.io.format.bed.DefaultBED6adder;
import lib.io.format.bed.DefaultInfoAdder;
import lib.worker.WorkerDispatcher;

public class BED6lrtArrestResultFormat 
extends AbstractResultFormat {

	public static final char CHAR = 'L';
	
	protected BED6lrtArrestResultFormat(
			final char c, final String desc,
			final String methodName,
			final GeneralParameter parameter) {
		super(c, desc, methodName, parameter);
	}

	public BED6lrtArrestResultFormat(
			final String methodName, 
			final GeneralParameter parameter) {

		this(CHAR, "Linkage arrest to base substitution", methodName, parameter);
	}

	@Override
	public BEDlikeResultFileWriter createWriter(final String outputFileName) {
		final BaseCallCount.AbstractParser bccParser = 
				new DefaultBaseCallCount.Parser(InputOutput.VALUE_SEP, InputOutput.EMPTY_FIELD);
		
		return new BEDlikeResultFileWriterBuilder(outputFileName, getParameter())
				.addBED6Adder(
						new LRTarrestBED6adder(
								new DefaultBED6adder(getMethodName(), "stat")))
				.addDataAdder(
						new LRTarrestDataAdder(bccParser))
				.addInfoAdder(
						new DefaultInfoAdder(getParameter()))
				.build();
	}

	@Override
	public CopyTmpResult createCopyTmp(
			final int threadId,
			final WorkerDispatcher workerDispatcher) {

		return new SerializeCopyTmpResult(threadId, workerDispatcher.getResultWriter(), this);
	}
	
}