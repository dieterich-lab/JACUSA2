package jacusa.io.format;

import jacusa.data.BaseCallConfig;

import jacusa.data.BaseQualReadInfoData;
import jacusa.filter.FilterConfig;

public class RTArrestDebugResultFormat 
extends RTArrestResultFormat {

	public static final char CHAR = 'D';

	public RTArrestDebugResultFormat(
			final BaseCallConfig baseConfig, 
			final FilterConfig<BaseQualReadInfoData> filterConfig,
			final boolean showReferenceBase) {
		super(CHAR, "Debug", baseConfig, filterConfig, showReferenceBase);
	}
	
	/*
	 * Helper function
	 */
	protected void addPileups(StringBuilder sb, BaseQualReadInfoData[] data) {
		// output condition: Ax,Cx,Gx,Tx
		for (BaseQualReadInfoData d : data) {
			sb.append(SEP);

			int i = 0;
			char b = BaseCallConfig.BASES[i];
			int baseIndex = baseConfig.getBaseIndex((byte)b);
			int count = 0;
			if (baseIndex >= 0) {
				count = d.getBaseQualCount().getBaseCount(baseIndex);
			}
			sb.append(count);
			++i;
			for (; i < BaseCallConfig.BASES.length; ++i) {
				b = BaseCallConfig.BASES[i];
				baseIndex = baseConfig.getBaseIndex((byte)b);
				count = 0;
				if (baseIndex >= 0) {
					count = d.getBaseQualCount().getBaseCount(baseIndex);
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