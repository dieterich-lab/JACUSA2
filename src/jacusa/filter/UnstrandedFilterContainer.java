package jacusa.filter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jacusa.filter.storage.AbstractCacheStorage;
import jacusa.filter.storage.ProcessAlignmentBlock;
import jacusa.filter.storage.ProcessAlignmentOperator;
import jacusa.filter.storage.ProcessDeletionOperator;
import jacusa.filter.storage.ProcessInsertionOperator;
import jacusa.filter.storage.ProcessRecord;
import jacusa.filter.storage.ProcessSkippedOperator;
import lib.cli.parameters.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.has.hasBaseCallCount;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class UnstrandedFilterContainer<T extends AbstractData & hasBaseCallCount> 
implements FilterContainer<T> {

	private FilterConfig<T> filterConfig;
	private AbstractConditionParameter<T> conditionParameter;
	
	private Map<Character,AbstractFilter<T>> filters;
	private Map<Character,AbstractCacheStorage<T>> storages;

	private int overhang;

	private Map<Character,ProcessRecord> processRecord;
	private Map<Character,ProcessAlignmentOperator> processAlignment;
	private Map<Character,ProcessAlignmentBlock> processAlignmentBlock;
	private Map<Character,ProcessDeletionOperator> processDeletion;
	private Map<Character,ProcessInsertionOperator> processInsertion;
	private Map<Character,ProcessSkippedOperator> processSkipped;
	
	// TODO LIBRARY TYPE
	
	public UnstrandedFilterContainer(
			final FilterConfig<T> filterConfig,
			final AbstractConditionParameter<T> conditionParameter) {
		this.filterConfig 		= filterConfig;
		this.conditionParameter = conditionParameter;
		
		overhang 				= 0;

		final int initialCapacity = 3;
		filters					= new HashMap<Character,AbstractFilter<T>>(initialCapacity);
		storages				= new HashMap<Character,AbstractCacheStorage<T>>(initialCapacity);
		
		processRecord 			= new HashMap<Character,ProcessRecord>(initialCapacity);
		processAlignment		= new HashMap<Character,ProcessAlignmentOperator>(initialCapacity);
		processAlignmentBlock	= new HashMap<Character,ProcessAlignmentBlock>(initialCapacity);
		processDeletion			= new HashMap<Character,ProcessDeletionOperator>(initialCapacity);
		processInsertion		= new HashMap<Character,ProcessInsertionOperator>(initialCapacity);
		processSkipped			= new HashMap<Character,ProcessSkippedOperator>(initialCapacity);
	}
	
	@Override
	public void clear() {
		for (final AbstractCacheStorage<T> storage : storages.values()) {
			storage.clear();
		}
	}

	@Override
	public int getOverhang() {
		return overhang;
	}

	@Override
	public FilterConfig<T> getFilterConfig() {
		return filterConfig;
	}
	
	@Override
	public Collection<ProcessRecord> getProcessRecord() {
		return processRecord.values();
	}
	
	@Override
	public void registerProcessRecord(final ProcessRecord e) {
		processRecord.put(e.getC(), e);
	}
	
	@Override
	public Collection<ProcessAlignmentOperator> getProcessAlignment() {
		return processAlignment.values();
	}
	
	@Override
	public void registerProcessAlignment(final ProcessAlignmentOperator e) {
		processAlignment.put(e.getC(), e);
	}
	
	@Override
	public Collection<ProcessAlignmentBlock> getProcessAlignmentBlock() {
		return processAlignmentBlock.values();
	}
	
	@Override
	public void registerProcessAlignmentBlock(final ProcessAlignmentBlock e) {
		processAlignmentBlock.put(e.getC(), e);
	}
	
	@Override
	public Collection<ProcessDeletionOperator> getProcessDeletion() {
		return processDeletion.values();
	}
	
	@Override
	public void registerProcessDeletion(final ProcessDeletionOperator e) {
		processDeletion.put(e.getC(), e);
	}
	
	@Override
	public Collection<ProcessInsertionOperator> getProcessInsertion() {
		return processInsertion.values();
	}
	
	@Override
	public void registerProcessInsertion(final ProcessInsertionOperator e) {
		processInsertion.put(e.getC(), e);
	}
	
	@Override
	public Collection<ProcessSkippedOperator> getProcessSkipped() {
		return processSkipped.values();
	}

	@Override
	public void registerProcessSkipped(final ProcessSkippedOperator e) {
		processSkipped.put(e.getC(), e);
	}
	
	@Override
	public AbstractCacheStorage<T> getStorage(final char c) {
		return storages.get(c);
	}
	
	@Override
	public void registerStorage(final AbstractCacheStorage<T> e) {
		storages.put(e.getC(), e);
	}
	
	@Override
	public void add(AbstractFilter<T> filter) {
		filters.put(filter.getC(), filter);
	}
	
	@Override
	public AbstractConditionParameter<T> getConditionParameter() {
		return conditionParameter;
	}

}