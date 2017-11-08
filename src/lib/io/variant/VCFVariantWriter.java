package lib.io.variant;

import java.util.Calendar;
import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.util.AbstractTool;
import lib.variant.Variant;

public class VCFVariantWriter extends AbstractVariantFileWriter {

	public static final char COMMENT ='#';
	public static final char EMPTY 	='.';
	public static final char SEP 	= '\t';

	public VCFVariantWriter(final String filename, final VCFVariantFormat format) {
		super(filename, format);
	}

	@Override
	public void addHeader(final List<AbstractConditionParameter<?>> conditionParameters) {
		final StringBuilder sb = new StringBuilder();
		
		sb.append("##fileformat=VCFv4.0");
		sb.append('\n');
		
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH);
		int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		sb.append("##fileDate=");
		sb.append(year);
		if (month < 10)
			sb.append(0);	
		sb.append(month);
		if (day < 10)
			sb.append(0);
		sb.append(day);
		sb.append('\n');

		sb.append("##source=");
		sb.append(AbstractTool.getLogger().getTool().getName() + "-" + AbstractTool.getLogger().getTool().getVersion());
		sb.append('\n');

		/* TODO
		// add filter descriptions to header
		for (final AbstractFilterFactory<BaseQualData> filterFactory : filterConfig.getFactories()) {
			sb.append("##FILTER=<ID=");
			sb.append(filterFactory.getC());
			sb.append(",Description=");
			sb.append("\"" + filterFactory.getDesc() + "\"");
			sb.append('\n');
		}
		sb.append("##FORMAT=<ID=DP,Number=1,Type=Integer,Description=\"Read Depth\">\n");
		sb.append("##FORMAT=<ID=BC,Number=4,Type=Integer,Description=\"Base counts A,C,G,T\">\n");
		*/

		String[] cols = {
				"CHROM",
				"POS",
				"ID",
				"REF",
				"ALT",
				"QUAL",
				"FILTER",
				"INFO",
				"FORMAT"
		};
		
		sb.append("#");
		sb.append(cols[0]);
		for (int i = 1; i < cols.length; ++i) {
			sb.append(SEP);
			sb.append(cols[i]);
		}
		
		for (final AbstractConditionParameter<?> conditionParameter : conditionParameters) {
			// FIXME sb.append(conditionParameter.getRecordFilename());
		}
		addLine(sb.toString());
	}
	
	@Override
	public void addVariants(final Variant[] variants, final List<AbstractConditionParameter<?>> conditionParameters) throws Exception {
		final StringBuilder sb = new StringBuilder();

		// TODO
		final String filterInfo = "PASS";

		int conditionIndex = 0;
		String contig = variants[conditionIndex].getCoordinate().getContig();
		int position = variants[conditionIndex].getCoordinate().getPosition();
		String reference = variants[conditionIndex].getReference();
		String alternative = variants[conditionIndex].getAlternative();
		for (; conditionIndex < conditionParameters.size(); conditionIndex++) {
			if (! contig.equals(variants[conditionIndex].getCoordinate().getContig())) {
				throw new Exception("Contigs don't match!");
			}
			if (position != variants[conditionIndex].getCoordinate().getPosition()) {
				throw new Exception("Positions don't match!");
			}
			if (! reference.equals(variants[conditionIndex].getReference())) {
				throw new Exception("Reference info don't match!");
			}
			alternative += "," + variants[conditionIndex].getAlternative();	
		}

		String[] cols = {
				// contig
				contig,
				// position
				Integer.toString(position),
				// ID
				Character.toString(EMPTY),
				// REF
				reference,
				// ALT
				alternative,
				// QUAL
				Character.toString(EMPTY),
				// FILTER
				filterInfo,
				// INFO
				".",
				// FORMAT
				Character.toString(EMPTY) // TODO "DP" + getSEP3() + "BC",
		};

		sb.append(cols[0]);
		for (int i = 1; i < cols.length; ++i) {
			sb.append(SEP);
			sb.append(cols[i]);
		}

		for (conditionIndex = 0; conditionIndex < conditionParameters.size(); conditionIndex++) {
			// TODO format
		}

		addLine(sb.toString());
	}

	@Override
	public AbstractVariantFormat getFormat() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
