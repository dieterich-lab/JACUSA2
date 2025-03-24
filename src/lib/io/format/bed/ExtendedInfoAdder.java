package lib.io.format.bed;

import lib.cli.parameter.GeneralParameter;
import lib.data.ParallelData;
import lib.data.result.Result;
import lib.io.InputOutput;
import lib.util.ExtendedInfo;

public class ExtendedInfoAdder implements InfoAdder {
	
	private final GeneralParameter parameter;
	
	public ExtendedInfoAdder(final GeneralParameter parameter) {
		this.parameter = parameter;
	}
	
	@Override
	public void addHeader(StringBuilder sb) {
		// add filtering info
		sb.append(InputOutput.FIELD_SEP);
		sb.append("filter");

		// always show reference base
		sb.append(InputOutput.FIELD_SEP);
		sb.append("ref");
	}

	@Override
	public void addData(StringBuilder sb, int valueIndex, Result result) {
		final ParallelData parallelData = result.getParellelData();

		// add filtering info
		sb.append(InputOutput.FIELD_SEP);
		if (parameter.getFilterConfig().hasFiters()) {
			sb.append(result.getFilterInfo(valueIndex).combine());
		} else {
			sb.append(InputOutput.EMPTY_FIELD);
		}

		// always show reference
		sb.append(InputOutput.FIELD_SEP);
		sb.append(parallelData.getCombPooledData().getAutoRefBase());

		final ExtendedInfo info = result.getResultInfo(valueIndex);
		for (final String key : ExtendedInfo.REGISTERED_KEYS) {
			sb.append(InputOutput.FIELD_SEP);
			sb.append(info.
					getRegisteredKeyValues().
					getOrDefault(key, Character.toString(InputOutput.EMPTY_FIELD)));
		}
	}
}
