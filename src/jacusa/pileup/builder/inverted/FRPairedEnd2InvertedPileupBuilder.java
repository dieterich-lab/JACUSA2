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
public class FRPairedEnd2InvertedPileupBuilder<T extends BaseQualData> 
extends AbstractStrandedPileupBuilder<T> {

	public FRPairedEnd2InvertedPileupBuilder(
			final WindowCoordinate windowCoordinates, 
			final SAMFileReader reader, 
			final ConditionParameters<T> condition,
			final AbstractParameters<T> parameters) {
		super(windowCoordinates, reader, condition, parameters, LIBRARY_TYPE.FR_SECONDSTRAND);
	}

	// invert
	public void processRecord(SAMRecord record) {
		AbstractDataBuilder<T> dataBuilder = null;
		
		if (record.getReadPairedFlag()) { // paired end
			if (record.getFirstOfPairFlag() && record.getReadNegativeStrandFlag() || 
					record.getSecondOfPairFlag() && ! record.getReadNegativeStrandFlag() ) {
				dataBuilder = getForward();
			} else {
				dataBuilder = getReverse();
			}
		} else {
			if (record.getReadNegativeStrandFlag()) {
				dataBuilder = getForward();
			} else {
				dataBuilder = getReverse();
			}
		}
		
		dataBuilder.processRecord(record);
	}

}