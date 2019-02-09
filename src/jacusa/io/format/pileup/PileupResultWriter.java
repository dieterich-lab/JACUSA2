package jacusa.io.format.pileup;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import lib.cli.parameter.ConditionParameter;
import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.count.PileupCount;
import lib.data.result.Result;
import lib.io.AbstractResultFileWriter;
import lib.util.Base;
import htsjdk.samtools.SAMUtils;

/**
 * TODO add comments.
 *
 * @param 
 * @param <R>
 */
public class PileupResultWriter
extends AbstractResultFileWriter {

	// char to start a comment
	// split columns by
	public static final char SEP 	= '\t';
	// split within columns by

	// indicates if the reference base should be added as a column 
	private final boolean showReferenceBase;
	
	public PileupResultWriter(
			final String outputFileName, 
			final boolean showReferenceBase) {
		
		super(outputFileName);
		this.showReferenceBase = showReferenceBase;
	}

	/**
	 * Converts a result object to a string.
	 * 
	 * @param result the object to be converted
	 * @return a string representation of the result object
	 */
	private String convert2String(final Result result) {
		final StringBuilder sb = new StringBuilder();
		final ParallelData parallelData = result.getParellelData();

		// coordinates
		sb.append(parallelData.getCoordinate().getContig());
		sb.append(SEP);
		sb.append(parallelData.getCoordinate().getEnd());

		// add reference
		sb.append(SEP);
		if (showReferenceBase) {
			sb.append(parallelData.getCombinedPooledData().getReferenceBase());
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
	 * @param containers
	 */
	protected void addPileupData(final StringBuilder sb, final List<DataContainer> containers) {
		for (final DataContainer container : containers) {
			final PileupCount pileupCount = container.getPileupCount();
			sb.append(SEP);
			sb.append(pileupCount.getBaseCallCount().getCoverage());
			sb.append(SEP);
			
			final Set<Base> alleles = pileupCount.getBaseCallCount().getAlleles();
			// print bases
			for (final Base base : alleles) {
				final int count = pileupCount.getBaseCallCount().getBaseCall(base);
				// repeat count times
				for (int i = 0; i < count; ++i) {
					sb.append(base.getByte());
				}
			}

			sb.append(SEP);

			// print quals
			for (final Base base : alleles) {
				final Set<Byte> baseQuals = new TreeSet<Byte>(pileupCount.getBaseCallQualityCount().getBaseCallQuality(base));
				for (final byte baseQual : baseQuals) {
					final int count = pileupCount.getBaseCallQualityCount().getBaseCallQuality(base, baseQual);
					// repeat count times
					for (int j = 0; j < count; ++j) {
						sb.append(SAMUtils.phredToFastq(baseQual));
					}
				}
			}
		}
	}

	@Override
	public void writeHeader(List<ConditionParameter> conditionParameter) {
		// pileup result format has to header
	}

	@Override
	public void writeResult(Result result) {
		writeLine(convert2String(result));
	}

}