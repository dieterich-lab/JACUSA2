package lib.data.cache;

import java.util.Arrays;

import lib.tmp.CoordinateController;
import lib.tmp.CoordinateController.WindowPositionGuard;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.Coordinate.STRAND;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.PileupCount;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.has.hasPileupCount;

public class PileupCountCache<T extends AbstractData & hasPileupCount> 
extends AbstractCache<T> {

	private final BaseCallConfig baseCallConfig;
	
	private final int maxDepth;
	private final byte minBASQ;

	private final int[] coverage;

	private final byte[] referenceBases;
	private final int[][] baseCalls;

	private final byte[][][] baseCallQualities;
	private final int baseCallQualityRange;
	
	public PileupCountCache(final int maxDepth, final byte minBASQ, final BaseCallConfig baseCallConfig, 
			final CoordinateController coordinateController) {
		super(coordinateController);
		this.baseCallConfig = baseCallConfig;
		
		this.maxDepth = maxDepth;
		this.minBASQ = minBASQ;
		
		// how many bases will be considered
		final int baseSize = getBaseSize();
		
		// range of base call quality score 
		baseCallQualityRange = getMaxBaseCallQuality() - getMinBaseCallQuality();

		coverage = new int[coordinateController.getActiveWindowSize()];
		
		referenceBases = new byte[coordinateController.getActiveWindowSize()];
		Arrays.fill(referenceBases, (byte)'N');
		
		baseCalls = new int[coordinateController.getActiveWindowSize()][baseSize];
		baseCallQualities = new byte[coordinateController.getActiveWindowSize()][baseSize][baseCallQualityRange];
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		for (final AlignmentBlock block : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			final int referencePosition = block.getReferenceStart(); 
			final int readPosition = block.getReadStart() - 1;
			
			// TODO quit when possible
			
			incrementBaseCalls(referencePosition, readPosition, block.getLength(), recordWrapper);
		}
	}
	
	@Override
	public void addRecordWrapperPosition(final int readPosition, final SAMRecordWrapper recordWrapper) {
		final int referencePosition = recordWrapper.getSAMRecord().getReferencePositionAtReadPosition(readPosition);
				incrementBaseCalls(referencePosition, readPosition, 1, recordWrapper);
	}
	
	@Override
	public void addRecordWrapperRegion(final int readPosition, final int length, final SAMRecordWrapper recordWrapper) {
		final int referencePosition = recordWrapper.getSAMRecord().getReferencePositionAtReadPosition(readPosition);
		incrementBaseCalls(referencePosition, readPosition, length, recordWrapper);
	}
	
	@Override
	public void addData(final T data, final Coordinate coordinate) {
		final int windowPosition = coordinateController.convert2windowPosition(coordinate);
		data.getPileupCount().setReferenceBase(referenceBases[windowPosition]);

		if (coverage[windowPosition] == 0) {
			return;
		}
		
		int[] baseCount = new int[BaseCallConfig.BASES.length];
		byte[][] base2qual = new byte[BaseCallConfig.BASES.length][getMaxBaseCallQuality()];
		byte[] minMapq = new byte[BaseCallConfig.BASES.length];		

		System.arraycopy(baseCalls[windowPosition], 0, baseCount, 0, baseCount.length);
		for (int baseIndex = 0; baseIndex < baseCount.length; ++baseIndex) {
			if (baseCount[baseIndex] > 0) {
				System.arraycopy(
						baseCallQualities[windowPosition][baseIndex], 0, 
						base2qual[baseIndex], getMinBaseCallQuality(), baseCallQualities[windowPosition][baseIndex].length);
				minMapq[baseIndex] = getMinBaseCallQuality();

			} else {
				Arrays.fill(base2qual[baseIndex], (byte)0);
				minMapq[baseIndex] = getMaxBaseCallQuality();
			}
		}
		
		final PileupCount pileupCount = new PileupCount(referenceBases[windowPosition], baseCount, base2qual, minMapq);
		data.getPileupCount().add(pileupCount);
		
		if (coordinate.getStrand() == STRAND.REVERSE) {
			data.getPileupCount().invert();
		}
	}

	protected void incrementBaseCalls(final int referencePosition, final int readPosition, int length, 
			final SAMRecordWrapper recordWrapper) {

		if (referencePosition < 0) {
			throw new IllegalArgumentException("Reference Position cannot be < 0! -> outside of alignmentBlock");
		}
		
		final WindowPositionGuard windowPositionGuard = coordinateController.convert(referencePosition, readPosition, length);

		if (windowPositionGuard.getWindowPosition() < 0 && windowPositionGuard.getLength() > 0) {
			throw new IllegalArgumentException("Window position cannot be < 0! -> outside of alignmentBlock");
		}
		
		final SAMRecord record = recordWrapper.getSAMRecord();
		
		for (int j = 0; j < windowPositionGuard.getLength(); ++j) {
			if (maxDepth > 0 && coverage[windowPositionGuard.getWindowPosition() + j] >= maxDepth) {
				continue;
			}
			final int baseIndex = baseCallConfig.getBaseIndex(record.getReadBases()[windowPositionGuard.getReadPosition() + j]);
			if (baseIndex < 0) {
				continue;
			}
			final byte bq = record.getBaseQualities()[windowPositionGuard.getReadPosition() + j];
			if (bq < minBASQ) {
				continue;
			}
			coverage[windowPositionGuard.getWindowPosition() + j] += 1;
			baseCalls[windowPositionGuard.getWindowPosition() + j][baseIndex] += 1;
			baseCallQualities[windowPositionGuard.getWindowPosition() + j][baseIndex][bq - getMinBaseCallQuality()] += 1;
		}
		
		
	}
	
	@Override
	public void clear() {
		Arrays.fill(coverage, 0);
		Arrays.fill(referenceBases, (byte)'N');

		for (int[] b : baseCalls) {
			Arrays.fill(b, 0);	
		}
		for (byte[][] bcs : baseCallQualities) {
			for (byte[] b : bcs) {
				Arrays.fill(b, (byte)0);	
			}
		}
	}
	
	public int getCoverage(final int windowPosition) {
		return coverage[windowPosition];
	}
	
	public int getBaseCalls(final int baseIndex, final int windowPosition) {
		return baseCalls[baseIndex][windowPosition];
	}
	
	public int getBaseCallQualities(final int baseIndex, final int baseQualIndex, final int windowPosition) {
		return baseCallQualities[baseIndex][baseQualIndex][windowPosition];
	}
	
	private int getBaseSize() {
		return baseCallConfig.getBases().length; 
	}
	
	private byte getMaxBaseCallQuality() {
		return baseCallConfig.getMaxBaseCallQuality();
	}

	private byte getMinBaseCallQuality() {
		return minBASQ;
	}
	
}
