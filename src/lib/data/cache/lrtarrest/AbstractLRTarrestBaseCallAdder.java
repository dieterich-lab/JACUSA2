package lib.data.cache.lrtarrest;

import lib.data.DataTypeContainer;
import lib.data.adder.AbstractDataContainerAdder;
import lib.data.adder.IncrementAdder;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.Fetcher;
import lib.util.Base;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.DynamicCoordinateTranslator;

public abstract class AbstractLRTarrestBaseCallAdder
extends AbstractDataContainerAdder 
implements IncrementAdder {

	private final Fetcher<ArrestPos2BaseCallCount> arrestPos2BaseCallCountExtractor;
	private final RefPos2ArrestPos2BaseCallCount ref2arrest2bc;
	
	public AbstractLRTarrestBaseCallAdder(
			final Fetcher<ArrestPos2BaseCallCount> arrestPos2BaseCallCountExtractor,
			final SharedCache sharedCache) {
		
		super(sharedCache);
		this.arrestPos2BaseCallCountExtractor = arrestPos2BaseCallCountExtractor;
		ref2arrest2bc = new RefPos2ArrestPos2BaseCallCount(
				new DynamicCoordinateTranslator(getCoordinateController()));
	}
	
	@Override
	public void populate(DataTypeContainer container, Coordinate coordinate) {
		final int refPos = coordinate.getPosition();
		if (! ref2arrest2bc.contains(refPos)) {
			return;
		}

		final ArrestPos2BaseCallCount dest = arrestPos2BaseCallCountExtractor.fetch(container);
		final ArrestPos2BaseCallCount src =  ref2arrest2bc.getArrestPos2BaseCallCount(refPos);
		dest.merge(src);
	}
	
	/*
	@Override
	public CoordinateController getCoordinateController() {
		return super.getCoordinateController();
	}
	*/
	
	protected void addBaseCall(final int refPos, final int arrestPos, final Base base) {
		ref2arrest2bc.addBaseCall(refPos, arrestPos, 
				base, getCoordinateController().getActive());
	}
	
	@Override
	public int getCoverage(int windowPosition) {
		final int refPos = 
				getCoordinateController().getCoordinateTranslator().convert2referencePosition(windowPosition);
		return ref2arrest2bc.getArrestPos2BaseCallCount(refPos).getTotalBaseCallCount().getCoverage();
	}
	
	@Override
	public void clear() {
		ref2arrest2bc.reset();
	}
	
}
