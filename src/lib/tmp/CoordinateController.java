package lib.tmp;

import lib.location.CoordinateAdvancer;
import lib.util.Coordinate;
import lib.util.coordinateprovider.WindowedCoordinateProvider;

public class CoordinateController {

	private final int activeWindowSize;
	private final CoordinateAdvancer referenceAdvancer;
	
	private Coordinate reserved;
	private WindowedCoordinateProvider provider;
	private Coordinate active;
	
	public CoordinateController(final int windowActiveSite, final CoordinateAdvancer referenceAdvancer) {
		this.activeWindowSize = windowActiveSite;
		this.referenceAdvancer = referenceAdvancer;
	}
	
	public void updateReserved(final Coordinate reservedWindowCoordinate) {
		active = null;
		reserved = reservedWindowCoordinate;
		provider = new WindowedCoordinateProvider(reservedWindowCoordinate, activeWindowSize);

		if (hasNext()) {
			updateReferenceAdvance(next());
		} else {
			updateReferenceAdvance(new Coordinate());
		}
	}
	
	public boolean hasNext() {
		return provider.hasNext();
	}
	
	public Coordinate next() {
		active = provider.next();
		updateReferenceAdvance(active);
		return active;
	}
	
	public Coordinate getActive() {
		return active;
	}
	
	public Coordinate getReserved() {
		return reserved;
	}

	public boolean isInner() {
		return ! (isLeft() || isRight());
	}
	
	public boolean isLeft() {
		return active.getStart() == reserved.getStart();
	}
	
	public boolean isRight() {
		return active.getEnd() == reserved.getEnd();
	}

	public boolean advance() {
		if (! checkReferenceAdvancerWithinActiveWindow()) {
			return false;
		}
		
		referenceAdvancer.advance();
		return true;
	}

	public CoordinateAdvancer getReferenceAdvance() {
		return referenceAdvancer;
	}
	
	public boolean checkReferenceAdvancerWithinActiveWindow() {
		final int position = referenceAdvancer.getCurrentCoordinate().getPosition();
		return position >= active.getStart() && position <= active.getEnd(); 		
	}
	
	private void updateReferenceAdvance(final Coordinate coordinate) {
		referenceAdvancer.adjust(coordinate);
	}
	
}
