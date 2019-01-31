package jacusa.filter.processrecord;

import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.recordextended.SAMRecordExtended;
import lib.util.position.CigarElementExtendedPositionProviderBuilder;

/**
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