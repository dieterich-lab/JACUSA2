package lib.util.coordinate;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.builder.ConditionContainer;
import lib.data.cache.container.ComplexGeneralCache;
import lib.data.cache.container.FileReferenceProvider;
import lib.data.cache.container.GeneralCache;
import lib.data.cache.container.ReferenceProvider;
import lib.data.cache.container.SimpleGeneralCache;
import lib.data.cache.container.SimpleMDReferenceProvider;
import lib.data.has.HasLibraryType.LIBRARY_TYPE;
import lib.location.CoordinateAdvancer;
import lib.location.StrandedJumpingCoordinateAdvancer;
import lib.location.UnstrandedJumpingCoordinateAdvancer;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.util.coordinate.provider.WindowedCoordinateProvider;

public class CoordinateController {

	private final int activeWindowSize;
	private final CoordinateAdvancer coordinateAdvancer;
	
	private final AbstractParameter<?, ?> parameter;
	
	private Coordinate reserved;
	private WindowedCoordinateProvider provider;
	private Coordinate active;

	private ReferenceProvider referenceProvider;
	
	public CoordinateController(final ConditionContainer<?> conditionContainer) {
		this.activeWindowSize = conditionContainer.getParameter().getActiveWindowSize();
		coordinateAdvancer = createCoordinateAdvancer(conditionContainer);

		parameter = conditionContainer.getParameter();
	}

	private CoordinateAdvancer createCoordinateAdvancer(final ConditionContainer<?> conditionContainer) {
		final Coordinate coordinate = new Coordinate();

		for (final AbstractConditionParameter<?> conditionParameter : conditionContainer.getConditionParameter()) {
			if (conditionParameter.getLibraryType() != LIBRARY_TYPE.UNSTRANDED) {
				coordinate.setStrand(STRAND.FORWARD);
				return new StrandedJumpingCoordinateAdvancer(this, conditionContainer);
			}
		}

		return new UnstrandedJumpingCoordinateAdvancer(this, conditionContainer);
	}
	
	public void updateReserved(final Coordinate reservedWindowCoordinate) {
		active = null;
		reserved = reservedWindowCoordinate;
		provider = new WindowedCoordinateProvider(reservedWindowCoordinate.getStrand() != STRAND.UNKNOWN, 
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
		coordinateAdvancer.getCurrentCoordinate().setContig(coordinate.getContig());
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
		return Coordinate.makeRelativePosition(active, windowPosition) >= 0;
	}

	public int convert2windowPosition(final Coordinate coordinate) {
		return convert2windowPosition(coordinate.getPosition());
	}
	
	public int convert2windowPosition(final int referencePosition) {
		return Coordinate.makeRelativePosition(active, referencePosition);
	}

	public int convert2referencePosition(final int windowPosition) {
		return active.getStart() + windowPosition;
	}

	public Entry<Integer, STRAND> getStrandedWindowPosition(final Coordinate coordinate) {
		final int windowPosition = convert2windowPosition(coordinate);
		return new SimpleEntry<Integer, STRAND>(windowPosition, coordinate.getStrand());
	}

	public GeneralCache getGeneralCache() {
		if (parameter.getReferenceFile() == null) {
			return new ComplexGeneralCache(getReferenceProvider(), this);
		} 

		return new SimpleGeneralCache(getReferenceProvider(), this);
	}
	
	public ReferenceProvider getReferenceProvider() {
		if (referenceProvider != null) {
			return referenceProvider;
		}
		
		if (parameter.getReferenceFile() == null) {
			referenceProvider = new SimpleMDReferenceProvider(this);
		} else {
			referenceProvider = new FileReferenceProvider(parameter.getReferenceFile(), this);
		}
		
		return referenceProvider;
	}
	
	
	public WindowPositionGuard convert(int referencePosition, int length) {
		int windowPosition = referencePosition - active.getStart();

		if (windowPosition < 0) {
			length = Math.max(0, length + windowPosition);
			referencePosition += -windowPosition;
			windowPosition += -windowPosition;
		}

		final int offset = activeWindowSize - (windowPosition + length);
		if (offset < 0) {
			length = Math.max(0, length + offset);
		}

		if (windowPosition < 0 || windowPosition > activeWindowSize) {
			windowPosition = -1;
		}

		return new WindowPositionGuard(referencePosition, windowPosition, length);
	}
	
	public WindowPositionGuard convert(int referencePosition, int readPosition, int length) {
		int windowPosition = referencePosition - active.getStart();

		if (windowPosition < 0) {
			length = Math.max(0, length + windowPosition);
			readPosition += -windowPosition;
			referencePosition += -windowPosition;
			windowPosition += -windowPosition;
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

			this.referencePosition = referencePosition;
			this.readPosition = readPosition;
			this.length = length;
			this.windowPosition = windowPosition;
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
		
		public int getWindowEndPosition() {
			return windowPosition + length;
		}
		
		public int getReadEndPosition() {
			return readPosition + length;
		}
		
		public int getReferenceEndPosition() {
			return referencePosition + length;
		}
		
		public boolean isValid() {
			return windowPosition >= 0 && length > 0;
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append("ref=" + referencePosition + " win=" + windowPosition + " read=" + readPosition + " length=" + length);
			
			return sb.toString();
		}
	}
	
}
