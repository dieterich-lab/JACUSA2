package lib.util.coordinate;

import lib.cli.parameter.ConditionParameter;
import lib.util.ConditionContainer;
import lib.util.LibraryType;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.util.coordinate.advancer.CoordinateAdvancer;
import lib.util.coordinate.advancer.StrandedJumpingCoordinateAdvancer;
import lib.util.coordinate.advancer.UnstrandedJumpingCoordinateAdvancer;
import lib.util.coordinate.provider.WindowedCoordinateStaticProvider;

/**
 * TODO
 */
public class CoordinateController {

	private final int activeWinSize;
	private CoordinateAdvancer coordAdvancer;

	private CoordinateTranslator coordTrans;
	
	private Coordinate reserved;
	private WindowedCoordinateStaticProvider provider;
	private Coordinate active;
	
	private CoordinateController(final int activeWindowSize) {
		this.active = null;
		this.activeWinSize = activeWindowSize;
		coordTrans = new DynamicCoordinateTranslator(this);
	}
	
	public CoordinateController(final int activeWindowSize, final CoordinateAdvancer coordinateAdvancer) {
		this(activeWindowSize);
		this.coordAdvancer = coordinateAdvancer;
	}
	
	public CoordinateController(final int activeWindowSize, final ConditionContainer conditionContainer) {
		this(activeWindowSize);
		coordAdvancer = createCoordinateAdvancer(conditionContainer);
	}
	
	private CoordinateAdvancer createCoordinateAdvancer(
			final ConditionContainer conditionContainer) {
		for (final ConditionParameter conditionParameter : conditionContainer.getConditionParameter()) {
			if (conditionParameter.getLibraryType() != LibraryType.UNSTRANDED) {
				return new StrandedJumpingCoordinateAdvancer(this, conditionContainer);
			}
		}

		return new UnstrandedJumpingCoordinateAdvancer(this, conditionContainer);
	}
	
	public void updateReserved(final Coordinate reservedWindowCoordinate) {
		active 		= null;
		reserved 	= reservedWindowCoordinate;
		provider 	= new WindowedCoordinateStaticProvider(
				reservedWindowCoordinate.getStrand() != STRAND.UNKNOWN, 
				reservedWindowCoordinate, activeWinSize);

		
		coordAdvancer.getCurrentCoordinate().resetPosition(reservedWindowCoordinate);
	}

	public boolean hasNext() {
		return provider.hasNext();
	}
	
	public Coordinate next() {
		if (! hasNext()) {
			return null;
		}

		active = provider.next();
		updateCoordinateAdvancer(active);
		return active;
	}

	// don't use only for JUNIT TODO remove
	public void helperSetActive(final Coordinate active) {
		this.active = active;
		updateCoordinateAdvancer(active);
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
		if (! checkCoordAdvancerWithinActiveWindow()) {
			return false;
		}
		
		coordAdvancer.advance();
		return true;
	}

	public CoordinateAdvancer getCoordAdvancer() {
		return coordAdvancer;
	}

	public int getActiveWindowSize() {
		return activeWinSize;
	}

	private void updateCoordinateAdvancer(final Coordinate coordinate) {
		coordAdvancer.adjustPosition(coordinate);
	}
	
	public boolean checkCoordinateWithinActiveWindow(final Coordinate coordinate) {
		return active.overlaps(coordinate);
	}
	
	public boolean checkCoordAdvancerWithinActiveWindow() {
		return checkCoordinateWithinActiveWindow(coordAdvancer.getCurrentCoordinate());
	}

	public CoordinateTranslator getCoordinateTranslator() {
		return coordTrans;
	}

	public WindowPositionGuard convert(int refPos, int length) {
		WindowPositionGuard tmp = convert(refPos, -1 , length);
		tmp.readPos = -1;
		return tmp;
	}
	
	public WindowPositionGuard convert(int refPos, int readPos, int length) {
		int winPos = refPos - active.getStart();

		if (winPos < 0) {
			length 	= Math.max(0, length + winPos);
			readPos += -winPos;
			refPos 	+= -winPos;
			winPos 	+= -winPos;
		}

		final int offset = activeWinSize - (winPos + length);
		if (offset < 0) {
			length = Math.max(0, length + offset);
		}

		if (winPos < 0 || winPos >= activeWinSize) {
			winPos = -1;
		}

		return new WindowPositionGuard(refPos, winPos, readPos, length);
	}		
	
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
		sb.append("active: ");
		sb.append(active);
		
		return sb.toString();
	}
	
	public class WindowPositionGuard {
		
		private int refPos;
		private int readPos;
		private int length;

		private int winPos;
		
		protected WindowPositionGuard(final int refPos, final int winPos, final int readPos, 
				final int length) {

			this.refPos 	= refPos;
			this.readPos 	= readPos;
			this.length 	= length;
			this.winPos 	= winPos;
		}
		
		public int getReferencePosition() {
			return refPos;
		}
		
		public int getLength() {
			return length;
		}
		
		public int getReadPosition() {
			return readPos;
		}
		
		public int getWindowPosition() {
			return winPos;
		}
		
		public int getWindowEnd() {
			return winPos + length;
		}
		
		public int getReadEnd() {
			return readPos + length;
		}
		
		public int getReferenceEnd() {
			return refPos + length;
		}
	
		public boolean isValid() {
			return winPos >= 0 && length > 0;
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append("ref=" + refPos + 
					" win=" + winPos + 
					" read=" + readPos + 
					" length=" + length);
			return sb.toString();
		}
	}
	
}
