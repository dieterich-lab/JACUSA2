package lib.data.cache;

import java.util.Arrays;

import lib.method.AbstractMethodFactory;
import lib.util.Coordinate;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;

import lib.cli.parameters.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.builder.SAMRecordWrapper;
import lib.data.has.hasPileupCount;

public class PileupCallCache<T extends AbstractData & hasPileupCount> 
extends AbstractCache<T> {

	private final AbstractConditionParameter<T> conditionParameter;
	
	private final int[] coverage;
	private final int[][] baseCalls;
	
	private final int[][][] baseCallQualities;
	private final int baseCallQualityRange;
	
	public PileupCallCache(final AbstractConditionParameter<T> conditionParamter, final AbstractMethodFactory<T> methodFactory) {
		super(methodFactory);
		this.conditionParameter = conditionParamter;
	
		// how many bases will be considered
		final int baseSize = getBaseSize();
		
		// range of base call quality score 
		final byte maxBaseCallQuality = getMaxBaseCallQuality();
		final byte minBaseCallQuality = conditionParamter.getMinBASQ();
		baseCallQualityRange = maxBaseCallQuality - minBaseCallQuality + 1;

		coverage = new int[getActiveWindowSize()];
		baseCalls = new int[baseSize][getActiveWindowSize()];
		baseCallQualities = new int[baseSize][baseCallQualityRange][getActiveWindowSize()];
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		for (final AlignmentBlock block : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			int readPosition = block.getReadStart() - 1;
			addRecordWrapperRegion(readPosition, block.getLength(), recordWrapper);
		}
	}
	
	@Override
	public void addRecordWrapperPosition(final int readPosition, final SAMRecordWrapper recordWrapper) {
		addRecordWrapperRegion(readPosition, 1, recordWrapper);
	}
	
	@Override
	public void addRecordWrapperRegion(final int readPosition, final int length, final SAMRecordWrapper recordWrapper) {
		final int referencePosition = recordWrapper.getSAMRecord().getReferencePositionAtReadPosition(readPosition);
		final int windowPosition = Coordinate.makeRelativePosition(getActiveWindowCoordinates(), referencePosition);
		incrementBaseCalls(windowPosition, readPosition, length, recordWrapper);
	}
	
	@Override
	public T getData(final Coordinate coordinate) {
		final T data = getMethodFactory().createData();
		final int windowPosition = getWindowPosition(coordinate);
		// TODO copy content from cache to data
		return data;
	}

	protected void incrementBaseCalls(final int windowPosition, final int readPosition, final int length, 
			final SAMRecordWrapper recordWrapper) {

		final SAMRecord record = recordWrapper.getSAMRecord();
		for (int i = 0; i < length; ++i) {
			final byte baseCallQuality = record.getBaseQualities()[readPosition + i];
			final int baseIndex = getBaseCallConfig().getBaseIndex(record.getReadBases()[readPosition + i]);
			coverage[windowPosition] += 1;
			baseCalls[baseIndex][windowPosition] += 1;
			baseCallQualities[baseIndex][baseCallQuality - conditionParameter.getMinBASQ()][windowPosition] += 1;
		}
	}
	
	@Override
	public void clear() {
		Arrays.fill(coverage, 0);
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
