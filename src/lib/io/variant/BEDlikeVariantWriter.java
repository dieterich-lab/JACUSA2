package lib.io.variant;

import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.util.Coordinate.STRAND;
import lib.variant.Variant;

public class BEDlikeVariantWriter extends AbstractVariantFileWriter {

	public final static char COMMENT = '\t';
	public final static char SEP = '\t'; 
	
	public BEDlikeVariantWriter(final String filename, final BEDlikeVariantFormat format) {
		super(filename, format);
	}

	@Override
	public void addHeader(final List<AbstractConditionParameter<?>> conditionParameters) {
		final StringBuilder sb = new StringBuilder();

		sb.append(COMMENT);
		sb.append("contig");
		sb.append(SEP);
		sb.append("start");
		sb.append(SEP);
		sb.append("end");
		sb.append(SEP);
		sb.append("name");
		sb.append(SEP);
		sb.append("strand");
		sb.append(SEP);
		sb.append("TODO");
		
		addLine(sb.toString());
	}
	
	@Override
	public void addVariants(final Variant[] variants, final List<AbstractConditionParameter<?>> conditionParameters) throws Exception {
		final StringBuilder sb = new StringBuilder();
		
		int conditionIndex = 0;
		String contig = variants[conditionIndex].getCoordinate().getContig();
		int start = variants[conditionIndex].getCoordinate().getStart();
		int end = variants[conditionIndex].getCoordinate().getEnd();
		STRAND strand = variants[conditionIndex].getCoordinate().getStrand();
		String reference = variants[conditionIndex].getReference();
		String alternative = variants[conditionIndex].getAlternative();
		int check = 1;
		conditionIndex++;
		
		for (; conditionIndex < conditionParameters.size(); conditionIndex++) {
			if (! contig.equals(variants[conditionIndex].getCoordinate().getContig())) {
				throw new Exception("Contigs don't match!");
			}
			if (start != variants[conditionIndex].getCoordinate().getStart()) {
				throw new Exception("Start positions don't match!");
			}
			if (end != variants[conditionIndex].getCoordinate().getEnd()) {
				throw new Exception("End positions don't match!");
			}
			if (strand != variants[conditionIndex].getCoordinate().getStrand()) {
				throw new Exception("Strand information doesn't match!");
			}
			if (! reference.equals(variants[conditionIndex].getReference())) {
				throw new Exception("Reference info don't match!");
			}
			alternative += "," + variants[conditionIndex].getAlternative();

			check++;
		}

		sb.append(contig);
		sb.append(SEP);
		sb.append(start);
		sb.append(SEP);
		sb.append(end);
		sb.append(SEP);
		sb.append(reference + ">" + alternative);
		sb.append(SEP);
		sb.append(check);
		sb.append(SEP);
		sb.append(strand.character());
		
		addLine(sb.toString());
	}

	@Override
	public AbstractVariantFormat getFormat() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
