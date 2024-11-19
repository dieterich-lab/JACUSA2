
package lib.data.downsample;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import lib.data.count.INDELCount;
import lib.data.count.PileupCount;
import lib.data.count.basecallquality.BaseCallQualityCount;
import lib.data.count.basecallquality.MapBaseCallQualityCount;
import lib.util.Base;

public class SamplePileupCount {

	private final PileupCount pileupCount;

	private  char[] bases;
	private byte[] quals;
	
	private final Random random;
	
	public SamplePileupCount(final PileupCount pileupCount) {
		this.pileupCount = pileupCount;
		
		int offset = 0;
		int offset2 = 0;
		bases = new char[pileupCount.getReads()];
		quals = new byte[pileupCount.getReads()];
		// prepare arrays to sample from
		for (final Base base : pileupCount.getBCC().getAlleles()) {
			for (int i = 0; i < pileupCount.getBCC().getBaseCall(base); ++i) {
				bases[offset] = base.getChar();
				offset++;
			}
			for (byte qual : pileupCount.getBaseCallQualityCount().getBaseCallQuality(base)) {
				for (int j = 0; j < pileupCount.getBaseCallQualityCount().getBaseCallQuality(base, qual); ++j) {
					quals[offset2] = qual;
					offset2++;
				}
			}
		}
		if (pileupCount.getINDELCount().getInsertionCount() > 0) {
			for (int i = 0; i < pileupCount.getINDELCount().getInsertionCount(); ++i) {
				bases[offset] = 'I';
				offset++;
			}
		}
		if (pileupCount.getINDELCount().getDeletionCount() > 0) {
			for (int i = 0; i < pileupCount.getINDELCount().getDeletionCount(); ++i) {
				bases[offset] = 'D';
				offset++;
			}
		}
		
		random = new Random();
	}
	
	public PileupCount sample(final int targetReads) {
		final Map<Base, Map<Byte, Integer>> newBaseCallQuals = 
				new HashMap<Base, Map<Byte,Integer>>(pileupCount.getBCC().getAlleles().size());
		int insertions = 0;
		int deletions = 0;
		for (int i = 0; i < targetReads; ++i) {
			final int randomI = random.nextInt(pileupCount.getReads());
			
			final char c = bases[randomI];
			switch (c) {
			case 'I':
				insertions++;
				break;
			case 'D':
				deletions++;
				break;
				
			default:
				final Base newBase = Base.valueOf(c);
				final byte newQual = quals[randomI];
				
				if (!newBaseCallQuals.containsKey(newBase)) {
					newBaseCallQuals.put(newBase, new HashMap<Byte, Integer>());
				}
				int qualCount = 0;
				if (newBaseCallQuals.get(newBase).containsKey(newQual)) {
					qualCount = newBaseCallQuals.get(newBase).get(newQual);
				}
				newBaseCallQuals.get(newBase).put(newQual, qualCount + 1);
				break;
			}
		}
		final BaseCallQualityCount bcqc = new MapBaseCallQualityCount(newBaseCallQuals);
		final INDELCount indelCount = new INDELCount(insertions, deletions);
		return new PileupCount(bcqc, indelCount);
	}
	
	/*
	int minimalReads = Integer.MAX_VALUE;
	for (DataContainer data : parallelData.getCombinedData()) {
		final int reads = data.getPileupCount().getReads();
		if (reads > 0) {
			minimalReads = Math.min(minimalReads, reads);
		}
	}
	minimalReads = (int)Math.ceil(minimalReads * ratio);
	
	// TODO
	
	// TODO Auto-generated method stub
	return null;
	*/
	
}

