package lib.cli.parameter;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import jacusa.cli.parameters.HasConditionParameter;
import jacusa.filter.FilterConfig;
import lib.io.ResultFormat;
import lib.util.AbstractTool;

public class GeneralParameter
implements HasConditionParameter {

	public static final String FILE_SUFFIX = ".filtered";
	
	// cache related
	private int activeWinSize;
	private int reservedWinSize;

	private int maxThreads;

	private String refFilename;
	private IndexedFastaSequenceFile refFile;
		
	// bed file to scan for variants
	private String inputBedFilename;

	protected List<ConditionParameter> conditionParameters;

	private String resultFilename;
	private ResultFormat resultFormat;
	
	private FilterConfig filterConfig;

	private String filteredFilename;
	
	private final Map<ShowOptions, Boolean> showMap;
	
	private final List<String> additionalKeys;
	
	// debug flag
	private boolean debug;
	
	protected GeneralParameter() {
		activeWinSize 	= 10000;
		reservedWinSize	= 10 * activeWinSize;

		maxThreads			= 1;
		
		inputBedFilename	= "";
		conditionParameters	= new ArrayList<>();

		filterConfig			= new FilterConfig();
		
		filteredFilename	= null;
		
		showMap 			= new HashMap<ShowOptions, Boolean>(8);
		showMap.put(ShowOptions.DELETION_COUNT, false);
		showMap.put(ShowOptions.INSERTION_COUNT, false);
		showMap.put(ShowOptions.INSERTION_START_COUNT, false);
		showMap.put(ShowOptions.NON_REFERENCE_COUNT, false);
		showMap.put(ShowOptions.DELETION_RATIO, false);
		showMap.put(ShowOptions.INSERTION_RATIO, false);
		showMap.put(ShowOptions.NON_REFERENCE_RATIO, false);
		showMap.put(ShowOptions.SHOW_ALL_SITES, false);
		showMap.put(ShowOptions.MODIFICATION_COUNT, false);
		
		additionalKeys 		= new ArrayList<String>();
		
		debug				= false;
	}
	
	public GeneralParameter(final int conditionSize) {
		this();
		
		for (int conditionIndex = 0; conditionIndex < conditionSize; conditionIndex++) {
			conditionParameters.add(new ConditionParameter(conditionIndex));
		}
	}
	
	public ConditionParameter createConditionParameter(final int conditionIndex) {
		return new ConditionParameter(conditionIndex);
	}
	
	public ResultFormat getResultFormat() {
		return resultFormat;
	}

	public void setResultFormat(ResultFormat resultFormat) {
		this.resultFormat = resultFormat;
	}
	
	/**
	 * @return the filterConfig
	 */
	public FilterConfig getFilterConfig() {
		return filterConfig;
	}
	
	public void setResultFilename(final String resultFilename) {
		this.resultFilename = resultFilename;
	}
	
	public String getResultFilename() {
		return resultFilename;
	}

	@Override
	public List<ConditionParameter> getConditionParameters() {
		return conditionParameters;
	}
	
	@Override
	public void setConditionParameters(
			final List<ConditionParameter> conditionParameters) {
		this.conditionParameters = conditionParameters;
	}
	
	@Override
	public ConditionParameter getConditionParameter(int conditionIndex) {
		return conditionParameters.get(conditionIndex);
	}
	
	@Override
	public int getConditionsSize() {
		return conditionParameters.size();
	}
	
	@Override
	public int getReplicates(int conditionIndex) {
		return getConditionParameter(conditionIndex).getRecordFilenames().length;
	}
	
	/**
	 * @return the windowSize
	 */
	public int getActiveWindowSize() {
		return activeWinSize;
	}

	/**
	 * @return the reservedWindowSize
	 */
	public int getReservedWindowSize() {
		return reservedWinSize;
	}
	
	/**
	 * @param activeWindowSize the windowSize to set
	 */
	public void setActiveWindowSize(final int activeWindowSize) {
		this.activeWinSize = activeWindowSize;
	}

	/**
	 * @param reservedWindowSize the threadWindowSize to set
	 */
	public void setReservedWindowSize(final int reservedWindowSize) {
		this.reservedWinSize = reservedWindowSize;
	}
	
	/**
	 * @return the maxThreads
	 */
	public int getMaxThreads() {
		return maxThreads;
	}

	/**
	 * @param maxThreads the maxThreads to set
	 */
	public void setMaxThreads(final int maxThreads) {
		this.maxThreads = maxThreads;
	}

	/**
	 * @return the bedPathname
	 */
	public String getInputBedFilename() {
		return inputBedFilename;
	}

	/**
	 * @param bedPathname the bedPathname to set
	 */
	public void setInputBedFilename(String bedPathname) {
		this.inputBedFilename = bedPathname;
	}

	/**
	 * @return the debug
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * @param debug the debug to set
	 */
	public void setDebug(boolean debug) {
		AbstractTool.getLogger().addDebug("DEBUG Modus!");
		this.debug = debug;
	}

	private boolean show(final ShowOptions showOption) {
		return showMap.getOrDefault(showOption, false);
	}
	
	private void show(final ShowOptions showOption, final boolean flag) {
		showMap.put(showOption, flag);
	}
	
	/**
	 * @return the showDeletionCount
	 */
	public boolean showDeletionCount() {
		return show(ShowOptions.DELETION_COUNT);
	}
	
	public boolean showInsertionCount() {
		return show(ShowOptions.INSERTION_COUNT);
	}
	
	public boolean showInsertionStartCount() {
		return show(ShowOptions.INSERTION_START_COUNT);
	}

	/**
	 * @param showDeletionCount the showDeletionCount to set
	 */
	public void showDeletionCount(boolean showDeletionCount) {
		show(ShowOptions.DELETION_COUNT, showDeletionCount);
		registerConditionReplictaKeys("deletion"); // FIXME use static STRING
	}
	
	/**
	 * @param showInsertionCount the showInsertionCount to set
	 */
	public void showInsertionCount(boolean showInsertionCount) throws Exception {
		if (showInsertionStartCount() && showInsertionCount) {
			throw new Exception("Cannot set both to true");
		}
		show(ShowOptions.INSERTION_COUNT, showInsertionCount);
		registerConditionReplictaKeys("insertion"); // FIXME use static STRING
	}
	
	public void showInsertionStartsCount(boolean showInsertionStartCount) throws Exception {
		if (showInsertionCount() && showInsertionStartCount) {
			throw new Exception("Cannot set both to true");
		}
		show(ShowOptions.INSERTION_START_COUNT, showInsertionStartCount);
		registerConditionReplictaKeys("insertion"); // FIXME use static STRING
	}
	
	/**
	 * @return if sites without a variant should be processed
	 */
	public boolean showAllSites() {
		return show(ShowOptions.SHOW_ALL_SITES);
	}

	public void showAllSites(boolean showAllSites) {
		show(ShowOptions.SHOW_ALL_SITES, showAllSites);
	}

	/**
	 * @return the filename where to write filtered sites
	 */
	public String getFilteredFilename() {
		if (filteredFilename == null) {
			return null;
		}
		
		// fake argument
		if (filteredFilename.length() == 0) {
			return getResultFilename() + FILE_SUFFIX;
		}
		
		return filteredFilename;
	}
	
	public boolean showINDELcounts() {
		return showDeletionCount() ||
				showInsertionCount() ||
				showInsertionStartCount();
	}
	
	public String getReferenceFilename() {
		return refFilename;
	}
	
	public void setReferernceFilename(final String referenceFilename) {
		this.refFilename = referenceFilename;
	}

	public int getBAMfileCount() {
		return IntStream.range(0, getConditionsSize())
		.mapToObj(i -> getReplicates(i))
		.mapToInt(i -> i).sum();
	}
	
	public IndexedFastaSequenceFile getReferenceFile() {
		if (refFile == null && getReferenceFilename() != null && ! getReferenceFilename().isEmpty()) {
			final File file = new File(getReferenceFilename());
			try {
				refFile = new IndexedFastaSequenceFile(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		return refFile;
	}
	
	/**
	 * Set to output filtered sites to filteredFilename.
	 * @param filteredFilename String name of file to write filtered sites to.
	 */
	public void setFilteredFilename(final String filteredFilename) {
		this.filteredFilename = filteredFilename;
	}

	
	public void registerKey(final String key) {
		if (additionalKeys.contains(key)) {
			throw new IllegalArgumentException();
		}
		
		additionalKeys.add(key);
	}

	public void registerConditionKeys(final String key) {
		for (int conditionIndex = 0; conditionIndex < getConditionsSize(); conditionIndex++) {
			additionalKeys.add(key + conditionIndex);
		}
	}
	
	public void registerConditionReplictaKeys(final String key) {
		for (int conditionIndex = 0; conditionIndex < getConditionsSize(); conditionIndex++) {
			for (int replicateIndex = 0; replicateIndex < getReplicates(conditionIndex); replicateIndex++) {
				additionalKeys.add(key + Integer.toString(conditionIndex) + Integer.toString(replicateIndex));
			}
		}
	}
	
	public List<String> getAdditionalKeys() {
		return Collections.unmodifiableList(additionalKeys);
	}

}

enum ShowOptions {
	DELETION_COUNT,
	INSERTION_COUNT,
	INSERTION_START_COUNT,
	NON_REFERENCE_COUNT,
	DELETION_RATIO,
	INSERTION_RATIO,
	NON_REFERENCE_RATIO,
	SHOW_ALL_SITES,
	MODIFICATION_COUNT,	
}
