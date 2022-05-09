package jacusa.io.format.pileup;

import lib.cli.parameter.GeneralParameter;
import lib.data.DataType;
import lib.data.count.PileupCount;
import lib.io.AbstractResultFileFormat;
import lib.io.ResultWriter;

/**
 * This class implements the pileup format that is 
 * similar to samtools mpileup (base columns without: $ ^ < > *).
 *
 * @param <T>
 */
public class PileupLikeFormat 
extends AbstractResultFileFormat {

	// unique char id for CLI
	public static final char CHAR = 'M';
	
	public PileupLikeFormat(
			final String methodName, 
			final GeneralParameter parameter) {
		
		super(CHAR, "samtools mpileup like format (base columns without: $ ^ < > *)", methodName, parameter);
	}

	@Override
	public ResultWriter createWriter(String outputFileName) {
		final DataType<PileupCount> pileupCountDt = DataType.get("default", PileupCount.class);
		return new PileupResultWriter(pileupCountDt, outputFileName);
	}

}
