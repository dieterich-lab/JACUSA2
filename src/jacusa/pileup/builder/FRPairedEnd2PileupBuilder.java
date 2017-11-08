package jacusa.pileup.builder;

import jacusa.pileup.builder.hasLibraryType.LIBRARY_TYPE;
import lib.cli.parameters.AbstractParameters;
import lib.cli.parameters.JACUSAConditionParameters;
import lib.data.BaseQualData;
import lib.util.WindowCoordinate;

import htsjdk.samtools.SamReader;
import htsjdk.samtools.SAMRecord;

/**
 * @author Michael Piechotta
 *
 */
public class FRPairedEnd2PileupBuilder<T extends BaseQualData> 
extends AbstractStrandedPileupBuilder<T> {

	public FRPairedEnd2PileupBuilder(
			final WindowCoordinate windowCoordinates, 
			final SamReader reader, 
			final JACUSAConditionParameters<T> condition,
			final AbstractParameters<T> parameters) {
		super(windowCoordinates, reader, condition, parameters, LIBRARY_TYPE.FR_SECONDSTRAND);
	}
	
	public void processRecord(SAMRecord record) {
		AbstractDataBuilder<T> dataBuilder = null;
		
		if (record.getReadPairedFlag()) { // paired end
			if (record.getFirstOfPairFlag() && record.getReadNegativeStrandFlag() || 
					record.getSecondOfPairFlag() && ! record.getReadNegativeStrandFlag() ) {
				dataBuilder = getReverse();
			} else {
				dataBuilder = getForward();
			}
		} else { // single end
			if (record.getReadNegativeStrandFlag()) {
				dataBuilder = getReverse();
			} else {
				dataBuilder = getForward();
			}
		}

		dataBuilder.processRecord(record);
	}

}