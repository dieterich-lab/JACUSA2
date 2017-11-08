package jacusa.filter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jacusa.filter.storage.AbstractWindowStorage;
import jacusa.filter.storage.ProcessAlignmentBlock;
import jacusa.filter.storage.ProcessAlignmentOperator;
import jacusa.filter.storage.ProcessDeletionOperator;
import jacusa.filter.storage.ProcessInsertionOperator;
import jacusa.filter.storage.ProcessRecord;
import jacusa.filter.storage.ProcessSkippedOperator;
import lib.cli.parameters.JACUSAConditionParameters;
import lib.data.AbstractData;
import lib.util.WindowCoordinate;
import lib.util.Coordinate.STRAND;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class FilterContainer<T extends AbstractData> {

	private FilterConfig<T> filterConfig;
	private STRAND strand;
	private WindowCoordinate windowCoordinates;
	private JACUSAConditionParameters<T> condition;
	
	private Map<Character,AbstractFilter<T>> filters;
	private Map<Character,AbstractWindowStorage<T>> windowStorage;

	private int overhang;

	private Map<Character,ProcessRecord> processRecord;
	private Map<Character,ProcessAlignmentOperator> processAlignment;
	private Map<Character,ProcessAlignmentBlock> processAlignmentBlock;
	private Map<Character,ProcessDeletionOperator> processDeletion;
	private Map<Character,ProcessInsertionOperator> processInsertion;
	private Map<Character,ProcessSkippedOperator> processSkipped;
	
	public FilterContainer(
			final FilterConfig<T> filterConfig,
			final STRAND strand, final WindowCoordinate windowCoordinates,
			final JACUSAConditionParameters<T> condition) {
		this.filterConfig 		= filterConfig;
		this.strand				= strand;
		this.windowCoordinates 	= windowCoordinates;
		this.condition			= condition;
		
		overhang 				= 0;

		final int initialCapacity = 3;
		filters					= new HashMap<Character,AbstractFilter<T>>(initialCapacity);
		windowStorage			= new HashMap<Character,AbstractWindowStorage<T>>(initialCapacity);
		
		processRecord 			= new HashMap<Character,ProcessRecord>(initialCapacity);
		processAlignment		= new HashMap<Character,ProcessAlignmentOperator>(initialCapacity);
		processAlignmentBlock	= new HashMap<Character,ProcessAlignmentBlock>(initialCapacity);
		processDeletion			= new HashMap<Character,ProcessDeletionOperator>(initialCapacity);
		processInsertion		= new HashMap<Character,ProcessInsertionOperator>(initialCapacity);
		processSkipped			= new HashMap<Character,ProcessSkippedOperator>(initialCapacity);
	}
	
	public void clear() {
		for (final AbstractWindowStorage<T> storage : windowStorage.values()) {
			storage.clear();
		}
	}

	public int getOverhang() {
		return overhang;
	}

	public FilterConfig<T> getFilterConfig() {
		return filterConfig;
	}

	public WindowCoordinate getWindowCoordinates() {
		return windowCoordinates;
	}

	public STRAND getStrand() {
		return strand;
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
	
	public AbstractWindowStorage<T> getWindowStorage(final char c) {
		return windowStorage.get(c);
	}
	
	public void registerWindowStorage(final AbstractWindowStorage<T> e) {
		windowStorage.put(e.getC(), e);
	}
	
	public void add(AbstractFilter<T> filter) {
		filters.put(filter.getC(), filter);
	}
	
	public JACUSAConditionParameters<T> getCondition() {
		return condition;
	}

}
