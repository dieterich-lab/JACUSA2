package lib.io;

import java.util.List;

import jacusa.io.format.FilterDebugAdder;
import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.data.ParallelData;
import lib.data.result.Result;
import lib.io.format.bed.BED6adder;
import lib.io.format.bed.DataAdder;
import lib.io.format.bed.HeaderDetailAdder;
import lib.io.format.bed.InfoAdder;
import lib.io.format.bed.JACUSAHeaderDetailAdder;
import lib.util.Builder;

public class BEDlikeResultFileWriter extends AbstractResultFileWriter {

	private final HeaderDetailAdder headerDetailAdder;
	private final BED6adder bed6Adder;
	private final DataAdder dataAdder;
	private final InfoAdder infoAdder;
	
	private BEDlikeResultFileWriter(final BEDlikeResultFileWriterBuilder builder) {
		super(builder.fileName);
		headerDetailAdder = builder.headerDetailAdder;
		bed6Adder = builder.bed6adder;
		dataAdder = builder.dataAdder;
		infoAdder = builder.infoAdder;
	}

	@Override
	public void writeHeader(List<ConditionParameter> conditionParameter) {
		final StringBuilder sb = new StringBuilder();
		headerDetailAdder.add(sb, conditionParameter);
		bed6Adder.addHeader(sb);
		for (int condI = 0; condI < conditionParameter.size(); ++condI) {
			final int replicateSize = conditionParameter.get(condI).getReplicateSize();
			for (int replicateI = 0; replicateI < replicateSize; ++replicateI) {
				dataAdder.addHeader(sb, condI, replicateI);
			}
		}
		infoAdder.addHeader(sb);
		writeLine(sb.toString());
	}
	
	@Override
	public void writeResult(Result result) {
		final ParallelData parallelData = result.getParellelData();
		for (final int valueIndex : result.getValuesIndex()) {
			final StringBuilder sb = new StringBuilder();
			bed6Adder.addData(sb, valueIndex, result);
			for (int condI = 0; condI < parallelData.getConditions(); ++condI) {
				final int replicateSize = parallelData.getReplicates(condI);
				for (int replicateI = 0; replicateI < replicateSize; ++replicateI) {
					dataAdder.addData(sb, valueIndex, condI, replicateI, result);
				}
			}
			infoAdder.addData(sb, valueIndex, result);
			writeLine(sb.toString());
		}
	}
	
	public static class BEDlikeResultFileWriterBuilder
	implements Builder<BEDlikeResultFileWriter> {

		private final String fileName;
		private final GeneralParameter parameter;
		
		private HeaderDetailAdder headerDetailAdder;
		private BED6adder bed6adder;
		private DataAdder dataAdder;
		private InfoAdder infoAdder;
		
		public BEDlikeResultFileWriterBuilder(
				final String fileName, 
				final GeneralParameter parameter) {
			
			this.fileName = fileName;
			this.parameter = parameter;
			
			// header defaults - can be overridden
			headerDetailAdder = new JACUSAHeaderDetailAdder();
		}
		
		public BEDlikeResultFileWriterBuilder addHeaderDetailAdder(HeaderDetailAdder headerDetailAdder) {
			this.headerDetailAdder = headerDetailAdder;
			return this;
		}
		
		public BEDlikeResultFileWriterBuilder addBED6Adder(BED6adder bed6adder) {
			this.bed6adder = bed6adder;
			return this;
		}
		
		public BEDlikeResultFileWriterBuilder addDataAdder(DataAdder dataAdder) {
			this.dataAdder = dataAdder;
			return this;
		}
		
		public BEDlikeResultFileWriterBuilder addInfoAdder(InfoAdder info) {
			this.infoAdder = info;
			return this;
		}
		
		public BEDlikeResultFileWriter build() {
			if (parameter.isDebug()) {
				dataAdder = new FilterDebugAdder(
						parameter.getFilterConfig().getFilterFactories(),
						dataAdder);
			}
			return new BEDlikeResultFileWriter(this);
		}
		
	}
	
}
