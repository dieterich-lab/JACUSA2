package lib.io;

import jacusa.io.format.BEDlikeResultWriter;
import jacusa.io.format.BEDlikeWriter;
import lib.cli.options.Base;
import lib.data.cache.lrtarrest.RefPos2BaseCallCount;
import lib.data.count.BaseCallCount;

public abstract class ResultWriterUtils {

	public static void addBaseCallCount(final StringBuilder sb, final BaseCallCount baseCallCount) {
		if (baseCallCount == null) {
			sb.append(BEDlikeWriter.EMPTY_FIELD);
			return;
		}
		
		int i = 0;
		for (final Base base : Base.validValues()) {
			sb.append(baseCallCount.getBaseCall(base));
			if (i < Base.validValues().length - 1)
			sb.append(BEDlikeResultWriter.VALUE_SEP);
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
			sb.append(BEDlikeResultWriter.SEP3);

			int k = 0;
			for (final Base base : Base.validValues()) {
				sb.append(baseCallCount.getBaseCall(base));
				if (k < Base.validValues().length - 1)
				sb.append(BEDlikeResultWriter.VALUE_SEP);
				++k;
			}

			++j;
			if (j < n) {
				sb.append(BEDlikeResultWriter.SEP4);
			}
		}
		if (n == 0 || i == 0) {
			sb.append(BEDlikeResultWriter.EMPTY_FIELD);
			return;
		}
	}
	
}
