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

	private PileupCount pileupCount;

	private  char[] bases;
	private byte[] quals;
	
	private Random random;

	public SamplePileupCount(final String seed) {
		if (seed == null) {
			random = new Random();
		} else {
			random = new Random(Long.parseLong(seed));			
		}
	}

	public SamplePileupCount() {
		this(null);
	}

	public void setPileupCount(final PileupCount pileupCount) {
		this.pileupCount = pileupCount;
		final int reads = pileupCount.getReads();
		bases = new char[reads];
		quals = new byte[reads];
		
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
	
	// TODO howto sample modications
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

	
	
}
