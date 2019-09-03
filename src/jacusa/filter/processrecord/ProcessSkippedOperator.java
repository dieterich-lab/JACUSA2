package jacusa.filter.processrecord;

import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.record.Record;
import lib.util.position.CigarElementPositionProviderBuilder;

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
	public void process(Record record) {
		// iterate over cigarElement indices of splice sites
		for (final int cigarElementExtendedIndex : record.getSkipped()) {
			processSkippedOperator(cigarElementExtendedIndex, record);
		}
	}
	
	/**
	 * Helper method.
	 * 
	 * @param cigarElementExtendedIndex
	 * @param record
	 */
	private void processSkippedOperator(
			final int cigarElementExtendedIndex, final Record record) {
		
		getPositionProcessor().process(
				new CigarElementPositionProviderBuilder(
						cigarElementExtendedIndex, getDistance(), record, getTranslator())
				.build());
	}

}
