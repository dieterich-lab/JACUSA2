package lib.util.coordinate;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import lib.cli.parameter.ConditionParameter;
import lib.util.ConditionContainer;
import lib.util.LibraryType;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.util.coordinate.advancer.CoordinateAdvancer;
import lib.util.coordinate.advancer.StrandedJumpingCoordinateAdvancer;
import lib.util.coordinate.advancer.UnstrandedJumpingCoordinateAdvancer;
import lib.util.coordinate.provider.WindowedCoordinateStaticProvider;

public class CoordinateController {

	private final int activeWindowSize;
	private CoordinateAdvancer coordinateAdvancer;

	private CoordinateTranslator coordinateTranslator;
	
	private Coordinate reserved;
	private WindowedCoordinateStaticProvider provider;
	private Coordinate active;
	
	private CoordinateController(final int activeWindowSize) {
		this.active = null;
		this.activeWindowSize = activeWindowSize;
	}
	
	public CoordinateController(final int activeWindowSize, final CoordinateAdvancer coordinateAdvancer) {
		this(activeWindowSize);
		this.coordinateAdvancer = coordinateAdvancer;
	}
	
	public CoordinateController(final int activeWindowSize, final ConditionContainer conditionContainer) {
		this(activeWindowSize);
		coordinateAdvancer = createCoordinateAdvancer(conditionContainer);
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
				reservedWindowCoordinate, activeWindowSize);

		
		coordinateAdvancer.getCurrentCoordinate().resetPosition(reservedWindowCoordinate);
	}

	public boolean hasNext() {
		return provider.hasNext();
	}
	
	public Coordinate next() {
		if (! hasNext()) {
			return null;
		}

		active = provider.next();
		coordinateTranslator = new DynamicCoordinateTranslator(this);
		updateCoordinateAdvancer(active);
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
		if (! checkCoordinateAdvancerWithinActiveWindow()) {
			return false;
		}
		
		coordinateAdvancer.advance();
		return true;
	}

	public CoordinateAdvancer getCoordinateAdvancer() {
		return coordinateAdvancer;
	}

	public int getActiveWindowSize() {
		return activeWindowSize;
	}

	private void updateCoordinateAdvancer(final Coordinate coordinate) {
		coordinateAdvancer.adjustPosition(coordinate);
	}
	
	public boolean checkCoordinateWithinActiveWindow(final Coordinate coordinate) {
		return active.overlaps(coordinate);
		
		// old code
		// final int position = coordinate.getPosition();
		// return position >= active.getStart() && position <= active.getEnd(); 		
	}
	
	public boolean checkCoordinateAdvancerWithinActiveWindow() {
		return checkCoordinateWithinActiveWindow(coordinateAdvancer.getCurrentCoordinate());
	}
	
	public boolean chechReferencePositionWithinActiveWindow(final int refPos) {
		return CoordinateUtil.makeRelativePosition(active, refPos) >= 0;
	}
	
	public boolean chechWindowPositionWithinActiveWindow(final int winPos) {
		return CoordinateUtil.makeRelativePosition(active, active.getStart() + winPos) >= 0;
	}

	public CoordinateTranslator getCoordinateTranslator() {
		return coordinateTranslator;
	}
	
	public Entry<Integer, STRAND> getStrandedWindowPosition(final Coordinate coordinate) {
		final int winPos = coordinateTranslator.coordinate2windowPosition(coordinate);
		return new SimpleEntry<Integer, STRAND>(winPos, coordinate.getStrand());
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

		final int offset = activeWindowSize - (winPos + length);
		if (offset < 0) {
			length = Math.max(0, length + offset);
		}

		if (winPos < 0 || winPos >= activeWindowSize) {
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

		protected WindowPositionGuard(final int refPos, final int winPos, final int length) {
			this(refPos, winPos, -1, length);
		}
		
		protected WindowPositionGuard(final int refPos, final int winPos, final int readPos, 
				final int length) {

			this.refPos 	= refPos;
			this.readPos 	= readPos;
			this.length 	= length;
			this.winPos 	= winPos;
		}

		public WindowPositionGuard transform(final int offset, final int length) {
			return convert(refPos + offset, readPos + offset, length);
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
