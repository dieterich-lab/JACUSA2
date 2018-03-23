package jacusa.io.writer;

import java.io.IOException;
import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.hasReferenceBase;
import lib.data.result.Result;
import lib.io.AbstractResultFileWriter;

/**
 * This abstract class implements a BED-like format writer.
 * The first 6 columns correspond to BED6. 
 * Additional columns can be added by overriding existing methods.
 *
 * @param <T>
 * @param <R>
 */
public abstract class BEDlikeWriter<T extends AbstractData & hasReferenceBase, R extends Result<T>> 
extends AbstractResultFileWriter<T, R> {

	// TODO add comments.
	public static final char COMMENT= '#';
	// TODO add comments.
	public static final char EMPTY 	= '*';
	// TODO add comments.
	public static final char SEP 	= '\t';
	// TODO add comments.
	public static final char SEP2 	= ',';

	// general parameters
	private AbstractParameter<T, R> parameter;
	
	public BEDlikeWriter(final String filename, final AbstractParameter<T, R> parameter) {
		super(filename);
		this.parameter = parameter;
	}

	@Override
	public void writeHeader(final List<AbstractConditionParameter<T>> conditionParameters) {
		final StringBuilder sb = new StringBuilder();

		// adds pre-header details about conditions
		addHeaderConditionDetails(sb, conditionParameters);

		// adds default BED6 columns
		addHeaderBED6(sb);
		// adds columns to describe condition data
		addHeaderConditions(sb, conditionParameters);
		// adds any relevant additional info, e.g.: filtering or fitting 
		addHeaderInfo(sb);
		sb.append('\n');

		try {
			getBW().write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * TODO add comments.
	 * 
	 * @param sb 
	 */
	protected void addHeaderBED6(final StringBuilder sb) {
		sb.append(COMMENT);

		// position (0-based)
		sb.append("contig");
		sb.append(SEP);
		sb.append("start");
		sb.append(SEP);
		sb.append("end");
		sb.append(SEP);

		sb.append("name");
		sb.append(SEP);

		// name of statistic column can be customized
		sb.append(getHeaderStat());
		sb.append(SEP);

		sb.append("strand");
	}

	/**
	 * Gets name for the 4-th column of a BED6 file. 
	 * 
	 * @return string name of 4-th column
	 */
	protected String getHeaderStat() {
		return "stat";
	}
	
	/**
	 * Adds header condition data for all conditions and respective replicates to the header.
	 * The header is represented by the StringBuilder object.
	 * 
	 * @param sb StringBuilder object that holds the current state of the header
	 * @param conditionParameters List of all condition parameters to be adder to the header
	 */
	protected void addHeaderConditions(final StringBuilder sb, final List<AbstractConditionParameter<T>> conditionParameters) {
		for (int conditionIndex = 0; conditionIndex < conditionParameters.size(); conditionIndex++) {
			// number of replicates
			final int replicates = conditionParameters.get(conditionIndex).getRecordFilenames().length;
			for (int replicateIndex = 0; replicateIndex < replicates; ++replicateIndex) {
				addHeaderConditionData(sb, conditionIndex, replicateIndex);
			}
		}
		sb.append(SEP);
	}

	/**
	 * Add condition specific data to the header for a particular condition and its replicate.
	 * 
	 * @param sb StringBuilder object that holds the current state of the header
	 * @param conditionIndex identifies a specific condition
	 * @param replicateIndex identifies a specific replicate for this condition
	 */
	protected void addHeaderConditionData(final StringBuilder sb, 
			final int conditionIndex, final int replicateIndex) {

		addHeaderBases(sb, conditionIndex, replicateIndex);
	}

	/**
	 * TODO add comments. Should this be in addHeaderConditionData
	 * 
	 * @param sb StringBuilder object that holds the current state of the header
	 * @param conditionIndex identifies a specific condition
	 * @param replicateIndex identifies a specific replicate for this condition
	 */
	protected void addHeaderBases(final StringBuilder sb, 
			final int conditionIndex, final int replicateIndex) {
		// override
	}
	
	/**
	 * TODO add comments.
	 * 
	 * @param sb StringBuilder object that holds the current state of the header
	 * @param conditionParameters
	 */
	protected void addHeaderConditionDetails(final StringBuilder sb, 
			final List<AbstractConditionParameter<T>> conditionParameters) {

		int conditionIndex = 1;
		for (final AbstractConditionParameter<T> conditionParameter : conditionParameters) {
			// pre-header is identified by 2x COMMENT char
			sb.append(COMMENT);
			sb.append(COMMENT);
			
			// unique id of condition
			sb.append(" condition");
			sb.append(conditionIndex);
			
			// library type of condition
			sb.append(" library_type=");
			sb.append(conditionParameter.getLibraryType());
			
			// provided files for condition
			sb.append(" files=");
			final String[] pathnames = conditionParameter.getRecordFilenames();
			sb.append(pathnames[0]);
			for (int replicateIndex = 1; replicateIndex < pathnames.length; replicateIndex++) {
				sb.append(SEP2);
				sb.append(pathnames[replicateIndex]);
			}
			sb.append(SEP);
			conditionIndex++;
			sb.append('\n');
		}
	}

	/**
	 * TODO add comments.
	 * 
	 * @param sb
	 */
	protected void addHeaderInfo(final StringBuilder sb) {
		// 
		sb.append("info");

		// add filtering info
		if (getParameter().getFilterConfig().hasFiters()) {
			sb.append(SEP);
			sb.append("filter_info");
		}

		// show reference base
		if (getParameter().showReferenceBase()) {
			sb.append(SEP);
			sb.append("refBase");
		}
	}
	
	/**
	 * Gets the name for a result object.
	 * Currently, the name is always variant - override to change behaviour.
	 * 
	 * @param result object to compute name
	 * @return string that identifies the result
	 */
	protected String getName(final Result<T> result) {
		return "variant";
	}
	
	@Override
	public void writeResult(final R result) {
		final StringBuilder sb = new StringBuilder();

		// add default BED6
		addResultBED6(sb, result, getFieldName());
		// add 
		addResultData(sb, result);
		// add infos
		addResultInfos(sb, result);
		sb.append('\n');

		try {
			getBW().write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void addResultBED6(final StringBuilder sb, final R result, final String fieldName) {
		final ParallelData<T> parallelData = result.getParellelData();

		// coordinates
		sb.append(parallelData.getCoordinate().getContig());
		sb.append(SEP);
		sb.append(parallelData.getCoordinate().getStart() - 1);
		sb.append(SEP);
		sb.append(parallelData.getCoordinate().getEnd());
		
		sb.append(SEP);
		sb.append(fieldName);
		
		sb.append(SEP);
		sb.append(getStatistic(result));

		sb.append(SEP);
		sb.append(parallelData.getCombinedPooledData().getCoordinate().getStrand().character());
	}

	protected abstract String getStatistic(final R result);
	protected abstract String getFieldName();

	protected void addResultData(final StringBuilder sb, final R result) {
		final ParallelData<T> parallelData = result.getParellelData();
		for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); conditionIndex++) {
			for (final T data : parallelData.getData(conditionIndex)) {
				addResultReplicateData(sb, data);
			}
		}
	}
	
	protected void addResultReplicateData(final StringBuilder sb, final T data) {
		addResultBaseCallCount(sb, data);
	}
	
	protected void addResultBaseCallCount(final StringBuilder sb, final T data) {
	}
	
	protected void addResultInfos(final StringBuilder sb, final R result) {
		final ParallelData<T> parallelData = result.getParellelData();

		sb.append(SEP);
		sb.append(result.getResultInfo().combine());
		
		// add filtering info
		if (parameter.getFilterConfig().hasFiters()) {
			sb.append(SEP);
			sb.append(result.getFilterInfo().combine());
		}
		
		if (parameter.showReferenceBase()) {
			sb.append(SEP);
			sb.append((char)parallelData.getCombinedPooledData().getReferenceBase());
		}
	}

	/**
	 * Gets the object that stores the general parameters. 
	 * 
	 * @return parameter object
	 */
	protected AbstractParameter<T, R> getParameter() {
		return parameter;
	}
	
}