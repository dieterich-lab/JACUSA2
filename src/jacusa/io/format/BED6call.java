package jacusa.io.format;

import java.util.List;

import lib.cli.options.BaseCallConfig;
import lib.cli.parameters.AbstractParameter;
import lib.cli.parameters.JACUSAConditionParameters;
import lib.data.ParallelData;
import lib.data.Result;
import lib.data.basecall.PileupData;

public class BED6call extends AbstractOutputFormat<PileupData> {

	public static final char CHAR = 'B';
	
	public static final char COMMENT= '#';
	public static final char EMPTY 	= '*';
	public static final char SEP 	= '\t';
	public static final char SEP2 	= ',';

	private AbstractParameter<PileupData> parameters;
	
	public BED6call(
			final char c,
			final String desc,
			final AbstractParameter<PileupData> parameters) {
		super(c, desc);
		
		this.parameters = parameters;
	}

	public BED6call(final AbstractParameter<PileupData> parameters) {
		this(CHAR, "Default", parameters);
	}

	@Override
	public String getHeader(final List<JACUSAConditionParameters<PileupData>> conditionParameters) {
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
		
		for (int conditionIndex = 0; conditionIndex < conditionParameters.size(); conditionIndex++) {
			addConditionHeader(sb, conditionIndex, conditionParameters.get(conditionIndex).getRecordFilenames().length);
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
		addConditionLibraryTypeHeader(sb, conditionParameters);

		sb.append("\n");
		addConditionPathnamesHeader(sb, conditionParameters);
		
		return sb.toString();
	}

	protected void addConditionLibraryTypeHeader(final StringBuilder sb, final List<JACUSAConditionParameters<PileupData>> conditions) {
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
		
		for (final JACUSAConditionParameters<PileupData> condition : conditions) {
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
	
	protected void addConditionPathnamesHeader(final StringBuilder sb, final List<JACUSAConditionParameters<PileupData>> conditionParameters) {
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
		
		for (final JACUSAConditionParameters<PileupData> conditionParameter : conditionParameters) {
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

	public String convert2String(Result<PileupData> result) {
		final ParallelData<PileupData> parallelData = result.getParellelData();
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
	protected void addData(final StringBuilder sb, final PileupData[] data) {
		// output condition: Ax,Cx,Gx,Tx
		for (final PileupData d : data) {
			sb.append(SEP);

			int i = 0;
			char b = BaseCallConfig.BASES[i];
			int baseI = parameters.getBaseConfig().getBaseIndex((byte)b);
			int count = 0;
			if (baseI >= 0) {
				count = d.getPileupCount().getBaseCount(baseI);
			}
			sb.append(count);
			++i;
			for (; i < BaseCallConfig.BASES.length; ++i) {
				b = BaseCallConfig.BASES[i];
				baseI = parameters.getBaseConfig().getBaseIndex((byte)b);
				count = 0;
				if (baseI >= 0) {
					count = d.getPileupCount().getBaseCount(baseI);
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