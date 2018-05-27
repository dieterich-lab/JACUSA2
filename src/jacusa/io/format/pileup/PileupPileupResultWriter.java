package jacusa.io.format.pileup;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import lib.cli.options.Base;
import lib.cli.parameter.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.HasPileupCount;
import lib.data.result.Result;
import lib.io.AbstractResultFileWriter;

import htsjdk.samtools.SAMUtils;

/**
 * TODO add comments.
 *
 * @param <T>
 * @param <R>
 */
public class PileupPileupResultWriter<T extends AbstractData & HasPileupCount, R extends Result<T>> 
extends AbstractResultFileWriter<T, R> {

	// empty field, e.g.: no filters, no infos etc.
	public static final char EMPTY 	= '*';
	// char to start a comment
	public static final char COMMENT = '#';
	// separate columns by
	public static final char SEP 	= '\t';
	// separate within columns by
	public static final char SEP2 	= ',';

	// indicates if the reference base should be added as a column 
	private final boolean showReferenceBase;

	public PileupPileupResultWriter(final String filename, final boolean showReferenceBase) {
		super(filename);
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
			
			final Set<Base> alleles = data.getPileupCount().getBaseCallCount().getAlleles();
			// print bases
			for (final Base base : alleles) {
				final int count = data.getPileupCount().getBaseCallCount().getBaseCall(base);
				// repeat count times
				for (int i = 0; i < count; ++i) {
					sb.append(base.getC());
				}
			}

			sb.append(SEP);

			// print quals
			for (final Base base : alleles) {
				final Set<Byte> baseQuals = new TreeSet<Byte>(data.getPileupCount().getBaseCallQualityCount().getBaseCallQuality(base));
				for (final byte baseQual : baseQuals) {
					final int count = data.getPileupCount().getBaseCallQualityCount().getBaseCallQuality(base, baseQual);
					// repeat count times
					for (int j = 0; j < count; ++j) {
						sb.append(SAMUtils.phredToFastq(baseQual));
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
		writeLine(convert2String(result));
	}

}