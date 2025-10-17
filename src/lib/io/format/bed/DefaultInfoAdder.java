package lib.io.format.bed;

import java.util.Map.Entry;

import lib.cli.parameter.GeneralParameter;
import lib.data.ParallelData;
import lib.data.result.Result;
import lib.io.InputOutput;
import lib.util.ExtendedInfo;

public class DefaultInfoAdder implements InfoAdder {
	
	private final GeneralParameter parameter;
	
	public DefaultInfoAdder(final GeneralParameter parameter) {
		this.parameter = parameter;
	}
	
	@Override
	public void addHeader(StringBuilder sb) {
		sb.append(InputOutput.FIELD_SEP);
		sb.append("info");

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

		// add result info
		sb.append(InputOutput.FIELD_SEP);
		final ExtendedInfo info = result.getResultInfo(valueIndex);

		if (info.NumericallyInstable) {
			sb.append("NumericallyInstable");
			sb.append(InputOutput.SEP4);
		}
		
		if (info.getMap().isEmpty()) {
			sb.append(InputOutput.EMPTY_FIELD);
		} else {
			for (final Entry<String, String> entry : info.getMap().entrySet()) {
				sb.append(entry.getKey());
				sb.append(InputOutput.KEY_VALUE_SEP);
				sb.append(entry.getValue());
				sb.append(InputOutput.SEP4);
			}
		}
		
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
	}

}
