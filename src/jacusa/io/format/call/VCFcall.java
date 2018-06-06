package jacusa.io.format.call;

import jacusa.filter.FilterConfig;

import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.HasPileupCount;
import lib.data.result.Result;
import lib.io.AbstractResultFormat;
import lib.io.ResultWriter;

/**
 * TODO add comments.
 * TODO use htsjdk implementation
 *
 * @param <T>
 * @param <R>
 */
public class VCFcall<T extends AbstractData & HasPileupCount, R extends Result<T>> 
extends AbstractResultFormat<T, R> {

	// unique char id for CLI
	public static final char CHAR 		= 'V';
	
	private final FilterConfig<T> filterConfig;
	
	public VCFcall(final AbstractParameter<T, R> parameter) {
		super(CHAR, "VCF Output format. Option -P will be ignored (VCF is unstranded)", parameter);
		this.filterConfig 	= parameter.getFilterConfig();
	}

	/* remove
	public String addHeader(final List<AbstractConditionParameter<T>> conditionParameters) {
		final StringBuilder sb = new StringBuilder();

		// VCF format version
		sb.append(COMMENT);
		sb.append("#fileformat=VCFv4.0");
		sb.append('\n');
		
		// date 
		final int year = Calendar.getInstance().get(Calendar.YEAR);
		final int month = Calendar.getInstance().get(Calendar.MONTH);
		final int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		sb.append("##fileDate=");
		sb.append(year);
		if (month < 10)
			sb.append(0);	
		sb.append(month);
		if (day < 10)
			sb.append(0);
		sb.append(day);
		sb.append('\n');

		// name and version of JACUSA...
		sb.append("##source=");
		sb.append(AbstractTool.getLogger().getTool().getName() + "-" + AbstractTool.getLogger().getTool().getVersion());
		sb.append('\n');

		// add filter descriptions to header
		for (final AbstractFilterFactory<T> filterFactory : filterConfig.getFilterFactories()) {
			sb.append("##FILTER=<ID=");
			sb.append(filterFactory.getC());
			sb.append(",Description=");
			sb.append("\"" + filterFactory.getDesc() + "\"");
			sb.append('\n');
		}
		sb.append("##FORMAT=<ID=DP,Number=1,Type=Integer,Description=\"Read Depth\">\n");
		sb.append("##FORMAT=<ID=BC,Number=4,Type=Integer,Description=\"Base counts A,C,G,T\">\n");
		
		// column names of header
		final String[] cols = {
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
		sb.append(COMMENT);
		sb.append(cols[0]);
		for (int i = 1; i < cols.length; ++i) {
			sb.append(FIELD_SEP);
			sb.append(cols[i]);
		}

		// filename of condition and replicate BAMs
		for (final AbstractConditionParameter<T> conditionParameter : conditionParameters) {
			for (String recordFilename : conditionParameter.getRecordFilenames())  {
				sb.append(FIELD_SEP);
				sb.append(recordFilename);
			}
		}

		return sb.toString();
	}
	*/

	// remove
	/*
	private String convert2String(final R result) {
		final StringBuilder sb = new StringBuilder();
		final ParallelData<T> parallelData = result.getParellelData();
		String filterInfo = result.getFilterInfo().combine();
		if (filterInfo == null || filterInfo.length() == 0) {
			filterInfo = "PASS";
		}

		StringBuilder sb2 = new StringBuilder();
		boolean first = true;
		for (int allelIndex : parallelData.getCombinedPooledData().getPileupCount().getBaseCallCount().getAlleles()) {
			if (parallelData.getCombinedPooledData().getPileupCount().getReferenceBase() != baseConfig.getBases()[allelIndex]) {
				if (! first) {
					sb2.append(',');
				} else {
					first = false;
				}
				sb2.append(baseConfig.getBases()[allelIndex]);
			}
		}

		String[] cols = {
				// contig
				parallelData.getCombinedPooledData().getCoordinate().getContig(),
				// position
				Integer.toString(parallelData.getCombinedPooledData().getCoordinate().getEnd()),
				// ID
				Character.toString(getEMPTY()),
				// REF
				Byte.toString(parallelData.getCombinedPooledData().getPileupCount().getReferenceBase()),
				// ALT
				sb2.toString(),
				// QUAL
				Character.toString(getEMPTY()),
				// FILTER
				filterInfo,
				// INFO
				".",
				// FORMAT
				"DP" + getSEP3() + "BC",
		};

		sb.append(cols[0]);
		for (int i = 1; i < cols.length; ++i) {
			sb.append(getSEP());
			sb.append(cols[i]);
		}

		for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); conditionIndex++) {
			addParallelPileup(sb, parallelData.getData(conditionIndex));
		}
		
		return sb.toString();
	}
	*/

	/* TODO do we need this?
	private void addParallelPileup(final StringBuilder sb, final T data[]) {
		for (int i = 0; i < data.length; ++i) {
			// add DP
			sb.append(getSEP());
			sb.append(data[i].getPileupCount().getCoverage());
			
			sb.append(getSEP3());
			
			// add BC - base counts
			int j = 0;
			char b = BaseCallConfig.BASES[j];
			int baseIndex = baseConfig.getBaseIndex((byte)b);
			int count = 0;
			if (baseIndex >= 0) {
				count = data[i].getPileupCount().getBaseCallCount().getBaseCallCount(baseIndex);
			}
			sb.append(count);
			++j;
			for (; j < BaseCallConfig.BASES.length; ++j) {
				b = BaseCallConfig.BASES[j];
				baseIndex = baseConfig.getBaseIndex((byte)b);
				count = 0;
				if (baseIndex >= 0) {
					count = data[i].getPileupCount().getBaseCallCount().getBaseCallCount(baseIndex);
				}
				sb.append(',');
				sb.append(count);
			}
		}
	}
	*/

	@Override
	public ResultWriter<T, R> createWriter(String filename) {
		return new VCFcallWriter<T, R>(filename, filterConfig); 
	}
	
}