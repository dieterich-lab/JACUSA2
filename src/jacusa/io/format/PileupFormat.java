package jacusa.io.format;

import lib.data.BaseCallConfig;
import lib.data.BaseQualData;
import lib.data.ParallelData;
import lib.data.Result;
import lib.phred2prob.Phred2Prob;

import htsjdk.samtools.SAMUtils;

public class PileupFormat extends AbstractOutputFormat<BaseQualData> {

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
	public String convert2String(final Result<BaseQualData> result) {
		final StringBuilder sb = new StringBuilder();
		final ParallelData<BaseQualData> parallelPileupData = result.getParellelData();

		// coordinates
		sb.append(parallelPileupData.getCoordinate().getContig());
		sb.append(SEP);
		sb.append(parallelPileupData.getCoordinate().getEnd());

		sb.append(getSEP());
		if (showReferenceBase) {
			sb.append(parallelPileupData.getCombinedPooledData().getReferenceBase());
		} else {
			sb.append("N");
		}

		sb.append(SEP);
		sb.append(parallelPileupData.getCoordinate().getStrand());
		
		for (int conditionIndex = 0; conditionIndex < parallelPileupData.getConditions(); conditionIndex++) {
			addPileupData(sb, parallelPileupData.getData(conditionIndex));
		}

		return sb.toString();		
	}
	
	protected void addPileupData(final StringBuilder sb, final BaseQualData[] datas) {
		for (final BaseQualData data : datas) {
			sb.append(SEP);
			sb.append(data.getBaseQualCount().getCoverage());
			sb.append(SEP);
			
			for (int baseIndex : data.getBaseQualCount().getAlleles()) {
				// print bases 
				for (int i = 0; i < data.getBaseQualCount().getBaseCount(baseIndex); ++i) {
					sb.append(baseConfig.getBases()[baseIndex]);
				}
			}

			sb.append(SEP);

			// print quals
			for (int base : data.getBaseQualCount().getAlleles()) {
				for (byte qual = 0; qual < Phred2Prob.MAX_Q; ++qual) {

					int count = data.getBaseQualCount().getQualCount(base, qual);
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