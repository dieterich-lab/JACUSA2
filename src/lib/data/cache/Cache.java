package lib.data.cache;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.util.coordinate.Coordinate;

public interface Cache<X extends AbstractData> {

	void addRecordWrapperPosition(final int readPosition, final SAMRecordWrapper recordWrapper);
	void addRecordWrapperRegion(final int readPosition, final int length, final SAMRecordWrapper recordWrapper);
	void addRecordWrapper(final SAMRecordWrapper recordWrapper);
	
	void clear();

	void addData(X data, final Coordinate coordinate);

	void setActiveWindowCoordinate(final Coordinate coordinate);
	Coordinate getActiveWindowCoordinate();
	
	public class WindowPosition {
		
		private int reference;
		private int read;
		private int length;

		private int window;
		
		private WindowPosition(final int reference, final int read, final int length, final int window) {
			this.reference = reference;
			this.read = read;
			this.length = length;
			this.window = window;
		}
		
		public static WindowPosition convert(final Coordinate windowCoordinate,
				int reference, int read, int length) {
			
			final int windowSize = windowCoordinate.getEnd() - windowCoordinate.getStart() + 1;
			
			int window = reference - windowCoordinate.getStart();

			if (window < 0) {
				length = Math.max(0, length + window);
				read += -window;
				reference += -window;
				window += -window;
			}

			if (length > 0) {
				final int offset = windowSize - (window + length);
				if (offset < 0) {
					length = Math.max(0, length + offset);
				}
			}

			return new WindowPosition(reference, read, length, window);
		}
		
		public int getReference() {
			return reference;
		}
		
		public int getLength() {
			return length;
		}
		
		public int getRead() {
			return read;
		}
		
		public int getWindowPosition() {
			return window;
		}
		
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append("ref=" + reference + " win=" + window + " read=" + read + " length=" + length);
			
			return sb.toString();
		}
		
	}
	
}
