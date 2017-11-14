package jacusa.io.format;

import jacusa.cli.parameters.RTArrestParameter;
import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.BaseCallReadInfoData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReadInfoCount;

public class RTArrestDebugResultFormat<T extends AbstractData & hasBaseCallCount & hasReadInfoCount> 
extends RTArrestResultFormat<T> {

	public static final char CHAR = 'D';

	public RTArrestDebugResultFormat(
			final RTArrestParameter<T> rtArrestParameter) {
		super(CHAR, "Debug", rtArrestParameter);
	}
	
	/*
	 * Helper function
	 */
	protected void addPileups(StringBuilder sb, BaseCallReadInfoData[] data) {
		// output condition: Ax,Cx,Gx,Tx
		for (BaseCallReadInfoData d : data) {
			sb.append(SEP);

			int i = 0;
			char b = BaseCallConfig.BASES[i];
			int baseIndex = getParameter().getBaseConfig().getBaseIndex((byte)b);
			int count = 0;
			if (baseIndex >= 0) {
				count = d.getBaseCallCount().getBaseCallCount(baseIndex);
			}
			sb.append(count);
			++i;
			for (; i < BaseCallConfig.BASES.length; ++i) {
				b = BaseCallConfig.BASES[i];
				baseIndex = getParameter().getBaseConfig().getBaseIndex((byte)b);
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