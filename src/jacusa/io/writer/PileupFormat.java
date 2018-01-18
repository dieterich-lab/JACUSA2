package jacusa.io.writer;

import jacusa.io.copytmp.FileCopyTmpResult;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.hasPileupCount;
import lib.data.result.DefaultResult;
import lib.io.AbstractResultFileWriter;
import lib.io.AbstractResultFormat;
import lib.io.ResultWriter;
import lib.io.copytmp.CopyTmpResult;

public class PileupFormat<T extends AbstractData & hasPileupCount> 
extends AbstractResultFormat<T, DefaultResult<T>> {

	public final static char CHAR = 'M';

		private AbstractParameter<T, DefaultResult<T>> parameter;
	
	public PileupFormat(final AbstractParameter<T, DefaultResult<T>> parameter) {
		super(CHAR, "samtools mpileup like format (base columns without: $ ^ < > *)");
		this.parameter = parameter;
	}

	@Override
	public ResultWriter<T, DefaultResult<T>> createWriter(String filename) {
		return new PileupResultWriter<T, DefaultResult<T>>(filename, parameter.getBaseConfig(), parameter.showReferenceBase());
	}
	
	@Override
	public CopyTmpResult<T, DefaultResult<T>> createCopyTmp(final int threadId) {
		return new FileCopyTmpResult<T, DefaultResult<T>>(threadId, 
				(AbstractResultFileWriter<T, DefaultResult<T>>)parameter.getResultWriter(), this);
	}
	
}