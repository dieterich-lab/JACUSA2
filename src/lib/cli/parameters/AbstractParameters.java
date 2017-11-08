package lib.cli.parameters;

import java.util.ArrayList;

import java.util.List;

import jacusa.cli.parameters.hasDefaultConditions;
import jacusa.filter.FilterConfig;
import jacusa.io.Output;
import jacusa.io.OutputPrinter;
import jacusa.io.format.AbstractOutputFormat;
import jacusa.pileup.builder.AbstractDataBuilderFactory;
import lib.data.AbstractData;
import lib.data.BaseCallConfig;
import lib.method.AbstractMethodFactory;
import lib.util.AbstractTool;

public abstract class AbstractParameters<T extends AbstractData>
implements hasDefaultConditions<T> {
	
	// cache related
	private int activeWindowSize;
	private int reservedWindowSize;

	private int maxThreads;
	
	private BaseCallConfig baseConfig;
		private char[] bases;
		private boolean showReferenceBase;

	

	// bed file to scan for variants
	private String inputBedFilename;

	// chosen method
	private AbstractMethodFactory<T> methodFactory;

	protected List<AbstractConditionParameter<T>> conditionParameters;

		private Output output;
		private AbstractOutputFormat<T> format;
		private FilterConfig<T> filterConfig;

		private boolean separate;
	
	// debug flag
	private boolean debug;
	
	protected AbstractParameters() {
		activeWindowSize 			= 10000;
		reservedWindowSize	= 10 * activeWindowSize;
		
		baseConfig			= BaseCallConfig.getInstance();
		bases				= BaseCallConfig.BASES.clone();
		showReferenceBase 	= false;

		maxThreads			= 1;
		
		inputBedFilename	= new String();
		conditionParameters	= new ArrayList<AbstractConditionParameter<T>>(2);

		output				= new OutputPrinter();
		filterConfig		= new FilterConfig<T>();
		
		separate			= false;
		
		debug				= false;
	}

	public AbstractParameters(final int conditions, final AbstractDataBuilderFactory<T> dataBuilderFactory) {
		this();
		
		/* TODO
		for (int i = 0; i < conditions; i++) {
			conditionParameters.add(new JConditionParameters<T>(dataBuilderFactory));
		}
		*/
	}
	
	public AbstractOutputFormat<T> getFormat() {
		return format;
	}

	public void setFormat(AbstractOutputFormat<T> format) {
		this.format = format;
	}
	
	/**
	 * @return the filterConfig
	 */
	public FilterConfig<T> getFilterConfig() {
		return filterConfig;
	}
	
	/**
	 * @return the output
	 */
	public Output getOutput() {
		return output;
	}

	/**
	 * @param output the output to set
	 */
	public void setOutput(Output output) {
		this.output = output;
	}

	@Override
	public List<AbstractConditionParameter<T>> getConditionParameters() {
		return conditionParameters;
	}
	
	@Override
	public void setConditionParameters(
			final List<AbstractConditionParameter<T>> conditionParameters) {
		this.conditionParameters = conditionParameters;
	}
	
	@Override
	public AbstractConditionParameter<T> getConditionParameters(int conditionIndex) {
		return conditionParameters.get(conditionIndex);
	}
	
	@Override
	public int getConditionsSize() {
		return conditionParameters.size();
	}
	
	@Override
	public int getReplicates(int conditionIndex) {
		return getConditionParameters(conditionIndex).getRecordFilenames().length;
	}
	
	/**
	 * @return the baseConfig
	 */
	public char[] getBases() {
		return bases;
	}
	
	/**
	 * @return the baseConfig
	 */
	public void setBases(final char[] bases) {
		this.bases = bases.clone();
	}
	
	/**
	 * @return the windowSize
	 */
	public int getActiveWindowSize() {
		return activeWindowSize;
	}

	/**
	 * @return the threadWindowSize
	 */
	public int getReservedWindowSize() {
		return reservedWindowSize;
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
	 * @return the methodFactory
	 */
	public AbstractMethodFactory<T> getMethodFactory() {
		return methodFactory;
	}

	/**
	 * @param methodFactory the methodFactory to set
	 */
	public void setMethodFactory(AbstractMethodFactory<T> methodFactory) {
		this.methodFactory = methodFactory;
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
	public boolean isSeparate() {
		return separate;
	}

	public boolean showReferenceBase() {
		return showReferenceBase;
	}
	
	public void setShowReferenceBase(boolean showReferenceBase) {
		this.showReferenceBase = showReferenceBase;
	}

	public BaseCallConfig getBaseConfig() {
		return baseConfig;
	}
	
	/**
	 * @param debug the debug to set
	 */
	public void setSeparate(boolean separate) {
		this.separate = separate;
	}
	
}
