package jacusa.io.writer;

import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.hasPileupCount;
import lib.data.result.DefaultResult;
import lib.io.AbstractResultFormat;
import lib.io.ResultWriter;

/**
 * This class implements the pileup format that is 
 * similar to samtools mpileup (base columns without: $ ^ < > *).
 *
 * @param <T>
 */
public class PileupFormat<T extends AbstractData & hasPileupCount> 
extends AbstractResultFormat<T, DefaultResult<T>> {

	// unique char id for CLI
	public final static char CHAR = 'M';

	public PileupFormat(final AbstractParameter<T, DefaultResult<T>> parameter) {
		super(CHAR, "samtools mpileup like format (base columns without: $ ^ < > *)", parameter);
	}

	@Override
	public ResultWriter<T, DefaultResult<T>> createWriter(String filename) {
		return new PileupResultWriter<T, DefaultResult<T>>(filename, 
				getParameter().getBaseConfig(), getParameter().showReferenceBase());
	}

}
