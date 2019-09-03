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

public class PileupResultWriter extends AbstractResultFileWriter {

	// char to start a comment
	// split columns by
	public static final char SEP 	= '\t';
	// split within columns by
	
	public PileupResultWriter(final String outputFileName) {
		super(outputFileName);
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
		sb.append(parallelData.getCombPooledData().getAutoReferenceBase());

		// add strand
		sb.append(SEP);
		sb.append(parallelData.getCoordinate().getStrand().character());
		
		// add pileup data
		for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); conditionIndex++) {
			addPileupData(sb, parallelData.getData(conditionIndex));
		}

		return sb.toString();		
	}
	
	protected void addPileupData(final StringBuilder sb, final List<DataContainer> containers) {
		for (final DataContainer container : containers) {
			final PileupCount pileupCount = container.getPileupCount();
			sb.append(SEP);
			sb.append(pileupCount.getBCC().getCoverage());
			sb.append(SEP);
			
			final Set<Base> alleles = pileupCount.getBCC().getAlleles();
			// print bases
			for (final Base base : alleles) {
				final int count = pileupCount.getBCC().getBaseCall(base);
				// repeat count times
				for (int i = 0; i < count; ++i) {
					sb.append(base.getByte());
				}
			}

			sb.append(SEP);

			// print quals
			for (final Base base : alleles) {
				final Set<Byte> baseQuals = new TreeSet<>(pileupCount.getBaseCallQualityCount().getBaseCallQuality(base));
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