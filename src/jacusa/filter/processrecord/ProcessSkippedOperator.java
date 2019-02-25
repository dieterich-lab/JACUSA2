package jacusa.filter.processrecord;

import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.recordextended.SAMRecordExtended;
import lib.util.position.CigarElementExtendedPositionProviderBuilder;

/**
 * This class will identify all splice sites within a read and mark/count +/- distance up- and downstream 
 * aligned/matched positions. Those counts will be used in a base call count filter to identify 
 * false positive variants.
 * 
 * Tested in @see test.jacusa.filter.processrecord.ProcessSkippedOperatorTest
 */
public class ProcessSkippedOperator extends AbstractFilterRecordExtendedProcessor {

	public ProcessSkippedOperator(
			final SharedStorage sharedStorage, 
			final int distance, 
			final PositionProcessor positionProcessor) {
		
		super(sharedStorage, distance, positionProcessor);
	}

	@Override
	public void process(SAMRecordExtended recordExtended) {
		// iterate over cigarElement indices of splice sites
		for (final int cigarElementExtendedIndex : recordExtended.getSkipped()) {
			processSkippedOperator(cigarElementExtendedIndex, recordExtended);
		}
	}
	
	/**
	 * Helper method.
	 * 
	 * @param cigarElementExtendedIndex
	 * @param recordExtended
	 */
	private void processSkippedOperator(
			final int cigarElementExtendedIndex, final SAMRecordExtended recordExtended) {
		
		getPositionProcessor().process(
				new CigarElementExtendedPositionProviderBuilder(
						cigarElementExtendedIndex, getDistance(), recordExtended, getTranslator())
				.build());
	}

}