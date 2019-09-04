package jacusa.filter.processrecord;

import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.record.Record;
import lib.util.position.CigarDetailPosProviderBuilder;
import lib.util.position.PositionProvider;

/**
 * This class will identify all insertions within a read and mark/count +/- distance up- and downstream 
 * aligned/matched positions. Those counts will be used in a base call count filter to identify 
 * false positive variants.
 * 
 * Tested in test.jacusa.filter.processrecord.ProcessInsertionOperatorTest
 */
public class ProcessInsertionOperator extends AbstractFilterRecordProcessor {

	public ProcessInsertionOperator(
			final SharedStorage sharedStorage,
			final int distance, 
			final PositionProcessor positionProcessor) {
		
		super(sharedStorage, distance, positionProcessor);
	}
	
	@Override
	public void process(Record record) {
		// iterate over cigarElement indices of deletions
		for (final int cigarDetailI : record.getInsertion()) {
			processInsertionOperator(cigarDetailI, record);
		}
	}
	
	private void processInsertionOperator(
			final int cigarDetailI, final Record record) {

		final PositionProvider positionProvider = new CigarDetailPosProviderBuilder(
				cigarDetailI, getDistance(), record, getTranslator())
				.build();
		getPosProcessor().process(positionProvider);
	}

}