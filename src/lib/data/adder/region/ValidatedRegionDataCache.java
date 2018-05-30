package lib.data.adder.region;

import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateController.WindowPositionGuard;

import java.util.ArrayList;
import java.util.List;

import htsjdk.samtools.SAMRecord;
import lib.cli.options.Base;
import lib.data.AbstractData;
import lib.data.adder.AbstractDataAdder;
import lib.data.adder.IncrementAdder;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.region.RegionDataCache;
import lib.data.cache.region.isvalid.BaseCallValidator;

public class ValidatedRegionDataCache<T extends AbstractData>
extends AbstractDataAdder<T> 
implements RegionDataCache<T> {

	private final List<IncrementAdder<T>> adder;
	private final List<BaseCallValidator> validator;

	public ValidatedRegionDataCache(final CoordinateController coordinateController) {
		this(new ArrayList<IncrementAdder<T>>(3), new ArrayList<BaseCallValidator>(3), coordinateController);
	}
	
	public ValidatedRegionDataCache(final List<IncrementAdder<T>> adder,
			final CoordinateController coordinateController) {
		this(adder, new ArrayList<BaseCallValidator>(3), coordinateController);
	}
	
	public ValidatedRegionDataCache(final List<IncrementAdder<T>> adder,
			final List<BaseCallValidator> validator, 
			final CoordinateController coordinateController) {

		super(coordinateController);
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

			final Base base 		= Base.valueOf(record.getReadBases()[tmpReadPosition]);
			final byte baseQuality 	= record.getBaseQualities()[tmpReadPosition];

// System.out.println(tmpReferencePosition + " " + tmpWindowPosition + " " + tmpReadPosition + " " + (char)base.getC());

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

		for (final IncrementAdder<T> adder : getAdder()) {
			adder.increment(referencePosition, windowPosition, readPosition,
					base, baseQuality,
					record);
		}
	}

	@Override
	public void clear() {
		for (final IncrementAdder<T> baseCallAdder : getAdder()) {
			baseCallAdder.clear();
		}
	}
	
	@Override
	public void addData(final T data, final Coordinate coordinate) {
		for (final IncrementAdder<T> adder : getAdder()) {
			adder.addData(data, coordinate);
		}
	}

	public void addVIPAdder(final IncrementAdder<T> baseCallAdder) {
		getAdder().add(0, baseCallAdder);
	}
	
	public void addAdder(final IncrementAdder<T> baseCallAdder) {
		getAdder().add(baseCallAdder);
	}
	
	public void addValidator(final BaseCallValidator baseCallValidator) {
		getValidator().add(baseCallValidator);
	}
	
	protected List<IncrementAdder<T>> getAdder() {
		return adder;
	}
	
	protected List<BaseCallValidator> getValidator() {
		return validator;
	}
	
}
