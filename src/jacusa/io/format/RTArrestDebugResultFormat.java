package jacusa.io.format;


import jacusa.filter.FilterConfig;
import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.PileupReadInfoData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReadInfoCount;
import lib.data.has.hasReferenceBase;

public class RTArrestDebugResultFormat<T extends AbstractData & hasBaseCallCount & hasReadInfoCount & hasReferenceBase> 
extends RTArrestResultFormat<T> {

	public static final char CHAR = 'D';

	public RTArrestDebugResultFormat(
			final BaseCallConfig baseConfig, 
			final FilterConfig<T> filterConfig,
			final boolean showReferenceBase) {
		super(CHAR, "Debug", baseConfig, filterConfig, showReferenceBase);
	}
	
	/*
	 * Helper function
	 */
	protected void addPileups(StringBuilder sb, PileupReadInfoData[] data) {
		// output condition: Ax,Cx,Gx,Tx
		for (PileupReadInfoData d : data) {
			sb.append(SEP);

			int i = 0;
			char b = BaseCallConfig.BASES[i];
			int baseIndex = baseConfig.getBaseIndex((byte)b);
			int count = 0;
			if (baseIndex >= 0) {
				count = d.getBaseCallCount().getBaseCallCount(baseIndex);
			}
			sb.append(count);
			++i;
			for (; i < BaseCallConfig.BASES.length; ++i) {
				b = BaseCallConfig.BASES[i];
				baseIndex = baseConfig.getBaseIndex((byte)b);
				count = 0;
				if (baseIndex >= 0) {
					count = d.getBaseCallCount().getBaseCallCount(baseIndex);
				}
				sb.append(SEP2);
				sb.append(count);
			}
			sb.append(SEP);
			sb.append(d.getReadInfoCount().getStart());
			sb.append(SEP2);
			sb.append(d.getReadInfoCount().getInner());
			sb.append(SEP2);
			sb.append(d.getReadInfoCount().getEnd());
		}
	}

}