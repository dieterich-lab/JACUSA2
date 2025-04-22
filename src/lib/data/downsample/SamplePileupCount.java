package lib.data.downsample;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

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
		bases = new char[pileupCount.getReads()];
		quals = new byte[pileupCount.getReads()];

		random = new Random(Long.parseLong("1234"));
		
		init();
	}
	
	private void init() {
		int baseOffset = 0;
		int qualOffset = 0;
		
		Set<Base> alleles = new TreeSet<Base>();
		alleles.addAll(pileupCount.getBCC().getAlleles());
		// prepare arrays to sample from
		for (final Base base : alleles) {
			final int base_count = pileupCount.getBCC().getBaseCall(base);
			Arrays.fill(bases, baseOffset, baseOffset + base_count, base.getChar());
			baseOffset += base_count;
			for (byte qual : pileupCount.getBaseCallQualityCount().getBaseCallQuality(base)) {
				final int qual_count = pileupCount.getBaseCallQualityCount().getBaseCallQuality(base, qual);
				Arrays.fill(quals, qualOffset, qualOffset + qual_count, qual);
				qualOffset += qual_count;
			}
		}
		if (pileupCount.getINDELCount().getInsertionCount() > 0) {
			final int n = pileupCount.getINDELCount().getInsertionCount();
			Arrays.fill(bases, baseOffset, baseOffset + n, 'I');
			baseOffset += n;
		}
		if (pileupCount.getINDELCount().getDeletionCount() > 0) {
			final int n = pileupCount.getINDELCount().getDeletionCount();
			Arrays.fill(bases, baseOffset, baseOffset + n, 'D');
			baseOffset += n;
		}
	}
	
	public PileupCount sample(final int targetReads) {
		final Map<Base, Map<Byte, Integer>> newBaseCallQuals = 
				new HashMap<Base, Map<Byte,Integer>>(pileupCount.getBCC().getAlleles().size());
		int insertions = 0;
		int deletions = 0;
		final int reads = pileupCount.getReads();
		for (int i = 0; i < targetReads; ++i) {
			final int randomI = random.nextInt(reads);
			
			final char c = bases[randomI];
			switch (c) {
			case 'I': // FIXME - modifications (Inosin) vs. insertion
				insertions++;
				break; 
			case 'D': // FIXME - modifications
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
	
}
