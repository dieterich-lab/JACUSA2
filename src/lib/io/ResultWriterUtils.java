package lib.io;

import jacusa.io.format.BEDlikeResultWriter;
import jacusa.io.format.BEDlikeWriter;
import lib.cli.options.BaseCallConfig;
import lib.data.cache.lrtarrest.RefPos2BaseCallCount;
import lib.data.count.BaseCallCount;

public abstract class ResultWriterUtils {

	public static void addBaseCallCount(final StringBuilder sb, final BaseCallCount baseCallCount) {
		if (baseCallCount == null) {
			sb.append(BEDlikeWriter.EMPTY);
			return;
		}
		
		// output condition: Ax,Cx,Gx,Tx
		sb.append(BEDlikeResultWriter.SEP);
		
		int i = 0;
		byte baseByte = (byte)BaseCallConfig.BASES[i];
		int baseIndex = BaseCallConfig.getInstance().getBaseIndex(baseByte);
		int count = 0;
		if (baseIndex >= 0) {
			count = baseCallCount.getBaseCall(baseIndex);
		}
		sb.append(count);
		++i;
		for (; i < BaseCallConfig.BASES.length; ++i) {
			baseByte = (byte)BaseCallConfig.BASES[i];
			baseIndex = BaseCallConfig.getInstance().getBaseIndex(baseByte);
			count = 0;
			if (baseIndex >= 0) {
				count = baseCallCount.getBaseCall(baseIndex);
			}
			sb.append(BEDlikeResultWriter.SEP2);
			sb.append(count);
		}
	}

	public static void addResultRefPos2baseChange(final StringBuilder sb, final RefPos2BaseCallCount refPos2BaseCallCount) {
		final int n = refPos2BaseCallCount.getRefPos().size();
		int j = 0;
		int i = 0;
		for (final int refPos : refPos2BaseCallCount.getRefPos()) {
			final BaseCallCount baseCallCount = refPos2BaseCallCount.getBaseCallCount(refPos);
			if (baseCallCount.getCoverage() == 0) {
				continue;
			}
			++i;

			sb.append(refPos);
			sb.append(BEDlikeResultWriter.SEP3);

			int baseIndex = 0;
			int count = 0;
			if (baseIndex >= 0) {
				count = baseCallCount.getBaseCall(baseIndex);
			}
			sb.append(count);
			++baseIndex;
			for (; baseIndex < BaseCallConfig.BASES.length; ++baseIndex) {
				count = 0;
				if (baseIndex >= 0) {
					count = baseCallCount.getBaseCall(baseIndex);
				}
				sb.append(BEDlikeResultWriter.SEP2);
				sb.append(count);
			}

			++j;
			if (j < n) {
				sb.append(BEDlikeResultWriter.SEP4);
			}
		}
		if (n == 0 || i == 0) {
			sb.append(BEDlikeResultWriter.EMPTY);
			return;
		}
	}
	
}
