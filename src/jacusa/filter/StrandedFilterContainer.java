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
import lib.cli.parameters.JACUSAConditionParameters;
import lib.data.AbstractData;
import lib.data.has.hasBaseCallCount;
import lib.util.Coordinate.STRAND;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class StrandedFilterContainer<T extends AbstractData & hasBaseCallCount> 
implements FilterContainer<T> {

	private final FilterContainer<T> forward;
	private final FilterContainer<T> reverse;

	public StrandedFilterContainer(
			final FilterContainer<T> forward, 
			final FilterContainer<T> reverse) {
		this.forward = forward;
		this.reverse = reverse;
	}
	
	public void clear() {
		forward.clear();
		reverse.clear();
	}

	public int getOverhang() {
		return forward.getOverhang();
	}

	public FilterConfig<T> getFilterConfig() {
		return forward.getFilterConfig();
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
	
	public AbstractCacheStorage<T> getStorage(final char c) {
		return windowStorage.get(c);
	}
	
	public void registerStorage(final AbstractCacheStorage<T> e) {
		windowStorage.put(e.getC(), e);
	}
	
	public void add(AbstractFilter<T> filter) {
		filters.put(filter.getC(), filter);
	}
	
	public AbstractConditionParameter<T> getCondition() {
		return conditionParameter;
	}

}
