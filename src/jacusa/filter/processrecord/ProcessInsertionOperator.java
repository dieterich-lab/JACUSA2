package jacusa.filter.processrecord;

import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.recordextended.SAMRecordExtended;
import lib.util.position.CigarElementExtendedPositionProviderBuilder;

/**
 * Tested in test.jacusa.filter.processrecord.ProcessInsertionOperatorTest
 */
public class ProcessInsertionOperator extends AbstractFilterRecordExtendedProcessor {

	public ProcessInsertionOperator(
			final SharedStorage sharedStorage,
			final int distance, 
			final PositionProcessor positionProcessor) {
		
		super(sharedStorage, distance, positionProcessor);
	}
	
	@Override
	public void process(SAMRecordExtended recordExtended) {
		for (final int cigarElementExtendedIndex : recordExtended.getInsertion()) {
			processInsertionOperator(cigarElementExtendedIndex, recordExtended);
		}
	}
	
	private void processInsertionOperator(
			final int cigarElementExtendedIndex, final SAMRecordExtended recordExtended) {

		getPositionProcessor().process(
				new CigarElementExtendedPositionProviderBuilder(
						cigarElementExtendedIndex, getDistance(), recordExtended, getTranslator())
				.build());
	}

}