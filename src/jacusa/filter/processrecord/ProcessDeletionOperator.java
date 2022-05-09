package jacusa.filter.processrecord;

import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.record.ProcessedRecord;
import lib.util.position.CigarDetailPosProviderBuilder;

/**
 * This class will identify all deletions within a read and mark/count +/- distance up- and downstream 
 * aligned/matched positions. Those counts will be used in a base call count filter to identify 
 * false positive variants.
 * 
 * Tested in test.jacusa.filter.processrecord.ProcessDeletionOperator
 */
public class ProcessDeletionOperator extends AbstractFilterRecordProcessor {

	public ProcessDeletionOperator(
			final SharedStorage sharedStorage,
			final int distance, 
			final PositionProcessor positionProcessor) {
		
		super(sharedStorage, distance, positionProcessor);
	}
	
	@Override
	public void process(ProcessedRecord record) {
		// iterate over cigarElement indices of deletions
		for (final int cigarDetailI : record.getDeletion()) {
			processDeletionOperator(cigarDetailI, record);
		}	
	}
	
	private void processDeletionOperator(final int cigarDetailI, final ProcessedRecord record) {
		getPosProcessor().process(
				new CigarDetailPosProviderBuilder(
						cigarDetailI, getDistance(), record, getTranslator())
				.build());
	}

	
}