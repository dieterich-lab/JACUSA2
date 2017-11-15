package jacusa.io.writer;

import jacusa.cli.parameters.hasStatistic;

import java.io.IOException;
import java.util.List;

import lib.cli.options.BaseCallConfig;
import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReferenceBase;
import lib.data.result.Result;
import lib.io.AbstractResultFileWriter;

public class BEDlikeResultWriter<T extends AbstractData & hasBaseCallCount & hasReferenceBase, R extends Result<T> & hasStatistic> 
extends AbstractResultFileWriter<T, R> {

	public static final char COMMENT= '#';
	public static final char EMPTY 	= '*';
	public static final char SEP 	= '\t';
	public static final char SEP2 	= ',';

	private AbstractParameter<T, R> parameter;
	
	public BEDlikeResultWriter(final String filename, final AbstractParameter<T, R> parameter) {
		super(filename);
		
		this.parameter = parameter;
	}

	@Override
	public void writeHeader(final List<AbstractConditionParameter<T>> conditionParameters) {
		final StringBuilder sb = new StringBuilder();

		addHeaderBED6(sb);
		addHeaderConditions(sb, conditionParameters);
		addHeaderInfo(sb);
		sb.append('\n');

		// TODO make this dependent on parameters
		addHeader2(sb, conditionParameters);
		sb.append('\n');

		addHeader3(sb, conditionParameters);
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
			for (int replicateIndex = 0; replicateIndex <= replicates; ++replicateIndex) {
				addHeaderConditionData(sb, conditionIndex, replicateIndex);
			}
		}
	}

	protected void addHeaderConditionData(final StringBuilder sb, final int conditionIndex, final int replicateIndex) {
		addHeaderBases(sb, conditionIndex, replicateIndex);
	}
	
	protected void addHeaderBases(final StringBuilder sb, final int conditionIndex, final int replicateIndex) {
		sb.append(SEP);
		sb.append("bases");
		sb.append(conditionIndex + 1);
		sb.append(replicateIndex + 1);
	}
	
	protected void addHeader2(final StringBuilder sb, final List<AbstractConditionParameter<T>> conditionParameters) {
		sb.append(COMMENT);

		sb.append(EMPTY);
		sb.append(getSEP());
		sb.append(EMPTY);
		sb.append(getSEP());
		sb.append(EMPTY);
		sb.append(getSEP());

		sb.append(EMPTY);
		sb.append(getSEP());

		// stat	
		sb.append("TODO");
		sb.append(getSEP());
		
		sb.append("TODO");
		sb.append(getSEP());
		
		for (final AbstractConditionParameter<T> conditionParameter : conditionParameters) {
			sb.append(conditionParameter.getLibraryType());
			sb.append(getSEP());
		}
		
		sb.append(EMPTY);
		
		// add filtering info
		if (parameter.getFilterConfig().hasFiters()) {
			sb.append(getSEP());
			sb.append(EMPTY);
		}

		if (parameter.showReferenceBase()) {
			sb.append(getSEP());
			sb.append(EMPTY);
		}
	}
	
	protected void addHeader3(final StringBuilder sb, final List<AbstractConditionParameter<T>> conditionParameters) {
		sb.append(COMMENT);

		sb.append(EMPTY);
		sb.append(getSEP());
		sb.append(EMPTY);
		sb.append(getSEP());
		sb.append(EMPTY);
		sb.append(getSEP());

		sb.append(EMPTY);
		sb.append(getSEP());

		sb.append(EMPTY);
		sb.append(getSEP());
		
		sb.append(EMPTY);
		sb.append(getSEP());
		
		for (final AbstractConditionParameter<T> conditionParameter : conditionParameters) {
			final String[] pathnames = conditionParameter.getRecordFilenames();
			sb.append(pathnames[0]);
			for (int replicateIndex = 1; replicateIndex < pathnames.length; replicateIndex++) {
				sb.append(getSEP2());
				sb.append(pathnames[replicateIndex]);
			}
			sb.append(getSEP());
		}

		sb.append(EMPTY);
		
		// add filtering info
		if (parameter.getFilterConfig().hasFiters()) {
			sb.append(getSEP());
			sb.append(EMPTY);
		}

		if (parameter.showReferenceBase()) {
			sb.append(getSEP());
			sb.append(EMPTY);
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
	
	protected String getStatistic(final R result) {
		return "NA";
	}
	
	protected String getName() {
		return "variant";
	}
	
	@Override
	public void writeResult(final R result) {
		final StringBuilder sb = new StringBuilder();

		addResultBED6(sb, result);

		addResultData(sb, result);

		addResultInfos(sb, result);
		sb.append('\n');

		try {
			getBW().write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void addResultBED6(final StringBuilder sb, final R result) {
		final ParallelData<T> parallelData = result.getParellelData();

		// coordinates
		sb.append(parallelData.getCoordinate().getContig());
		sb.append(SEP);
		sb.append(parallelData.getCoordinate().getStart() - 1);
		sb.append(SEP);
		sb.append(parallelData.getCoordinate().getEnd());
		
		sb.append(SEP);
		sb.append("variant");
		
		sb.append(SEP);
		sb.append(Double.toString(result.getStatistic()));

		sb.append(SEP);
		sb.append(parallelData.getCombinedPooledData().getCoordinate().getStrand().character());
	}
	
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
		// output condition: Ax,Cx,Gx,Tx
		sb.append(SEP);

		int i = 0;
		byte baseByte = (byte)BaseCallConfig.BASES[i];
		int baseIndex = parameter.getBaseConfig().getBaseIndex(baseByte);
		int count = 0;
		if (baseIndex >= 0) {
			count = data.getBaseCallCount().getBaseCallCount(baseIndex);
		}
		sb.append(count);
		++i;
		for (; i < BaseCallConfig.BASES.length; ++i) {
			baseByte = (byte)BaseCallConfig.BASES[i];
			baseIndex = parameter.getBaseConfig().getBaseIndex(baseByte);
			count = 0;
			if (baseIndex >= 0) {
				count = data.getBaseCallCount().getBaseCallCount(baseIndex);
			}
			sb.append(SEP2);
			sb.append(count);
		}
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
			sb.append(parallelData.getCombinedPooledData().getReferenceBase());
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