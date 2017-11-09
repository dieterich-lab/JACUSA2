package jacusa.io.format;

import java.util.List;


import jacusa.filter.FilterConfig;
import lib.cli.options.BaseCallConfig;
import lib.cli.parameters.JACUSAConditionParameters;
import lib.data.BaseQualReadInfoData;
import lib.data.ParallelData;
import lib.data.Result;

public class RTArrestResultFormat 
extends AbstractOutputFormat<BaseQualReadInfoData> {

	public static final char CHAR = 'B';
	
	public static final char COMMENT= '#';
	public static final char EMPTY 	= '*';
	public static final char SEP 	= '\t';
	public static final char SEP2 	= ',';
	
	// read start, trough, and end	
	private static final String RTinfo = "reads";
	
	protected FilterConfig<BaseQualReadInfoData> filterConfig;
	protected BaseCallConfig baseConfig;
	private boolean showReferenceBase;

	public RTArrestResultFormat(
			final char c,
			final String desc,
			final BaseCallConfig baseConfig, 
			final FilterConfig<BaseQualReadInfoData> filterConfig,
			final boolean showReferenceBase) {
		super(c, desc);
		
		this.baseConfig = baseConfig;
		this.filterConfig = filterConfig;

		this.showReferenceBase = showReferenceBase;
	}

	public RTArrestResultFormat(
			final BaseCallConfig baseConfig, 
			final FilterConfig<BaseQualReadInfoData> filterConfig,
			final boolean showReferenceBase) {
		this(CHAR, "Default", baseConfig, filterConfig, showReferenceBase);
	}

	@Override
	public String getHeader(final List<JACUSAConditionParameters<BaseQualReadInfoData>> conditionParameters) {
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
		sb.append("pvalue");
		sb.append(getSEP());
		
		sb.append("strand");
		sb.append(getSEP());

		for (int conditionIndex = 0; conditionIndex < conditionParameters.size(); conditionIndex++) {
			addConditionHeader(sb, conditionIndex, conditionParameters.get(conditionIndex).getRecordFilenames().length);
			sb.append(getSEP());
		}
		
		sb.append(getSEP());
		sb.append("info");
		
		// add filtering info
		if (filterConfig.hasFiters()) {
			sb.append(getSEP());
			sb.append("filter_info");
		}

		if (showReferenceBase) {
			sb.append(getSEP());
			sb.append("refBase");
		}
		
		return sb.toString();
	}
	
	protected void addConditionHeader(final StringBuilder sb, int condition, final int replicates) {
		condition += 1;

		sb.append("bases");
		sb.append(condition);
		sb.append(1);
		
		sb.append(SEP);
		
		sb.append(RTinfo);
		sb.append(condition);
		sb.append(1);
		if (replicates == 1) {
			return;
		}
		
		for (int i = 2; i <= replicates; ++i) {
			sb.append(SEP);
			
			sb.append("bases");
			sb.append(condition);
			sb.append(i);
			
			sb.append(SEP);
			
			sb.append(RTinfo);
			sb.append(condition);
			sb.append(i);
		}
	}
	
	@Override
	public String convert2String(Result<BaseQualReadInfoData> result) {
		final ParallelData<BaseQualReadInfoData> parallelData = result.getParellelData();
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
			addPileups(sb, parallelData.getData(conditionIndex));
		}

		sb.append(getSEP());
		sb.append(result.getResultInfo().combine());
		
		// add filtering info
		if (filterConfig.hasFiters()) {
			sb.append(getSEP());
			sb.append(result.getFilterInfo().combine());
		}
		
		if (showReferenceBase) {
			sb.append(getSEP());
			sb.append(parallelData.getCombinedPooledData().getReferenceBase());
		}

		return sb.toString();		
	}
	
	/*
	 * Helper function
	 */
	protected void addPileups(StringBuilder sb, BaseQualReadInfoData[] data) {
		// output condition: Ax,Cx,Gx,Tx
		for (BaseQualReadInfoData d : data) {
			sb.append(SEP);

			int i = 0;
			char b = BaseCallConfig.BASES[i];
			int baseIndex = baseConfig.getBaseIndex((byte)b);
			int count = 0;
			if (baseIndex >= 0) {
				count = d.getPileupCount().getBaseCount(baseIndex);
			}
			sb.append(count);
			++i;
			for (; i < BaseCallConfig.BASES.length; ++i) {
				b = BaseCallConfig.BASES[i];
				baseIndex = baseConfig.getBaseIndex((byte)b);
				count = 0;
				if (baseIndex >= 0) {
					count = d.getPileupCount().getBaseCount(baseIndex);
				}
				sb.append(SEP2);
				sb.append(count);
			}
			sb.append(SEP);
			sb.append(d.getReadInfoCount().getArrest());
			sb.append(SEP2);
			sb.append(d.getReadInfoCount().getThrough());
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