package lib.cli.parameter;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.IntStream;

import jacusa.cli.parameters.HasConditionParameter;
import jacusa.filter.FilterConfig;
import lib.cli.options.filter.has.BaseSub;
import lib.cli.options.filter.has.HasReadTag;
import lib.io.ResultFormat;
import lib.util.AbstractTool;

public class GeneralParameter
implements HasConditionParameter, HasReadTag {

	public static final String FILE_SUFFIX = ".filtered";
	
	// cache related
	private int activeWinSize;
	private int reservedWinSize;

	private int maxThreads;

	private byte enforcedBASQ;
	
	private String refFilename;
	private IndexedFastaSequenceFile refFile;
		
	// bed file to scan for variants
	private String inputBedFilename;

	protected List<ConditionParameter> condPrms;

	private String resFilename;
	private ResultFormat resFormat;
	
	private FilterConfig filterConf;

	private String filteredFilename;
	
	private final SortedSet<BaseSub> baseSubs;
	private boolean showDeletionCount;
	private boolean showInsertionCount;
	private boolean showInsertionStartCount;
	private boolean showAllSites;
	
	// debug flag
	private boolean debug;
	
	protected GeneralParameter() {
		activeWinSize 	= 10000;
		reservedWinSize	= 10 * activeWinSize;

		maxThreads			= 1;
		enforcedBASQ		= -1;
		
		inputBedFilename	= "";
		condPrms	= new ArrayList<>(2);

		filterConf		= new FilterConfig();
		
		filteredFilename	= null;
		
		baseSubs	= new TreeSet<>();
		showDeletionCount	= false;
		showAllSites = false;
		
		debug				= false;
	}
	
	public GeneralParameter(final int conditionSize) {
		this();
		
		for (int condI = 1; condI <= conditionSize; condI++) {
			condPrms.add(new ConditionParameter(condI));
		}
	}
	
	public void enforceBASQ(final byte basq) {
		enforcedBASQ = basq;
	}
	
	public byte getEnforcedBASQ() {
		return enforcedBASQ;
	}
	
	public ConditionParameter createConditionParameter(final int condI) {
		return new ConditionParameter(condI);
	}
	
	public ResultFormat getResultFormat() {
		return resFormat;
	}

	public void setResultFormat(ResultFormat resultFormat) {
		this.resFormat = resultFormat;
	}
	
	/**
	 * @return the filterConfig
	 */
	public FilterConfig getFilterConfig() {
		return filterConf;
	}
	
	public void setResultFilename(final String resultFilename) {
		this.resFilename = resultFilename;
	}
	
	public String getResultFilename() {
		return resFilename;
	}

	@Override
	public List<ConditionParameter> getConditionParameters() {
		return condPrms;
	}
	
	@Override
	public void setConditionParameters(
			final List<ConditionParameter> conditionParameters) {
		this.condPrms = conditionParameters;
	}
	
	@Override
	public ConditionParameter getConditionParameter(int condI) {
		return condPrms.get(condI);
	}
	
	@Override
	public int getConditionsSize() {
		return condPrms.size();
	}
	
	@Override
	public int getReplicates(int condI) {
		return getConditionParameter(condI).getRecordFilenames().length;
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
	

	@Override
	public SortedSet<BaseSub> getReadTags() {
		return Collections.unmodifiableSortedSet(baseSubs);
	}
	
	@Override
	public void addReadTag(BaseSub baseSub) {
		baseSubs.add(baseSub);
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

	
	/**
	 * @return the showDeletionCount
	 */
	public boolean showDeletionCount() {
		return showDeletionCount;
	}
	
	public boolean showInsertionCount() {
		return showInsertionCount;
	}
	
	public boolean showInsertionStartCount() {
		return showInsertionStartCount;
	}

	/**
	 * @param showDeletionCount the showDeletionCount to set
	 */
	public void showDeletionCount(boolean showDeletionCount) {
		this.showDeletionCount = showDeletionCount;
	}
	
	/**
	 * @param showInsertionCount the showInsertionCount to set
	 */
	public void showInsertionCount(boolean showInsertionCount) throws Exception {
		if (showInsertionStartCount && showInsertionCount) {
			throw new Exception("Cannot set both to true");
		}
		this.showInsertionCount = showInsertionCount;
	}
	
	public void showInsertionStartsCount(boolean showInsertionStartCount) throws Exception {
		if (this.showInsertionCount && showInsertionStartCount) {
			throw new Exception("Cannot set both to true");
		}
		this.showInsertionStartCount = showInsertionStartCount;
	}
	
	/**
	 * @return the showDeletionCount
	 */
	public boolean showAllSites() {
		return showAllSites;
	}

	/**
	 * @param showInsertionCount the showInsertionCount to set
	 */
	public void showAllSites(boolean showAllSites) {
		this.showAllSites = showAllSites;
	}

	/**
	 * @return the filteredFilename
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
	
	public void resetReferenceFile() {
		if (refFile != null) {
			try {
				refFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			refFile = null;
		}
	}
	
	/**
	 * Set to output filtered sites to filteredFilename.
	 * @param filteredFilename String name of file to write filtered sites to.
	 */
	public void setFilteredFilename(final String filteredFilename) {
		this.filteredFilename = filteredFilename;
	}
	
}
