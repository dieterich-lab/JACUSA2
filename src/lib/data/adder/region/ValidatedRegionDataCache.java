package lib.data.adder.region;

import lib.util.Base;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController.WindowPositionGuard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import htsjdk.samtools.SAMRecord;
import lib.data.DataTypeContainer;
import lib.data.adder.AbstractDataContainerPopulator;
import lib.data.adder.IncrementAdder;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.container.SharedCache;
import lib.data.cache.region.RegionDataCache;
import lib.data.cache.region.isvalid.BaseCallValidator;

public class ValidatedRegionDataCache
extends AbstractDataContainerPopulator 
implements RegionDataCache {

	private final List<IncrementAdder> adder;
	private final List<BaseCallValidator> validator;

	public ValidatedRegionDataCache(final SharedCache sharedCache) {
		this(new ArrayList<IncrementAdder>(3), new ArrayList<BaseCallValidator>(3), sharedCache);
	}
	
	public ValidatedRegionDataCache(final List<IncrementAdder> adder,
			final SharedCache sharedCache) {
		this(adder, new ArrayList<BaseCallValidator>(3), sharedCache);
	}
	
	public ValidatedRegionDataCache(final List<IncrementAdder> adder,
			final List<BaseCallValidator> validator, 
			final SharedCache sharedCache) {

		super(sharedCache);
		this.adder 		= adder;
		this.validator 	= validator;
	}

	@Override
	public void addRegion(final int referencePosition, final int readPosition, final int length, 
			final SAMRecordWrapper recordWrapper) {
		
		if (referencePosition < 0) {
			throw new IllegalArgumentException("Reference Position cannot be < 0! -> outside of alignmentBlock");
		}

		final WindowPositionGuard windowPositionGuard = 
				getCoordinateController().convert(referencePosition, readPosition, length);
		
		if (windowPositionGuard.getWindowPosition() < 0 && windowPositionGuard.getLength() > 0) {
			throw new IllegalArgumentException("Window position cannot be < 0! -> outside of alignmentBlock");
		}

		final SAMRecord record = recordWrapper.getSAMRecord();
		for (int offset = 0; offset < windowPositionGuard.getLength(); ++offset) {
			final int tmpReferencePosition 	= windowPositionGuard.getReferencePosition() + offset;
			final int tmpWindowPosition 	= windowPositionGuard.getWindowPosition() + offset;
			final int tmpReadPosition		= windowPositionGuard.getReadPosition() + offset;

			final Base base = Base.valueOf(record.getReadBases()[tmpReadPosition]);
			final byte baseQuality = record.getBaseQualities()[tmpReadPosition];

			if (isValid(tmpReferencePosition, tmpWindowPosition, tmpReadPosition, 
					base, baseQuality, 
					record)) {
				increment(tmpReferencePosition, tmpWindowPosition, tmpReadPosition, 
						base, baseQuality, 
						record);
			}
		}
	}

	private boolean isValid(final int referencePosition, final int windowPosition, final int readPosition, 
			final Base base, final byte baseQuality,
			final SAMRecord record) {

		for (final BaseCallValidator validator : getValidator()) {
			if (! validator.isValid(referencePosition, windowPosition, readPosition, 
					base, baseQuality, 
					record)) {
				return false;
			}
		}

		return true;
	}

	private void increment(final int referencePosition, final int windowPosition, final int readPosition, 
			final Base base, final byte baseQuality,
			final SAMRecord record) {

		for (final IncrementAdder adder : getAdder()) {
			adder.increment(referencePosition, windowPosition, readPosition,
					base, baseQuality,
					record);
		}
	}

	@Override
	public void clear() {
		for (final IncrementAdder baseCallAdder : getAdder()) {
			baseCallAdder.clear();
		}
	}
	
	@Override
	public void populate(DataTypeContainer container, Coordinate coordinate) {
		for (final IncrementAdder adder : getAdder()) {
			adder.populate(container, coordinate);
		}
	}

	public void addHeadAdder(final IncrementAdder baseCallAdder) {
		adder.add(0, baseCallAdder);
	}
	
	public void addAdder(final IncrementAdder baseCallAdder) {
		adder.add(baseCallAdder);
	}
	
	public void addValidator(final BaseCallValidator baseCallValidator) {
		validator.add(baseCallValidator);
	}
	
	protected List<IncrementAdder> getAdder() {
		return Collections.unmodifiableList(adder);
	}
	
	protected List<BaseCallValidator> getValidator() {
		return Collections.unmodifiableList(validator);
	}
	
}
