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

import jacusa.cli.parameters.HasConditionParameter;
import jacusa.filter.FilterConfig;
import lib.cli.options.has.HasReadSubstitution;
import lib.io.ResultFormat;
import lib.util.AbstractTool;

public class GeneralParameter
implements HasConditionParameter, HasReadSubstitution {
	
	// cache related
	private int activeWindowSize;
	private int reservedWindowSize;

	private int maxThreads;
	
	private boolean showReferenceBase;

	private String referenceFilename;
	private IndexedFastaSequenceFile referenceFile;
		
	// bed file to scan for variants
	private String inputBedFilename;

	protected List<ConditionParameter> conditionParameters;

	private String resultFilename;
	private ResultFormat resultFormat;
	
	private FilterConfig filterConfig;

	private boolean splitFiltered;
	
	private final SortedSet<BaseSubstitution> baseSubstitutions;
	
	// debug flag
	private boolean debug;
	
	protected GeneralParameter() {
		activeWindowSize 	= 10000;
		reservedWindowSize	= 10 * activeWindowSize;
		
		showReferenceBase 	= false;

		maxThreads			= 1;
		
		inputBedFilename	= new String();
		conditionParameters	= new ArrayList<ConditionParameter>(2);

		filterConfig		= new FilterConfig();
		
		splitFiltered		= false;
		
		baseSubstitutions	= new TreeSet<>();
		
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
		return activeWindowSize;
	}

	/**
	 * @return the reservedWindowSize
	 */
	public int getReservedWindowSize() {
		return reservedWindowSize;
	}
	

	@Override
	public SortedSet<BaseSubstitution> getReadSubstitutions() {
		return Collections.unmodifiableSortedSet(baseSubstitutions);
	}
	
	@Override
	public void addReadSubstitution(BaseSubstitution baseSubstitution) {
		baseSubstitutions.add(baseSubstitution);
	}
	
	/**
	 * @param activeWindowSize the windowSize to set
	 */
	public void setActiveWindowSize(final int activeWindowSize) {
		this.activeWindowSize = activeWindowSize;
	}

	/**
	 * @param reservedWindowSize the threadWindowSize to set
	 */
	public void setReservedWindowSize(final int reservedWindowSize) {
		this.reservedWindowSize = reservedWindowSize;
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
	 * @return the debug
	 */
	public boolean splitFiltered() {
		return splitFiltered;
	}

	public boolean showReferenceBase() {
		return showReferenceBase;
	}
	
	public void setShowReferenceBase(boolean showReferenceBase) {
		this.showReferenceBase = showReferenceBase;
	}
	
	public String getReferenceFilename() {
		return referenceFilename;
	}
	
	public void setReferernceFilename(final String referenceFilename) {
		this.referenceFilename = referenceFilename;
	}
	
	public IndexedFastaSequenceFile getReferenceFile() {
		if (referenceFile == null && getReferenceFilename() != null && ! getReferenceFilename().isEmpty()) {
			final File file = new File(getReferenceFilename());
			try {
				referenceFile = new IndexedFastaSequenceFile(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		return referenceFile;
	}
	
	public void resetReferenceFile() {
		if (referenceFile != null) {
			try {
				referenceFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			referenceFile = null;
		}
	}
	
	/**
	 * TODO add comments
	 */
	public void splitFiltered(boolean split) {
		this.splitFiltered = split;
	}
	
}