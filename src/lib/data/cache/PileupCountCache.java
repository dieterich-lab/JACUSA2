package lib.data.cache;

import java.util.Arrays;

import lib.method.AbstractMethodFactory;
import lib.phred2prob.Phred2Prob;
import lib.util.Coordinate;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;

import lib.cli.options.BaseCallConfig;
import lib.cli.parameters.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.basecall.PileupCount;
import lib.data.builder.SAMRecordWrapper;
import lib.data.has.hasPileupCount;

public class PileupCountCache<T extends AbstractData & hasPileupCount> 
extends AbstractCache<T> {

	private final AbstractConditionParameter<T> conditionParameter;
	
	private final int[] coverage;

	private final byte[] referenceBases;
	private final int[][] baseCalls;

	private final int[][][] baseCallQualities;
	private final int baseCallQualityRange;
	
	public PileupCountCache(final AbstractConditionParameter<T> conditionParamter, final AbstractMethodFactory<T> methodFactory) {
		super(methodFactory);
		this.conditionParameter = conditionParamter;
	
		// how many bases will be considered
		final int baseSize = getBaseSize();
		
		// range of base call quality score 
		final byte maxBaseCallQuality = getMaxBaseCallQuality();
		final byte minBaseCallQuality = conditionParamter.getMinBASQ();
		baseCallQualityRange = maxBaseCallQuality - minBaseCallQuality + 1;

		coverage = new int[getActiveWindowSize()];
		
		referenceBases = new byte[getActiveWindowSize()];
		// TODO remove and make this from read using MD string
		Arrays.fill(referenceBases, (byte)'N');
		
		baseCalls = new int[getActiveWindowSize()][baseSize];
		baseCallQualities = new int[getActiveWindowSize()][baseSize][baseCallQualityRange];
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		for (final AlignmentBlock block : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			final int referencePosition = block.getReferenceStart() - 1; 
			final int readPosition = block.getReadStart() - 1;
			incrementBaseCalls(referencePosition, readPosition, block.getLength(), recordWrapper);
		}
	}
	
	@Override
	public void addRecordWrapperPosition(final int readPosition, final SAMRecordWrapper recordWrapper) {
		addRecordWrapperRegion(readPosition, 1, recordWrapper);
	}
	
	@Override
	public void addRecordWrapperRegion(final int readPosition, final int length, final SAMRecordWrapper recordWrapper) {
		final int referencePosition = recordWrapper.getSAMRecord().getReferencePositionAtReadPosition(readPosition);
		incrementBaseCalls(referencePosition, readPosition, length, recordWrapper);
	}
	
	@Override
	public T getData(final Coordinate coordinate) {
		final T data = getDataGenerator().createData();
		final int windowPosition = Coordinate.makeRelativePosition(getActiveWindowCoordinate(), coordinate.getPosition());
		data.setCoordinate(new Coordinate(coordinate));
		
		int[] baseCount = new int[BaseCallConfig.BASES.length];
		byte[][] base2qual = new byte[BaseCallConfig.BASES.length][Phred2Prob.MAX_Q];
		byte[] minMapq = new byte[BaseCallConfig.BASES.length];		

		if (coverage[windowPosition] > 0) {
			System.arraycopy(baseCount, 0, baseCalls[windowPosition], 0, baseCount.length);
			for (int baseIndex = 0; baseIndex < baseCount.length; ++baseIndex) {
				if (baseCount[baseIndex] > 0) {
					System.arraycopy(
							base2qual[baseIndex], conditionParameter.getMinBASQ(), 
							baseCallQualities[baseIndex][windowPosition], 0, base2qual[baseIndex].length);
					minMapq[baseIndex] = conditionParameter.getMinBASQ();
				} else {
					minMapq[baseIndex] = Phred2Prob.MAX_Q;
				}
			}
		}
		
		final PileupCount pileupCount = new PileupCount(referenceBases[windowPosition], baseCount, base2qual, minMapq);
		data.setPileupCount(pileupCount);

		return data;
	}

	protected void incrementBaseCalls(final int referencePosition, int readPosition, int length, 
			final SAMRecordWrapper recordWrapper) {

		final WindowPosition windowPosition = getWindowPosition(referencePosition);

		if (windowPosition.leftOffset < 0) {
			windowPosition.i += -windowPosition.leftOffset;
			windowPosition.rightOffset += windowPosition.leftOffset;
			windowPosition.leftOffset += windowPosition.leftOffset;
			if (windowPosition.leftOffset < 0) {
				return;
			}
			
			readPosition += -windowPosition.leftOffset;
			length += windowPosition.leftOffset;
		}

		if (windowPosition.rightOffset > 0) {
			length -= windowPosition.rightOffset;
			windowPosition.rightOffset -= windowPosition.rightOffset;
		}
		
		final SAMRecord record = recordWrapper.getSAMRecord();
		for (int j = 0; j < length; ++j) {
			final byte baseCallQuality = record.getBaseQualities()[readPosition + j];
			final int baseIndex = getBaseCallConfig().getBaseIndex(record.getReadBases()[readPosition + j]);
			coverage[windowPosition.i + j] += 1;
			baseCalls[windowPosition.i + j][baseIndex] += 1;
			baseCallQualities[windowPosition.i + j][baseIndex][baseCallQuality - conditionParameter.getMinBASQ()] += 1;
		}
	}
	
	@Override
	public void clear() {
		Arrays.fill(coverage, 0);
		Arrays.fill(referenceBases, (byte)'N');

		for (int[] b : baseCalls) {
			Arrays.fill(b, 0);	
		}
		for (int[][] bcs : baseCallQualities) {
			for (int[] b : bcs) {
				Arrays.fill(b, 0);	
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
		return getBaseCallConfig().getBases().length; 
	}
	
	private byte getMaxBaseCallQuality() {
		return getBaseCallConfig().getMaxBaseCallQuality();
	}
	
}
