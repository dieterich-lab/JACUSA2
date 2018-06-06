package lib.io;

import lib.data.cache.lrtarrest.RefPos2BaseCallCount;
import lib.data.count.BaseCallCount;
import lib.util.Base;
import lib.util.Util;

public abstract class ResultWriterUtils {

	public static void addBaseCallCount(final StringBuilder sb, final BaseCallCount baseCallCount) {
		if (baseCallCount == null) {
			sb.append(Util.EMPTY_FIELD);
			return;
		}
		
		int i = 0;
		for (final Base base : Base.validValues()) {
			sb.append(baseCallCount.getBaseCall(base));
			if (i < Base.validValues().length - 1)
			sb.append(Util.VALUE_SEP);
			++i;
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
			sb.append(Util.WITHIN_FIELD_SEP);

			int k = 0;
			for (final Base base : Base.validValues()) {
				sb.append(baseCallCount.getBaseCall(base));
				if (k < Base.validValues().length - 1)
				sb.append(Util.VALUE_SEP);
				++k;
			}

			++j;
			if (j < n) {
				sb.append(Util.SEP4);
			}
		}
		if (n == 0 || i == 0) {
			sb.append(Util.EMPTY_FIELD);
			return;
		}
	}
	
}
