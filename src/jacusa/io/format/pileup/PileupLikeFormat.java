package jacusa.io.format.pileup;

import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.HasPileupCount;
import lib.data.result.DefaultResult;
import lib.io.AbstractResultFormat;
import lib.io.ResultWriter;

/**
 * This class implements the pileup format that is 
 * similar to samtools mpileup (base columns without: $ ^ < > *).
 *
 * @param <T>
 */
public class PileupLikeFormat<T extends AbstractData & HasPileupCount> 
extends AbstractResultFormat<T, DefaultResult<T>> {

	// unique char id for CLI
	public final static char CHAR = 'M';

	public PileupLikeFormat(final AbstractParameter<T, DefaultResult<T>> parameter) {
		super(CHAR, "samtools mpileup like format (base columns without: $ ^ < > *)", parameter);
	}

	@Override
	public ResultWriter<T, DefaultResult<T>> createWriter(String filename) {
		return new PileupPileupResultWriter<T, DefaultResult<T>>(filename, 
				getParameter().getBaseConfig(), getParameter().showReferenceBase());
	}

}
