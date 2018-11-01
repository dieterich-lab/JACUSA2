package lib.util.coordinate;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import lib.cli.parameter.AbstractConditionParameter;
import lib.data.assembler.ConditionContainer;
import lib.data.has.LibraryType;
import lib.location.CoordinateAdvancer;
import lib.location.StrandedJumpingCoordinateAdvancer;
import lib.location.UnstrandedJumpingCoordinateAdvancer;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.util.coordinate.provider.WindowedCoordinateProviderStatic;

public class CoordinateController {

	private final int activeWindowSize;
	private CoordinateAdvancer coordinateAdvancer;

	private CoordinateTranslator coordinateTranslator;
	
	private Coordinate reserved;
	private WindowedCoordinateProviderStatic provider;
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
		for (final AbstractConditionParameter conditionParameter : conditionContainer.getConditionParameter()) {
			if (conditionParameter.getLibraryType() != LibraryType.UNSTRANDED) {
				return new StrandedJumpingCoordinateAdvancer(this, conditionContainer);
			}
		}

		return new UnstrandedJumpingCoordinateAdvancer(this, conditionContainer);
	}
	
	public void updateReserved(final Coordinate reservedWindowCoordinate) {
		active = null;
		reserved = reservedWindowCoordinate;
		provider = new WindowedCoordinateProviderStatic(reservedWindowCoordinate.getStrand() != STRAND.UNKNOWN, 
				reservedWindowCoordinate, activeWindowSize);

		coordinateAdvancer.getCurrentCoordinate().setContig(reservedWindowCoordinate.getContig());
		coordinateAdvancer.getCurrentCoordinate().setPosition(-1);
		coordinateAdvancer.getCurrentCoordinate().setStrand(reservedWindowCoordinate.getStrand());
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
		coordinateAdvancer.adjust(coordinate);
	}
	
	public boolean checkCoordinateWithinActiveWindow(final Coordinate coordinate) {
		final int position = coordinate.getPosition();
		return position >= active.getStart() && position <= active.getEnd(); 		
	}
	
	public boolean checkCoordinateAdvancerWithinActiveWindow() {
		return checkCoordinateWithinActiveWindow(coordinateAdvancer.getCurrentCoordinate());
		
	}
	
	public boolean chechWindowPositionWithinActiveWindow(final int windowPosition) {
		return CoordinateUtil.makeRelativePosition(active, windowPosition) >= 0;
	}

	public CoordinateTranslator getCoordinateTranslator() {
		return coordinateTranslator;
	}
	
	public Entry<Integer, STRAND> getStrandedWindowPosition(final Coordinate coordinate) {
		final int windowPosition = coordinateTranslator.convert2windowPosition(coordinate);
		return new SimpleEntry<Integer, STRAND>(windowPosition, coordinate.getStrand());
	}

	public WindowPositionGuard convert(int referencePosition, int length) {
		WindowPositionGuard tmp = convert(referencePosition, -1 , length);
		tmp.readPosition = -1;
		return tmp;
	}
	
	public WindowPositionGuard convert(int referencePosition, int readPosition, int length) {
		int windowPosition = referencePosition - active.getStart();

		if (windowPosition < 0) {
			length 				= Math.max(0, length + windowPosition);
			readPosition 		+= -windowPosition;
			referencePosition 	+= -windowPosition;
			windowPosition 		+= -windowPosition;
		}

		final int offset = activeWindowSize - (windowPosition + length);
		if (offset < 0) {
			length = Math.max(0, length + offset);
		}

		if (windowPosition < 0 || windowPosition >= activeWindowSize) {
			windowPosition = -1;
		}

		return new WindowPositionGuard(referencePosition, windowPosition, readPosition, length);
	}		
	
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
		sb.append("active: ");
		sb.append(active);
		
		return sb.toString();
	}
	
	public class WindowPositionGuard {
		
		private int referencePosition;
		private int readPosition;
		private int length;

		private int windowPosition;

		protected WindowPositionGuard(final int referencePosition, final int windowPosition, final int length) {
			this(referencePosition, windowPosition, -1, length);
		}
		
		protected WindowPositionGuard(final int referencePosition, final int windowPosition, final int readPosition, 
				final int length) {

			this.referencePosition 	= referencePosition;
			this.readPosition 		= readPosition;
			this.length 			= length;
			this.windowPosition 	= windowPosition;
		}

		public WindowPositionGuard transform(final int offset, final int length) {
			return convert(referencePosition + offset, readPosition + offset, length);
		}
		
		public int getReferencePosition() {
			return referencePosition;
		}
		
		public int getLength() {
			return length;
		}
		
		public int getReadPosition() {
			return readPosition;
		}
		
		public int getWindowPosition() {
			return windowPosition;
		}
		
		public int getWindowEnd() {
			return windowPosition + length;
		}
		
		public int getReadEnd() {
			return readPosition + length;
		}
		
		public int getReferenceEnd() {
			return referencePosition + length;
		}
	
		public boolean isValid() {
			return windowPosition >= 0 && length > 0;
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append("ref=" + referencePosition + 
					" win=" + windowPosition + 
					" read=" + readPosition + 
					" length=" + length);
			return sb.toString();
		}
	}
	
}
