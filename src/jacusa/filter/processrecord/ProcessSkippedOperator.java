package jacusa.filter.processrecord;

import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.record.ProcessedRecord;
import lib.util.position.CigarDetailPosProviderBuilder;

/**
 * This class will identify all splice sites within a read and mark/count +/- distance up- and downstream 
 * aligned/matched positions. Those counts will be used in a base call count filter to identify 
 * false positive variants.
 * 
 * Tested in @see test.jacusa.filter.processrecord.ProcessSkippedOperatorTest
 */
public class ProcessSkippedOperator extends AbstractFilterRecordProcessor {

	public ProcessSkippedOperator(
			final SharedStorage sharedStorage, 
			final int distance, 
			final PositionProcessor positionProcessor) {
		
		super(sharedStorage, distance, positionProcessor);
	}

	@Override
	public void process(ProcessedRecord record) {
		// iterate over cigarElement indices of splice sites
		for (final int cigarDetailI : record.getSkipped()) {
			processSkippedOperator(cigarDetailI, record);
		}
	}
	
	/**
	 * Helper method.
	 * 
	 * @param cigarDetailI
	 * @param record
	 */
	private void processSkippedOperator(
			final int cigarDetailI, final ProcessedRecord record) {
		
		getPosProcessor().process(
				new CigarDetailPosProviderBuilder(
						cigarDetailI, getDistance(), record, getTranslator())
				.build());
	}

}
