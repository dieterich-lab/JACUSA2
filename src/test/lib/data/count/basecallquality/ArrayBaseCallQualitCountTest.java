package test.lib.data.count.basecallquality;

import java.util.Map;

import htsjdk.samtools.util.SequenceUtil;
import lib.data.count.basecallquality.BaseCallQualityCount;
import lib.phred2prob.Phred2Prob;
import lib.data.count.basecallquality.ArrayBaseCallQualityCount;
import lib.util.Base;

public class ArrayBaseCallQualitCountTest extends BaseCallQualityCountTest {

	@Override
	protected BaseCallQualityCount createBaseCallQualityCount(final Map<Base, Map<Byte, Integer>> base2qual2count) {
		final int[][] arrayBase2qual2count = new int[SequenceUtil.VALID_BASES_UPPER.length][Phred2Prob.MAX_Q];
		for (final Base base : base2qual2count.keySet()) {
			Map<Byte, Integer> qual2count = base2qual2count.get(base);
			for (final byte bite : qual2count.keySet()) {
				final int count = qual2count.get(bite);
				arrayBase2qual2count[base.getIndex()][bite] = count;
			}
		}
		return new ArrayBaseCallQualityCount(arrayBase2qual2count);
	}

}
