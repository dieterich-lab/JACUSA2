package jacusa.filter.processrecord;

import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.recordextended.SAMRecordExtended;
import lib.util.position.CigarElementExtendedPositionProviderBuilder;

/**
 * This class will identify all deletions within a read and mark/count +/- distance up- and downstream 
 * aligned/matched positions. Those counts will be used in a base call count filter to identify 
 * false positive variants.
 * 
 * Tested in test.jacusa.filter.processrecord.ProcessDeletionOperator
 */
public class ProcessDeletionOperator extends AbstractFilterRecordExtendedProcessor {

	public ProcessDeletionOperator(
			final SharedStorage sharedStorage,
			final int distance, 
			final PositionProcessor positionProcessor) {
		
		super(sharedStorage, distance, positionProcessor);
	}
	
	@Override
	public void process(SAMRecordExtended recordExtended) {
		// iterate over cigarElement indices of deletions
		for (final int cigarElementExtendedIndex : recordExtended.getDeletion()) {
			processDeletionOperator(cigarElementExtendedIndex, recordExtended);
		}	
	}

	private void processDeletionOperator(final int cigarElementExtendedIndex, final SAMRecordExtended recordExtended) {
		getPositionProcessor().process(
				new CigarElementExtendedPositionProviderBuilder(
						cigarElementExtendedIndex, getDistance(), recordExtended, getTranslator())
				.build());
	}

	
}