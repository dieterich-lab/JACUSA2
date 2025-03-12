package lib.io.format.bed;

import java.util.Set;

import lib.cli.parameter.GeneralParameter;
import lib.data.ParallelData;
import lib.data.result.Result;
import lib.io.InputOutput;

public class ExpandedInfoAdder implements InfoAdder {
	
	private final GeneralParameter parameter;
	private final Set<String> additionalHeader;
	
	public ExpandedInfoAdder(final GeneralParameter parameter, final Set<String> additionalHeader) {
		this.parameter = parameter;
		this.additionalHeader = additionalHeader;
	}
	
	@Override
	public void addHeader(StringBuilder sb) {
		// add filtering info
		sb.append(InputOutput.FIELD_SEP);
		sb.append("info");
				
		// add filtering info
		sb.append(InputOutput.FIELD_SEP);
		sb.append("filter");

		// always show reference base
		sb.append(InputOutput.FIELD_SEP);
		sb.append("ref");
		
		for (final String s : additionalHeader) {
			sb.append(InputOutput.FIELD_SEP);
			sb.append(s);
		}
	}

	@Override
	public void addData(StringBuilder sb, int valueIndex, Result result) {
		final ParallelData parallelData = result.getParellelData();

		// always show reference
		sb.append(InputOutput.FIELD_SEP);
		sb.append(value); // TODO add stuff that is not registered
		
		// add filtering info
		sb.append(InputOutput.FIELD_SEP);
		if (parameter.getFilterConfig().hasFiters()) {
			sb.append(result.getFilterInfo(valueIndex).combine());
		}

		// always show reference
		sb.append(InputOutput.FIELD_SEP);
		sb.append(parallelData.getCombPooledData().getAutoRefBase());
		
		// add result info
		for (final String value : result.getResultInfo(valueIndex).getValues()) {
			sb.append(InputOutput.FIELD_SEP);
			sb.append(value);
		}
	}

}
