package lib.data.cache.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.container.ReferenceSegment.TYPE;

public class ReferenceSegmentContainer {

	private final int activeWindowSize;
	
	private byte[] reference;
	private int[] winPos2id;
	private List<ReferenceSegment> segments;

	public ReferenceSegmentContainer(final int activeWindowSize) {
		this.activeWindowSize = activeWindowSize;
		
		reference = new byte[activeWindowSize];
		Arrays.fill(reference, (byte)'N');
		winPos2id = new int[activeWindowSize];

		int n = Math.max(10, activeWindowSize / 10);
		segments = new ArrayList<ReferenceSegment>(n);
		createUnknown();
	}

	public void clear() {
		segments.clear();
		// added to segments 
		createUnknown();
	}

	public ReferenceSegment get(final int windowPosition) {
		final int id = winPos2id[windowPosition];
		return segments.get(id);
	}
	
	public void markCovered(final int windowPosition, final int readPosition, final int length, 
			final SAMRecordWrapper recordWrapper) {

		final List<ReferenceSegment> segments = getSegments(windowPosition, length);

		int tmpWindowPosition = windowPosition;
		int tmpReadPosition = readPosition;
		int tmpLength = length;
		
		for (final ReferenceSegment segment : segments) {
			final int end = tmpWindowPosition + tmpLength;

			switch (segment.getType()) {
			
			case UNKNOWN:
				updateUnknown2Covered(segment, tmpWindowPosition, tmpReadPosition, tmpLength, recordWrapper);
				break;
			
			case NOT_COVERED:
				updateNotCovered2Covered(segment, tmpWindowPosition, tmpReadPosition, tmpLength, recordWrapper);
				break;
				
			case COVERED:
				break;
				
			default:
				break;
			}

			final int offset = Math.min(segment.getEnd(), end) - tmpWindowPosition;
			if (offset > 0) {
				tmpWindowPosition += offset;
				tmpReadPosition += offset;
				tmpLength -= offset;
			}
			
		}
	}
	
	private void updateUnknown2Covered(final ReferenceSegment unknown, 
			final int windowPosition, final int readPosition, final int length,
			final SAMRecordWrapper recordWrapper) {

		final int end = windowPosition + length;

		// create new or extends previous covered segment
		final ReferenceSegment previousCovered = getPreviousCovered(unknown);
		if (previousCovered != null) {
			// extend previous covered
			updateEnd(previousCovered, end, readPosition, recordWrapper);
		} else {
			// create uncovered
			createNotCovered(unknown.getStart(), windowPosition);
			// create covered
			createCovered(windowPosition, readPosition, length, recordWrapper);
		}

		// update unknown segment
		updateStart(unknown, end);
	}
	
	private void updateNotCovered2Covered(final ReferenceSegment notCovered, 
			final int windowPosition, final int readPosition, final int length,
			final SAMRecordWrapper recordWrapper) {

		final int end = windowPosition + length;
		
		// create new or extend previous covered segment
		final ReferenceSegment previousCovered = getPreviousCovered(notCovered);
		if (previousCovered != null) {
			// is the next segment covered too? 
			final ReferenceSegment nextCovered = getNextCovered(notCovered);
			if (nextCovered != null) {
				mergeCovered(previousCovered, nextCovered, readPosition, recordWrapper);
			} else {
				// extend previous covered segment
				updateEnd(previousCovered, end, readPosition, recordWrapper);
				// accordingly shrink notCovered
				updateStart(notCovered, end);
			}
		} else {
			// is the next segment covered? 
			final ReferenceSegment nextCovered = getNextCovered(notCovered);
			if (nextCovered != null) {
				// extend adjacent covered segment
				updateStart(nextCovered, windowPosition);
				// accordingly shrink notCovered 
				updateEnd(notCovered, windowPosition);
			} else {
				final int tmpNotCoveredEnd = notCovered.getEnd();
				
				// shrink not covered
				updateEnd(notCovered, windowPosition);
				// update to next created covered segment

				// create covered
				createCovered(windowPosition, end, readPosition, recordWrapper);
				// create notCovered
				createNotCovered(end, tmpNotCoveredEnd);
			}
		}
	}
	
	private void mergeCovered(final ReferenceSegment previous, final ReferenceSegment next, final int readPosition, final SAMRecordWrapper recordWrapper) {
		// pick biggest segment
		ReferenceSegment tmp = previous;
		if (next.getEnd() - next.getStart() > previous.getEnd() - previous.getStart()) {
			tmp = next;
			updateStart(tmp, previous.getStart(), readPosition, recordWrapper);
		} else {
			updateEnd(tmp, next.getEnd(), readPosition, recordWrapper); 
		}
	}
	
	public void markNotCovered(int windowPosition, int length, final int nextPosition) {
	final List<ReferenceSegment> segments = getSegments(windowPosition, length);
		
		for (final ReferenceSegment segment : segments) {
			final int end = windowPosition + length;
			
			switch (segment.getType()) {
			
			case UNKNOWN:
				updateUnknown2NotCovered(segment, windowPosition, length);
				break;
			
			case NOT_COVERED:
			case COVERED:
				// nothing to be done - already covered
				break;
				
			default:
				break;
			}
			
			final int offset = Math.min(segment.getEnd(), end) - windowPosition;
			if (offset > 0) {
				windowPosition += offset;
				length -= offset;
			}
			
		}
	}
	
	private void updateUnknown2NotCovered(final ReferenceSegment unknown, final int windowPosition, final int length) {
		assert windowPosition != unknown.getStart();
		
		final int end = windowPosition + length;

		// shrink unknown
		updateStart(unknown, windowPosition);
		// create not covered segment
		createNotCovered(windowPosition, end);
	}

	private List<ReferenceSegment> getSegments(final int windowPosition, final int length) {
 		final int end = windowPosition + length;
		final List<ReferenceSegment> segments = new ArrayList<ReferenceSegment>(3);
		ReferenceSegment segment = get(windowPosition);

		while (segment.getStart() < end) {
			segments.add(segment);
			if (segment.getEnd() < activeWindowSize) {
				segment = get(segment.getEnd());
			} else {
				break;
			}
		}

		return segments;
	}
	
	private void createUnknown() {
		final int start = 0;
		final int end = activeWindowSize;
		add(new ReferenceSegment(this, TYPE.UNKNOWN, start, end));
	}

	private void createCovered(final int windowStart, final int readStart, final int length, final SAMRecordWrapper recordWrapper) {
		add(new ReferenceSegment(this, TYPE.COVERED, windowStart, windowStart + length));
		setReference(windowStart, readStart, length, recordWrapper);
	}

	private void createNotCovered(final int start, final int end) {
		add(new ReferenceSegment(this, TYPE.NOT_COVERED, start, end));
	}
	
	private void updateStart(final ReferenceSegment segment, final int windowStart) {
		if (windowStart < segment.getStart()) {
			setId(segment.getId(), windowStart, segment.getStart());
		}
		segment.updateStart(windowStart);
	}
	
	private void updateStart(final ReferenceSegment segment, final int windowStart, final int readPosition, final SAMRecordWrapper recordWrapper) {
		if (windowStart < segment.getStart()) {
			setId(segment.getId(), windowStart, segment.getStart());
			final int length = segment.getStart() - windowStart;
			setReference(windowStart, readPosition, length, recordWrapper);
		}
		segment.updateStart(windowStart);
	}

	private void updateEnd(final ReferenceSegment segment, final int windowEnd) {
		if (windowEnd > segment.getEnd()) {
			setId(segment.getId(), segment.getEnd(), windowEnd);
		}
		segment.updateEnd(windowEnd);
	}
	
	private void updateEnd(final ReferenceSegment segment, final int windowEnd, final int readPosition, final SAMRecordWrapper recordWrapper) {
		if (windowEnd > segment.getEnd()) {
			setId(segment.getId(), segment.getEnd(), windowEnd);
			if (segment.getType() == TYPE.COVERED) {
				final int length = windowEnd - segment.getEnd(); 
				setReference(segment.getEnd(), readPosition, length, recordWrapper);
			}
		}
		segment.updateEnd(windowEnd);
	}
	
	private void add(final ReferenceSegment segment) {
		segments.add(segment);
		setId(segment.getId(), segment.getStart(), segment.getEnd());
	}
	
	private void setId(final int id, final int start, final int end) {
		Arrays.fill(winPos2id, start, end, id);
	}
	
	private void setReference(final int windowStart, final int readStart, final int length, SAMRecordWrapper recordWrapper) {
	}
	
	public int getNextId() {
		return segments.size();
	}
	
	private ReferenceSegment getPreviousCovered(final ReferenceSegment current) {
		if (current.getStart() <= 0) {
			return null;
		}
		final ReferenceSegment previous = get(current.getStart() - 1);
		return previous.getType() == TYPE.COVERED ? previous : null;
	}

	private ReferenceSegment getNextCovered(final ReferenceSegment current) {
		if (current.getEnd() >= activeWindowSize) {
			return null;
		}
		final ReferenceSegment next = get(current.getEnd());
		return next.getType() == TYPE.COVERED ? next : null;
	}

	public byte getReference(final int windowPosition) {
		return reference[windowPosition];
	}
	
}
