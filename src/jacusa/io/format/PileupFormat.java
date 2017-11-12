package jacusa.io.format;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.Result;
import lib.data.has.hasPileupCount;
import lib.phred2prob.Phred2Prob;

import htsjdk.samtools.SAMUtils;

public class PileupFormat<T extends AbstractData & hasPileupCount> 
extends AbstractOutputFormat<T> {

	public final static char CHAR = 'M';
	public static char EMPTY 	= '*';
	public static char COMMENT = '#';
	public static char SEP 	= '\t';
	public static char SEP2 	= ',';

	private boolean showReferenceBase;
	private BaseCallConfig baseConfig;

	public PileupFormat(final BaseCallConfig baseConfig, final boolean showReferenceBase) {
		super(CHAR, "samtools mpileup like format (base columns without: $ ^ < > *)");
		this.showReferenceBase = showReferenceBase;
		this.baseConfig = baseConfig;
	}

	@Override
	public String convert2String(final Result<T> result) {
		final StringBuilder sb = new StringBuilder();
		final ParallelData<T> parallelData = result.getParellelData();

		// coordinates
		sb.append(parallelData.getCoordinate().getContig());
		sb.append(SEP);
		sb.append(parallelData.getCoordinate().getEnd());

		sb.append(getSEP());
		if (showReferenceBase) {
			sb.append(parallelData.getCombinedPooledData().getReferenceBase());
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
			
			for (int baseIndex : data.getPileupCount().getAlleles()) {
				// print bases 
				for (int i = 0; i < data.getPileupCount().getBaseCount(baseIndex); ++i) {
					sb.append(baseConfig.getBases()[baseIndex]);
				}
			}

			sb.append(SEP);

			// print quals
			for (int base : data.getPileupCount().getAlleles()) {
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

}