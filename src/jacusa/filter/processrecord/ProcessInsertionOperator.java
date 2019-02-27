package jacusa.filter.processrecord;

import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.recordextended.SAMRecordExtended;
import lib.util.position.CigarElementExtendedPositionProviderBuilder;
import lib.util.position.PositionProvider;

/**
 * This class will identify all insertions within a read and mark/count +/- distance up- and downstream 
 * aligned/matched positions. Those counts will be used in a base call count filter to identify 
 * false positive variants.
 * 
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
		// iterate over cigarElement indices of deletions
		for (final int cigarElementExtendedIndex : recordExtended.getInsertion()) {
			processInsertionOperator(cigarElementExtendedIndex, recordExtended);
		}
	}
	
	private void processInsertionOperator(
			final int cigarElementExtendedIndex, final SAMRecordExtended recordExtended) {

		final PositionProvider positionProvider = new CigarElementExtendedPositionProviderBuilder(
				cigarElementExtendedIndex, getDistance(), recordExtended, getTranslator())
				.build();
		getPositionProcessor().process(positionProvider);
	}

}