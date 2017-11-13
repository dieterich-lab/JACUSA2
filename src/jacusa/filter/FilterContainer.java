package jacusa.filter;

import htsjdk.samtools.CigarOperator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jacusa.filter.storage.AbstractCacheStorage;
import jacusa.filter.storage.ProcessAlignmentBlock;
import jacusa.filter.storage.ProcessAlignmentOperator;
import jacusa.filter.storage.ProcessDeletionOperator;
import jacusa.filter.storage.ProcessInsertionOperator;
import jacusa.filter.storage.ProcessRecord;
import jacusa.filter.storage.ProcessSkippedOperator;
import lib.cli.parameters.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class FilterContainer<T extends AbstractData> {

	private FilterConfig<T> filterConfig;

	private AbstractConditionParameter<T> conditionParameter;
	
	private Map<Character,AbstractFilter<T>> filters;
	private Map<Character,AbstractCacheStorage<?>> windowStorage;

	private int overhang;

	private Map<Character,ProcessRecord> processRecord;
	private Map<Character,ProcessAlignmentOperator> processAlignment;
	private Map<Character,ProcessAlignmentBlock> processAlignmentBlock;
	private Map<Character,ProcessDeletionOperator> processDeletion;
	private Map<Character,ProcessInsertionOperator> processInsertion;
	private Map<Character,ProcessSkippedOperator> processSkipped;
	
	public FilterContainer(final FilterConfig<T> filterConfig, 
			final AbstractConditionParameter<T> conditionParameter) {
		this.filterConfig 		= filterConfig;
		this.conditionParameter	= conditionParameter;
		
		overhang 				= 0;

		final int initialCapacity = 3;
		filters					= new HashMap<Character,AbstractFilter<T>>(initialCapacity);
		windowStorage			= new HashMap<Character,AbstractCacheStorage<?>>(initialCapacity);
		
		processRecord 			= new HashMap<Character,ProcessRecord>(initialCapacity);
		processAlignment		= new HashMap<Character,ProcessAlignmentOperator>(initialCapacity);
		processAlignmentBlock	= new HashMap<Character,ProcessAlignmentBlock>(initialCapacity);
		processDeletion			= new HashMap<Character,ProcessDeletionOperator>(initialCapacity);
		processInsertion		= new HashMap<Character,ProcessInsertionOperator>(initialCapacity);
		processSkipped			= new HashMap<Character,ProcessSkippedOperator>(initialCapacity);
	}
	
	public void clear() {
		for (final AbstractCacheStorage<?> storage : windowStorage.values()) {
			storage.clear();
		}
	}

	public int getOverhang() {
		return overhang;
	}

	public FilterConfig<T> getFilterConfig() {
		return filterConfig;
	}

	
	public Collection<ProcessRecord> getProcessRecord() {
		return processRecord.values();
	}
	
	public void registerProcessRecord(final ProcessRecord e) {
		processRecord.put(e.getC(), e);
	}
	
	public Collection<ProcessAlignmentOperator> getProcessAlignment() {
		return processAlignment.values();
	}
	
	public void registerProcessAlignment(final ProcessAlignmentOperator e) {
		processAlignment.put(e.getC(), e);
	}
	
	public Collection<ProcessAlignmentBlock> getProcessAlignmentBlock() {
		return processAlignmentBlock.values();
	}
	
	public void registerProcessAlignmentBlock(final ProcessAlignmentBlock e) {
		processAlignmentBlock.put(e.getC(), e);
	}
	
	public Collection<ProcessDeletionOperator> getProcessDeletion() {
		return processDeletion.values();
	}
	
	public void registerProcessDeletion(final ProcessDeletionOperator e) {
		processDeletion.put(e.getC(), e);
	}
	
	public Collection<ProcessInsertionOperator> getProcessInsertion() {
		return processInsertion.values();
	}
	
	public void registerProcessInsertion(final ProcessInsertionOperator e) {
		processInsertion.put(e.getC(), e);
	}
	
	public Collection<ProcessSkippedOperator> getProcessSkipped() {
		return processSkipped.values();
	}

	public void registerProcessSkipped(final ProcessSkippedOperator e) {
		processSkipped.put(e.getC(), e);
	}
	
	public AbstractCacheStorage<?> getStorage(final char c) {
		return windowStorage.get(c);
	}

	public void registerStorage(final AbstractCacheStorage<?> e) {
		windowStorage.put(e.getC(), e);
	}
	
	public void add(AbstractFilter<T> filter) {
		filters.put(filter.getC(), filter);
	}
	
	public AbstractConditionParameter<T> getConditionParameter() {
		return conditionParameter;
	}

	public Set<CigarOperator> getCigarOperators() {
		return null; // TODo
	}
	
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		// TODO
	}
	
}
