package jacusa.io.writer;

import java.util.List;

import lib.cli.options.BaseCallConfig;
import lib.cli.parameter.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.hasPileupCount;
import lib.data.result.Result;
import lib.io.AbstractResultFileWriter;
import lib.phred2prob.Phred2Prob;

import htsjdk.samtools.SAMUtils;

/**
 * TODO add comments.
 *
 * @param <T>
 * @param <R>
 */
public class PileupResultWriter<T extends AbstractData & hasPileupCount, R extends Result<T>> 
extends AbstractResultFileWriter<T, R> {

	// empty field, e.g.: no filters, no infos etc.
	public static final char EMPTY 	= '*';
	// char to start a comment
	public static final char COMMENT = '#';
	// separate columns by
	public static final char SEP 	= '\t';
	// separate within columns by
	public static final char SEP2 	= ',';

	private final BaseCallConfig baseConfig;
	// indicates if the reference base should be added as a column 
	private final boolean showReferenceBase;

	public PileupResultWriter(final String filename, 
			final BaseCallConfig baseConfig, final boolean showReferenceBase) {

		super(filename);
		this.baseConfig = baseConfig;
		this.showReferenceBase = showReferenceBase;
	}

	/**
	 * Converts a result object to a string.
	 * 
	 * @param result the object to be converted
	 * @return a string representation of the result object
	 */
	private String convert2String(final R result) {
		final StringBuilder sb = new StringBuilder();
		final ParallelData<T> parallelData = result.getParellelData();

		// coordinates
		sb.append(parallelData.getCoordinate().getContig());
		sb.append(SEP);
		sb.append(parallelData.getCoordinate().getEnd());

		// add reference
		sb.append(SEP);
		if (showReferenceBase) {
			sb.append(parallelData.getCombinedPooledData().getPileupCount().getReferenceBase());
		} else {
			sb.append("N");
		}

		// add strand
		sb.append(SEP);
		sb.append(parallelData.getCoordinate().getStrand().character());
		
		// add pileup data
		for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); conditionIndex++) {
			addPileupData(sb, parallelData.getData(conditionIndex));
		}

		return sb.toString();		
	}
	
	/**
	 * TODO add comments.
	 * 
	 * @param sb
	 * @param dataArray
	 */
	protected void addPileupData(final StringBuilder sb, final T[] dataArray) {
		for (final T data : dataArray) {
			sb.append(SEP);
			sb.append(data.getPileupCount().getCoverage());
			sb.append(SEP);
			
			final int[] alleles = data.getPileupCount().getBaseCallCount().getAlleles();
			// print bases
			for (final int baseIndex : alleles) {
				final int count = data.getPileupCount().getBaseCallCount().getBaseCallCount(baseIndex);
				// repeat count times
				for (int i = 0; i < count; ++i) {
					sb.append(baseConfig.getBases()[baseIndex]);
				}
			}

			sb.append(SEP);

			// print quals
			for (final int base : alleles) {
				for (byte qual = 0; qual < Phred2Prob.MAX_Q; ++qual) {
					final int count = data.getPileupCount().getQualCount(base, qual);
					// repeat count times
					for (int j = 0; j < count; ++j) {
						sb.append(SAMUtils.phredToFastq(qual));
					}
				}
			}
		}
	}

	@Override
	public void writeHeader(List<AbstractConditionParameter<T>> conditionParameter) {
		// pileup result format has to header
	}

	@Override
	public void writeResult(R result) {
		addLine(convert2String(result));
	}

}