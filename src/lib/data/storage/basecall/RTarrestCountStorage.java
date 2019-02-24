package lib.data.storage.basecall;

import lib.util.coordinate.Coordinate;
import lib.util.position.Position;

import lib.data.DataContainer;
import lib.data.storage.AbstractStorage;
import lib.data.storage.WindowCoverage;
import lib.data.storage.arrest.LocationInterpreter;
import lib.data.storage.container.SharedStorage;

public class RTarrestCountStorage
extends AbstractStorage 
implements WindowCoverage {

	private final LocationInterpreter locInterpreter;
	
	private final AbstractBaseCallCountStorage arrestBccStorage;
	private final AbstractBaseCallCountStorage throughBccStorage;
	
	public RTarrestCountStorage(
			final SharedStorage sharedStorage,
			final LocationInterpreter locationInterpreter,
			final AbstractBaseCallCountStorage arrestBccStorage,
			final AbstractBaseCallCountStorage throughBccStorage) {

		super(sharedStorage);
		locInterpreter 			= locationInterpreter;
		this.arrestBccStorage 	= arrestBccStorage;
		this.throughBccStorage 	= throughBccStorage;
	}
	
	@Override
	public void populate(DataContainer container, int winPos, Coordinate coordinate) {
		arrestBccStorage.populate(container, winPos, coordinate);
		throughBccStorage.populate(container, winPos, coordinate);
	}
	
	@Override
	public void increment(Position pos) {
		if (locInterpreter.isArrest(
				pos.getReadPosition(),
				pos.getRecordExtended(), 
				getCoordinateController().getCoordinateTranslator())) {
			arrestBccStorage.increment(pos);
		} else {
			throughBccStorage.increment(pos);
		}
	}
	
	@Override
	public void clear() {
		arrestBccStorage.clear();
		throughBccStorage.clear();
	}

	@Override
	public int getCoverage(final int winPos) {
		return arrestBccStorage.getCoverage(winPos) + throughBccStorage.getCoverage(winPos);
	}

}
