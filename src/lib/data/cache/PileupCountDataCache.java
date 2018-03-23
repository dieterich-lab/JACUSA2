package lib.data.cache;

import java.util.Arrays;

import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.PileupCount;
import lib.data.has.hasPileupCount;

public class PileupCountDataCache<T extends AbstractData & hasPileupCount>
extends BaseCallDataCache<T> {

	private final byte[][][] baseCallQualities;
	private final int baseCallQualityRange;
	
	public PileupCountDataCache(final int maxDepth, final byte minBASQ, final BaseCallConfig baseCallConfig, 
			final CoordinateController coordinateController) {

		super(maxDepth, minBASQ, baseCallConfig, coordinateController);

		// range of base call quality score 
		baseCallQualityRange = getMaxBaseCallQuality() - getMinBaseCallQuality();
		
		baseCallQualities = 
				new byte[coordinateController.getActiveWindowSize()][getBaseSize()][baseCallQualityRange];
	}

	@Override
	public void addData(final T data, final Coordinate coordinate) {
		final int windowPosition = getCoordinateController().convert2windowPosition(coordinate);
		if (getCoverage()[windowPosition] == 0) {
			return;
		}

		int[] baseCount = new int[BaseCallConfig.BASES.length];
		byte[][] base2qual = new byte[BaseCallConfig.BASES.length][getMaxBaseCallQuality()];
		byte[] minMapq = new byte[BaseCallConfig.BASES.length];		
		
		System.arraycopy(getBaseCalls()[windowPosition], 0, 
				baseCount, 0, baseCount.length);
		for (int baseIndex = 0; baseIndex < getBaseSize(); ++baseIndex) {
			if (data.getBaseCallCount().getBaseCallCount(baseIndex) > 0) {
				System.arraycopy(
						baseCallQualities[windowPosition][baseIndex], 0, 
						base2qual[baseIndex], getMinBaseCallQuality(), baseCallQualities[windowPosition][baseIndex].length);
				minMapq[baseIndex] = getMinBaseCallQuality();
			} else {
				Arrays.fill(base2qual[baseIndex], (byte)0);
				minMapq[baseIndex] = getMaxBaseCallQuality();
			}
		}
		
		final byte referenceBase = getCoordinateController().getReferenceProvider().getReference(windowPosition);
		// TODO referenceBase set twice
		data.getPileupCount().setReferenceBase(referenceBase);
		final PileupCount pileupCount = new PileupCount(referenceBase, baseCount, base2qual, minMapq);
		data.getPileupCount().add(pileupCount);

		if (coordinate.getStrand() == STRAND.REVERSE) {
			data.getPileupCount().invert();
		}
	}

	@Override
	public void incrementBaseCall(final int windowPosition, final int readPosition,
			final int baseIndex, final byte bq) {
		super.incrementBaseCall(windowPosition, readPosition, baseIndex, bq);
		baseCallQualities[windowPosition][baseIndex][bq - getMinBaseCallQuality()] += 1;
	}
	
	@Override
	public void clear() {
		super.clear();

		for (byte[][] bcs : baseCallQualities) {
			for (byte[] b : bcs) {
				Arrays.fill(b, (byte)0);	
			}
		}
	}

	public int getBaseCallQualities(final int baseIndex, final int baseQualIndex, final int windowPosition) {
		return baseCallQualities[baseIndex][baseQualIndex][windowPosition];
	}
	
}
