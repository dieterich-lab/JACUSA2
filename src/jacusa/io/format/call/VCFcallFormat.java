package jacusa.io.format.call;

import java.io.IOException;

import htsjdk.samtools.SAMSequenceDictionary;
import jacusa.io.copytmp.SerializeCopyTmpResult;
import lib.cli.parameter.GeneralParameter;
import lib.io.AbstractResultFormat;
import lib.io.ResultWriter;
import lib.io.copytmp.CopyTmpResult;
import lib.util.Util;
import lib.worker.WorkerDispatcher;

/**
 * Implements the VCF file format.
 */
public class VCFcallFormat extends AbstractResultFormat {

	// unique char id for CLI
	public static final char CHAR = 'V';
	private SAMSequenceDictionary dictionary; 
	
	public VCFcallFormat(final GeneralParameter parameter) {
		super(CHAR, "VCF Output format. Option -P will be ignored (VCF is unstranded)", new String(), parameter);
	}

	@Override
	public ResultWriter createWriter(String outputFileName) {
		if (dictionary == null) {
			try {
				dictionary = Util.getSAMSequenceDictionary(
						getParameter().getConditionParameter(0).getRecordFilenames()[0]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new VCFcallWriter(
				outputFileName, 
				getParameter().getFilterConfig(),
				dictionary); 
	}
	
	@Override
	public CopyTmpResult createCopyTmp(int threadId, final WorkerDispatcher workerDispatcher) {
		return new SerializeCopyTmpResult(threadId, workerDispatcher.getResultWriter(), this);
	}

}