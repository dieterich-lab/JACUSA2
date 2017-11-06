package jacusa.io.format;

import java.util.List;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.BaseQualData;
import jacusa.data.BaseCallConfig;
import jacusa.data.ParallelPileupData;
import jacusa.data.Result;

public class BED6call extends AbstractOutputFormat<BaseQualData> {

	public static final char CHAR = 'B';
	
	public static final char COMMENT= '#';
	public static final char EMPTY 	= '*';
	public static final char SEP 	= '\t';
	public static final char SEP2 	= ',';

	private AbstractParameters<BaseQualData> parameters;
	
	public BED6call(
			final char c,
			final String desc,
			final AbstractParameters<BaseQualData> parameters) {
		super(c, desc);
		
		this.parameters = parameters;
	}

	public BED6call(final AbstractParameters<BaseQualData> parameters) {
		this(CHAR, "Default", parameters);
	}

	@Override
	public String getHeader(final List<ConditionParameters<BaseQualData>> conditions) {
		final StringBuilder sb = new StringBuilder();

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
		sb.append("stat");
		sb.append(getSEP());
		
		sb.append("strand");
		sb.append(getSEP());
		
		for (int conditionIndex = 0; conditionIndex < conditions.size(); conditionIndex++) {
			addConditionHeader(sb, conditionIndex, conditions.get(conditionIndex).getPathnames().length);
			sb.append(getSEP());
		}
		
		sb.append("info");
		
		// add filtering info
		if (parameters.getFilterConfig().hasFiters()) {
			sb.append(getSEP());
			sb.append("filter_info");
		}

		if (parameters.showReferenceBase()) {
			sb.append(getSEP());
			sb.append("refBase");
		}

		sb.append("\n");
		addConditionLibraryTypeHeader(sb, conditions);

		sb.append("\n");
		addConditionPathnamesHeader(sb, conditions);
		
		return sb.toString();
	}

	protected void addConditionLibraryTypeHeader(final StringBuilder sb, final List<ConditionParameters<BaseQualData>> conditions) {
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
		
		for (final ConditionParameters<BaseQualData> condition : conditions) {
			sb.append(condition.getLibraryType());
			sb.append(getSEP());
		}
		
		sb.append(EMPTY);
		
		// add filtering info
		if (parameters.getFilterConfig().hasFiters()) {
			sb.append(getSEP());
			sb.append(EMPTY);
		}

		if (parameters.showReferenceBase()) {
			sb.append(getSEP());
			sb.append(EMPTY);
		}
	}
	
	protected void addConditionPathnamesHeader(final StringBuilder sb, final List<ConditionParameters<BaseQualData>> conditions) {
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
		
		for (final ConditionParameters<BaseQualData> condition : conditions) {
			final String[] pathnames = condition.getPathnames();
			sb.append(pathnames[0]);
			for (int replicateIndex = 1; replicateIndex < pathnames.length; replicateIndex++) {
				sb.append(getSEP2());
				sb.append(pathnames[replicateIndex]);
			}
			sb.append(getSEP());
		}

		sb.append(EMPTY);
		
		// add filtering info
		if (parameters.getFilterConfig().hasFiters()) {
			sb.append(getSEP());
			sb.append(EMPTY);
		}

		if (parameters.showReferenceBase()) {
			sb.append(getSEP());
			sb.append(EMPTY);
		}
	}
	
	protected void addConditionHeader(final StringBuilder sb, final int condition, final int replicates) {
		sb.append("bases");
		sb.append(condition + 1);
		sb.append(1);
		if (replicates == 1) {
			return;
		}
		
		for (int i = 2; i <= replicates; ++i) {
			sb.append(SEP);
			sb.append("bases");
			sb.append(condition + 1);
			sb.append(i);
		}
	}

	public String convert2String(Result<BaseQualData> result) {
		final ParallelPileupData<BaseQualData> parallelData = result.getParellelData();
		final double statistic = result.getStatistic();
		final StringBuilder sb = new StringBuilder();

		// coordinates
		sb.append(parallelData.getCoordinate().getContig());
		sb.append(SEP);
		sb.append(parallelData.getCoordinate().getStart() - 1);
		sb.append(SEP);
		sb.append(parallelData.getCoordinate().getEnd());
		
		sb.append(SEP);
		sb.append("variant");
		
		sb.append(SEP);
		if (Double.isNaN(statistic)) {
			sb.append("NA");
		} else {
			sb.append(statistic);
		}

		sb.append(SEP);
		sb.append(parallelData.getCombinedPooledData().getCoordinate().getStrand().character());

		for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); conditionIndex++) {
			addData(sb, parallelData.getData(conditionIndex));
		}

		sb.append(getSEP());
		sb.append(result.getResultInfo().combine());
		
		// add filtering info
		if (parameters.getFilterConfig().hasFiters()) {
			sb.append(getSEP());
			sb.append(result.getFilterInfo().combine());
		}
		
		if (parameters.showReferenceBase()) {
			sb.append(getSEP());
			sb.append(parallelData.getCombinedPooledData().getReferenceBase());
		}

		return sb.toString();		
	}
	
	/*
	 * Helper function
	 */
	protected void addData(final StringBuilder sb, final BaseQualData[] data) {
		// output condition: Ax,Cx,Gx,Tx
		for (final BaseQualData d : data) {
			sb.append(SEP);

			int i = 0;
			char b = BaseCallConfig.BASES[i];
			int baseI = parameters.getBaseConfig().getBaseIndex((byte)b);
			int count = 0;
			if (baseI >= 0) {
				count = d.getBaseQualCount().getBaseCount(baseI);
			}
			sb.append(count);
			++i;
			for (; i < BaseCallConfig.BASES.length; ++i) {
				b = BaseCallConfig.BASES[i];
				baseI = parameters.getBaseConfig().getBaseIndex((byte)b);
				count = 0;
				if (baseI >= 0) {
					count = d.getBaseQualCount().getBaseCount(baseI);
				}
				sb.append(SEP2);
				sb.append(count);
			}
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

}