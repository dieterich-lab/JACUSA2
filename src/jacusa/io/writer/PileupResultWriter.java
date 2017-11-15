package jacusa.io.writer;

import java.util.List;

import lib.cli.options.BaseCallConfig;
import lib.cli.parameters.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.hasPileupCount;
import lib.data.result.Result;
import lib.io.AbstractResultFileWriter;
import lib.phred2prob.Phred2Prob;

import htsjdk.samtools.SAMUtils;

public class PileupResultWriter<T extends AbstractData & hasPileupCount, R extends Result<T>> 
extends AbstractResultFileWriter<T, R> {

	public static char EMPTY 	= '*';
	public static char COMMENT = '#';
	public static char SEP 	= '\t';
	public static char SEP2 	= ',';

	private boolean showReferenceBase;
	private BaseCallConfig baseConfig;

	public PileupResultWriter(final String filename, 
			final BaseCallConfig baseConfig, final boolean showReferenceBase) {

		super(filename);
		this.showReferenceBase = showReferenceBase;
		this.baseConfig = baseConfig;
	}

	public String convert2String(final R result) {
		final StringBuilder sb = new StringBuilder();
		final ParallelData<T> parallelData = result.getParellelData();

		// coordinates
		sb.append(parallelData.getCoordinate().getContig());
		sb.append(SEP);
		sb.append(parallelData.getCoordinate().getEnd());

		sb.append(getSEP());
		if (showReferenceBase) {
			sb.append(parallelData.getCombinedPooledData().getPileupCount().getReferenceBase());
		} else {
			sb.append("N");
		}

		sb.append(SEP);
		sb.append(parallelData.getCoordinate().getStrand());
		
		for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); conditionIndex++) {
			addPileupData(sb, parallelData.getData(conditionIndex));
		}

		return sb.toString();		
	}
	
	protected void addPileupData(final StringBuilder sb, final T[] dataArray) {
		for (final T data : dataArray) {
			sb.append(SEP);
			sb.append(data.getPileupCount().getCoverage());
			sb.append(SEP);
			
			final int[] alleles = data.getPileupCount().getBaseCallCount().getAlleles();
			for (int baseIndex : alleles) {
				// print bases 
				for (int i = 0; i < data.getPileupCount().getBaseCallCount().getBaseCallCount(baseIndex); ++i) {
					sb.append(baseConfig.getBases()[baseIndex]);
				}
			}

			sb.append(SEP);

			// print quals
			for (int base : alleles) {
				for (byte qual = 0; qual < Phred2Prob.MAX_Q; ++qual) {

					int count = data.getPileupCount().getQualCount(base, qual);
					if (count > 0) {
						// repeat count times
						for (int j = 0; j < count; ++j) {
							sb.append(SAMUtils.phredToFastq(qual));
						}
					}
				}
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

	@Override
	public void writeHeader(List<AbstractConditionParameter<T>> conditionParameter) {
		// no header - no output
	}

	@Override
	public void writeResult(R result) {
		addLine(convert2String(result));
	}

}