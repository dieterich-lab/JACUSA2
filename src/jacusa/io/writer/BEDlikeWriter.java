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

public abstract class BEDlikeWriter<T extends AbstractData & hasReferenceBase, R extends Result<T>> 
extends AbstractResultFileWriter<T, R> {

	public static final char COMMENT= '#';
	public static final char EMPTY 	= '*';
	public static final char SEP 	= '\t';
	public static final char SEP2 	= ',';

	private AbstractParameter<T, R> parameter;
	
	public BEDlikeWriter(final String filename, final AbstractParameter<T, R> parameter) {
		super(filename);
		
		this.parameter = parameter;
	}

	@Override
	public void writeHeader(final List<AbstractConditionParameter<T>> conditionParameters) {
		final StringBuilder sb = new StringBuilder();

		addHeaderConditionDetails(sb, conditionParameters);
		
		addHeaderBED6(sb);
		addHeaderConditions(sb, conditionParameters);
		addHeaderInfo(sb);
		sb.append('\n');

		try {
			getBW().write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void addHeaderBED6(final StringBuilder sb) {
		sb.append(COMMENT);

		// position (0-based)
		sb.append("contig");
		sb.append(getSEP());
		sb.append("start");
		sb.append(getSEP());
		sb.append("end");
		sb.append(getSEP());

		sb.append("name");
		sb.append(getSEP());

		// stat	
		sb.append(getHeaderStat());
		sb.append(getSEP());
		
		sb.append("strand");
		sb.append(getSEP());
	}
	
	protected String getHeaderStat() {
		return "stat";
	}
	
	protected void addHeaderConditions(final StringBuilder sb, final List<AbstractConditionParameter<T>> conditionParameters) {
		for (int conditionIndex = 0; conditionIndex < conditionParameters.size(); conditionIndex++) {
			final int replicates = conditionParameters.get(conditionIndex).getRecordFilenames().length;
			for (int replicateIndex = 0; replicateIndex < replicates; ++replicateIndex) {
				addHeaderConditionData(sb, conditionIndex, replicateIndex);
			}
		}
		sb.append(getSEP());
	}

	protected void addHeaderConditionData(final StringBuilder sb, final int conditionIndex, final int replicateIndex) {
		addHeaderBases(sb, conditionIndex, replicateIndex);
	}
	
	protected void addHeaderBases(final StringBuilder sb, final int conditionIndex, final int replicateIndex) {
	}
	
	protected void addHeaderConditionDetails(final StringBuilder sb, final List<AbstractConditionParameter<T>> conditionParameters) {
		int conditionIndex = 1;
		for (final AbstractConditionParameter<T> conditionParameter : conditionParameters) {
			sb.append(COMMENT);
			sb.append(COMMENT);
			
			sb.append(" condition");
			sb.append(conditionIndex);
			
			sb.append(" library_type=");
			sb.append(conditionParameter.getLibraryType());
			
			sb.append(" files=");
			final String[] pathnames = conditionParameter.getRecordFilenames();
			sb.append(pathnames[0]);
			for (int replicateIndex = 1; replicateIndex < pathnames.length; replicateIndex++) {
				sb.append(getSEP2());
				sb.append(pathnames[replicateIndex]);
			}
			sb.append(getSEP());
			conditionIndex++;
			sb.append('\n');
		}
	}

	protected void addHeaderInfo(final StringBuilder sb) {
		sb.append("info");

		// add filtering info
		if (getParameter().getFilterConfig().hasFiters()) {
			sb.append(getSEP());
			sb.append("filter_info");
		}
	
		if (getParameter().showReferenceBase()) {
			sb.append(getSEP());
			sb.append("refBase");
		}
	}
	
	protected String getName() {
		return "variant";
	}
	
	@Override
	public void writeResult(final R result) {
		final StringBuilder sb = new StringBuilder();

		addResultBED6(sb, result, getFieldName());

		addResultData(sb, result);

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

		sb.append(getSEP());
		sb.append(result.getResultInfo().combine());
		
		// add filtering info
		if (parameter.getFilterConfig().hasFiters()) {
			sb.append(getSEP());
			sb.append(result.getFilterInfo().combine());
		}
		
		if (parameter.showReferenceBase()) {
			sb.append(getSEP());
			sb.append((char)parallelData.getCombinedPooledData().getReferenceBase());
		}
	}
	
	public char getCOMMENT() {
		return COMMENT;
	}

	public char getEMPTY() {
		return EMPTY;
	}

	public char getSEP() {
		return SEP;
	}
	
	public char getSEP2() {
		return SEP2;
	}

	protected AbstractParameter<T, R> getParameter() {
		return parameter;
	}
	
}