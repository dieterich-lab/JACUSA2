package jacusa.pileup.builder.inverted;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.BaseQualData;
import jacusa.pileup.builder.AbstractDataBuilder;
import jacusa.pileup.builder.AbstractStrandedPileupBuilder;
import jacusa.pileup.builder.hasLibraryType.LIBRARY_TYPE;
import jacusa.util.WindowCoordinate;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;

/**
 * @author Michael Piechotta
 *
 */
public class FRPairedEnd1InvertedPileupBuilder<T extends BaseQualData> 
extends AbstractStrandedPileupBuilder<T> {

	public FRPairedEnd1InvertedPileupBuilder(
			final WindowCoordinate windowCoordinate, 
			final SAMFileReader reader, 
			final ConditionParameters<T> condition,
			final AbstractParameters<T> parameters) {
		super(windowCoordinate, reader, condition, parameters, 
				LIBRARY_TYPE.FR_FIRSTSTRAND);
	}

	// invert
	public void processRecord(SAMRecord record) {
		AbstractDataBuilder<T> dataBuilder = null;
		
		if (record.getReadPairedFlag()) { // paired end
			if (record.getFirstOfPairFlag() && record.getReadNegativeStrandFlag() || 
					record.getSecondOfPairFlag() && ! record.getReadNegativeStrandFlag()) {
				dataBuilder = getReverse();
			} else {
				dataBuilder = getForward();
			}
		} else {
			if (record.getReadNegativeStrandFlag()) {
				dataBuilder = getReverse();
			} else {
				dataBuilder = getForward();
			}
		}
		
		dataBuilder.processRecord(record);
	}
	
}
