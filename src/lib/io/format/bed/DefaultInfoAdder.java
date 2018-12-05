package lib.io.format.bed;

import lib.cli.parameter.GeneralParameter;
import lib.data.ParallelData;
import lib.data.result.Result;
import lib.util.Util;

public class DefaultInfoAdder implements InfoAdder {
	
	private final GeneralParameter parameter;
	
	public DefaultInfoAdder(final GeneralParameter parameter) {
		this.parameter = parameter;
	}
	
	@Override
	public void addHeader(StringBuilder sb) {
		sb.append(Util.FIELD_SEP);
		sb.append("info");

		// add filtering info
		if (parameter.getFilterConfig().hasFiters()) {
			sb.append(Util.FIELD_SEP);
			sb.append("filter_info");
		}

		// show reference base
		if (parameter.showReferenceBase()) {
			sb.append(Util.FIELD_SEP);
			sb.append("refBase");
		}
	}

	@Override
	public void addData(StringBuilder sb, int valueIndex, Result result) {
		final ParallelData parallelData = result.getParellelData();

		sb.append(Util.FIELD_SEP);
		sb.append(result.getResultInfo(valueIndex).combine());
		
		// add filtering info
		if (parameter.getFilterConfig().hasFiters()) {
			sb.append(Util.FIELD_SEP);
			sb.append(result.getFilterInfo(valueIndex).combine());
		}
		
		if (parameter.showReferenceBase()) {
			sb.append(Util.FIELD_SEP);
			sb.append(parallelData.getCombinedPooledData().getReferenceBase());
		}
	}

}
