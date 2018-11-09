package lib.io;

import java.util.ArrayList;
import java.util.List;

import jacusa.io.format.BaseSubstitutionBED6adder;
import jacusa.io.format.BaseSubstitutionDataAdder;
import jacusa.io.format.FilterDebugAdder;
import lib.cli.options.has.HasReadSubstitution.BaseSubstitution;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.ParallelData;
import lib.data.count.basecall.BaseCallCount;
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
	public void writeHeader(List<AbstractConditionParameter> conditionParameter) {
		final StringBuilder sb = new StringBuilder();
		headerDetailAdder.add(sb, conditionParameter);
		bed6Adder.addHeader(sb);
		for (int conditionIndex = 0; conditionIndex < conditionParameter.size(); ++conditionIndex) {
			final int replicateSize = conditionParameter.get(conditionIndex).getReplicateSize();
			for (int replicateIndex = 0; replicateIndex < replicateSize; ++replicateIndex) {
				dataAdder.addHeader(sb, conditionIndex, replicateIndex);
			}
		}
		infoAdder.addHeader(sb);
		writeLine(sb.toString());
	}
	
	@Override
	public void writeResult(Result result) {
		final ParallelData parallelData = result.getParellelData();
		for (final int value : result.getValues()) {
			final StringBuilder sb = new StringBuilder();
			bed6Adder.addData(sb, value, result);
			for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); ++conditionIndex) {
				final int replicateSize = parallelData.getReplicates(conditionIndex);
				for (int replicateIndex = 0; replicateIndex < replicateSize; ++replicateIndex) {
					dataAdder.addData(sb, value, conditionIndex, replicateIndex, result);
				}
			}
			infoAdder.addData(sb, value, result);
			writeLine(sb.toString());
		}
	}
	
	public static class BEDlikeResultFileWriterBuilder
	implements Builder<BEDlikeResultFileWriter> {

		private final String fileName;
		private final AbstractParameter parameter;
		
		private HeaderDetailAdder headerDetailAdder;
		private BED6adder bed6adder;
		private DataAdder dataAdder;
		private InfoAdder infoAdder;
		
		public BEDlikeResultFileWriterBuilder(
				final String fileName, 
				final AbstractParameter parameter) {
			
			this.fileName = fileName;
			this.parameter = parameter;
			
			// header defaults - can be overridden
			headerDetailAdder = new JACUSAHeaderDetailAdder();
		}
		
		public BEDlikeResultFileWriterBuilder addHeaderDetailAdder(HeaderDetailAdder headerDetailAdder) {
			this.headerDetailAdder = headerDetailAdder;
			return this;
		}
		
		public BEDlikeResultFileWriterBuilder addBED6Adder(BED6adder BED6adder) {
			this.bed6adder = BED6adder;
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
		
		public BEDlikeResultFileWriterBuilder addBaseSubstition(BaseCallCount.AbstractParser bccParser) {
			if (parameter.getReadSubstitutions().size() > 0) {
				final List<BaseSubstitution> baseSubs = new ArrayList<>(parameter.getReadSubstitutions()); 
				bed6adder = new BaseSubstitutionBED6adder(baseSubs, bed6adder);
				dataAdder = new BaseSubstitutionDataAdder(bccParser, baseSubs, dataAdder);
			}
			return this;
		}
		
		public BEDlikeResultFileWriterBuilder addFilterDebug() {
			if (parameter.isDebug()) {
				dataAdder = new FilterDebugAdder(
						parameter.getFilterConfig().getFilterFactories(),
						dataAdder);
			}
			return this;
		}
		
		public BEDlikeResultFileWriter build() {
			return new BEDlikeResultFileWriter(this);
		}
		
	}
	
}
