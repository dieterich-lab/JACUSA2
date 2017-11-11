package jacusa.filter;

import java.util.Collection;

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
public interface FilterContainer<T extends AbstractData & hasBaseCallCount> {

	void add(final AbstractFilter<T> filter);
	AbstractConditionParameter<T> getConditionParameter();

	int getOverhang();
	FilterConfig<T> getFilterConfig();
	
	Collection<ProcessRecord> getProcessRecord();
	void registerProcessRecord(final ProcessRecord e);
	
	Collection<ProcessAlignmentOperator> getProcessAlignment();
	void registerProcessAlignment(final ProcessAlignmentOperator e);
	
	Collection<ProcessAlignmentBlock> getProcessAlignmentBlock();
	void registerProcessAlignmentBlock(final ProcessAlignmentBlock e);
	
	Collection<ProcessDeletionOperator> getProcessDeletion();
	void registerProcessDeletion(final ProcessDeletionOperator e);
	
	Collection<ProcessInsertionOperator> getProcessInsertion();
	void registerProcessInsertion(final ProcessInsertionOperator e);
	
	Collection<ProcessSkippedOperator> getProcessSkipped();
	void registerProcessSkipped(final ProcessSkippedOperator e);
	
	AbstractCacheStorage<T> getStorage(final char c);
	void registerStorage(final AbstractCacheStorage<T> e);
	void clear();

}
