package jacusa.io.format;

import java.util.List;

import lib.cli.options.BaseCallConfig;
import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.Result;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReadInfoCount;

public class RTArrestResultFormat<T extends AbstractData & hasBaseCallCount & hasReadInfoCount> 
extends AbstractOutputFormat<T> {

	public static final char CHAR = 'B';
	
	public static final char COMMENT= '#';
	public static final char EMPTY 	= '*';
	public static final char SEP 	= '\t';
	public static final char SEP2 	= ',';
	
	// read start, trough, and end	
	private static final String RTinfo = "reads";

	private AbstractParameter<T> parameter;
	
	protected RTArrestResultFormat(
			final char c,
			final String desc,
			final AbstractParameter<T> parameters) {
		super(c, desc);
		
		this.parameter = parameters;
	}

	public RTArrestResultFormat(final AbstractParameter<T> parameters) {
		this(CHAR, "Default", parameters);
	}

	@Override
	public String getHeader(final List<AbstractConditionParameter<T>> conditionParameters) {
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
		if (parameter.getFilterConfig().hasFiters()) {
			sb.append(getSEP());
			sb.append("filter_info");
		}

		if (parameter.showReferenceBase()) {
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
			
			if (parameter.isDebug()) {
				sb.append("read");
				sb.append(condition);
				sb.append(i);
			}
		}
	}
	
	@Override
	public String convert2String(Result<T> result) {
		final ParallelData<T> parallelData = result.getParellelData();
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
		if (parameter.getFilterConfig().hasFiters()) {
			sb.append(getSEP());
			sb.append(result.getFilterInfo().combine());
		}
		
		/* FIXME
		if (showReferenceBase) {
			sb.append(getSEP());
			sb.append(Byte.toString(parallelData.getCombinedPooledData().getReferenceBase()));
		}
		*/

		return sb.toString();		
	}
	
	/*
	 * Helper function
	 */
	protected void addPileups(StringBuilder sb, T[] dataArray) {
		// output condition: Ax,Cx,Gx,Tx
		for (T data : dataArray) {
			sb.append(SEP);

			int i = 0;
			char b = BaseCallConfig.BASES[i];
			int baseIndex = parameter.getBaseConfig().getBaseIndex((byte)b);
			int count = 0;
			if (baseIndex >= 0) {
				count = data.getBaseCallCount().getBaseCallCount(baseIndex);
			}
			sb.append(count);
			++i;
			for (; i < BaseCallConfig.BASES.length; ++i) {
				b = BaseCallConfig.BASES[i];
				baseIndex = parameter.getBaseConfig().getBaseIndex((byte)b);
				count = 0;
				if (baseIndex >= 0) {
					count = data.getBaseCallCount().getBaseCallCount(baseIndex);
				}
				sb.append(SEP2);
				sb.append(count);
			}
			sb.append(SEP);
			sb.append(data.getReadInfoCount().getArrest());
			sb.append(SEP2);
			sb.append(data.getReadInfoCount().getThrough());
		
			if (parameter.isDebug()) {
				sb.append(SEP);
				sb.append(data.getReadInfoCount().getStart());
				sb.append(SEP2);
				sb.append(data.getReadInfoCount().getInner());
				sb.append(SEP2);
				sb.append(data.getReadInfoCount().getEnd());
			}
		}
	}
	
	public AbstractParameter<T> getParameter() {
		return parameter;
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