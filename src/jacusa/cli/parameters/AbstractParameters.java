package jacusa.cli.parameters;

import java.util.ArrayList;

import java.util.List;

import jacusa.JACUSA;
import jacusa.data.AbstractData;
import jacusa.data.BaseCallConfig;
import jacusa.filter.FilterConfig;
import jacusa.io.Output;
import jacusa.io.OutputPrinter;
import jacusa.io.format.AbstractOutputFormat;
import jacusa.method.AbstractMethodFactory;
import jacusa.pileup.builder.AbstractDataBuilderFactory;

public abstract class AbstractParameters<T extends AbstractData>
implements hasConditions<T> {
	
	// cache related
	private int windowSize;
	private int threadWindowSize;

	private BaseCallConfig baseConfig;
	private char[] bases;
	private boolean showReferenceBase;

	private int maxThreads;

	// bed file to scan for variants
	private String bedPathname;

	// chosen method
	private AbstractMethodFactory<T> methodFactory;

	protected List<ConditionParameters<T>> conditionParameters;

	private Output output;
	private AbstractOutputFormat<T> format;
	private FilterConfig<T> filterConfig;

	private boolean separate;
	
	// debug flag
	private boolean debug;

	private boolean collectLQBCs;
	
	private AbstractParameters() {
		windowSize 			= 10000;
		threadWindowSize	= 10 * windowSize;
		
		baseConfig			= BaseCallConfig.getInstance();
		bases				= BaseCallConfig.BASES.clone();
		showReferenceBase 	= false;

		maxThreads			= 1;
		
		bedPathname			= new String();
		conditionParameters	= new ArrayList<ConditionParameters<T>>(2);

		output				= new OutputPrinter();
		filterConfig		= new FilterConfig<T>();
		
		separate			= false;
		
		debug				= false;
		collectLQBCs		= false;
	}

	public AbstractParameters(final int conditions, final AbstractDataBuilderFactory<T> dataBuilderFactory) {
		this();
		
		for (int i = 0; i < conditions; i++) {
			conditionParameters.add(new ConditionParameters<T>(dataBuilderFactory));
		}
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
	public List<ConditionParameters<T>> getConditionParameters() {
		return conditionParameters;
	}
	
	@Override
	public void setConditionParameters(
			final List<ConditionParameters<T>> conditionParameters) {
		this.conditionParameters = conditionParameters;
	}
	
	@Override
	public ConditionParameters<T> getConditionParameters(int conditionIndex) {
		return conditionParameters.get(conditionIndex);
	}
	
	@Override
	public int getConditions() {
		return conditionParameters.size();
	}
	
	@Override
	public int getReplicates(int conditionIndex) {
		return getConditionParameters(conditionIndex).getPathnames().length;
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
	public int getWindowSize() {
		return windowSize;
	}

	/**
	 * @return the threadWindowSize
	 */
	public int getThreadReservedWindowSize() {
		return threadWindowSize;
	}
	
	/**
	 * @param windowSize the windowSize to set
	 */
	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

	/**
	 * @param threadWindowSize the threadWindowSize to set
	 */
	public void setThreadWindowSize(int threadWindowSize) {
		this.threadWindowSize = threadWindowSize;
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
	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}

	/**
	 * @return the bedPathname
	 */
	public String getBedPathname() {
		return bedPathname;
	}

	/**
	 * @param bedPathname the bedPathname to set
	 */
	public void setBedPathname(String bedPathname) {
		this.bedPathname = bedPathname;
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
		JACUSA.printDebug("DEBUG Modus!");
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

	public void collectLowQualityBaseCalls(boolean collectLQBCs) {
		this.collectLQBCs = collectLQBCs;
	}

	public boolean collectLowQualityBaseCalls() {
		return collectLQBCs;
	}
	
}
