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
public class FRPairedEnd1PileupBuilder<T extends BaseQualData>
extends AbstractStrandedPileupBuilder<T> {

	public FRPairedEnd1PileupBuilder(final WindowCoordinate windowCoordinates, 
			final SamReader reader, 
			final JACUSAConditionParameters<T> condition,
			final AbstractParameters<T> parameters) {
		super(windowCoordinates, reader, condition, parameters, LIBRARY_TYPE.FR_FIRSTSTRAND);
	}
	
	public void processRecord(SAMRecord record) {
		/*
		 * 
		 * Taken from: https://www.biostars.org/p/64250/
	     * fr-firststrand:dUTP, NSR, NNSR Same as above except we enforce the rule that the right-most end of the fragment (in transcript coordinates) is the first sequenced (or only sequenced for single-end reads). Equivalently, it is assumed that only the strand generated during first strand synthesis is sequenced.
	     *  
		 */
		AbstractDataBuilder<T> dataBuilder = null;
		
		if (record.getReadPairedFlag()) { // paired end
			if (record.getFirstOfPairFlag() && record.getReadNegativeStrandFlag() || 
					record.getSecondOfPairFlag() && ! record.getReadNegativeStrandFlag()) {
				dataBuilder = getForward();
			} else {
				dataBuilder = getReverse();
			}
		} else { // single end
			if (record.getReadNegativeStrandFlag()) {
				dataBuilder = getForward();
			} else {
				dataBuilder = getReverse();
			}
		}

		dataBuilder.processRecord(record);
	}

}
