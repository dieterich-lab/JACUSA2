package lib.data.storage.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TODO
 */
public class NextPositionSegmentContainer {

	enum TYPE {UNKNOWN, COVERED, NOT_COVERED}
	
	private final int activeWindowSize;
	
	private int[] winPos2id;
	private List<NextPositionSegment> segments;

	public NextPositionSegmentContainer(final int activeWindowSize) {
		this.activeWindowSize = activeWindowSize;
		
		winPos2id = new int[activeWindowSize];

		int n = Math.max(10, activeWindowSize / 10);
		segments = new ArrayList<NextPositionSegment>(n);
		createUnknown();
	}

	public void clear() {
		segments.clear();
		// added to segments 
		createUnknown();
	}

	public NextPositionSegment get(final int winPos) {
		final int id = winPos2id[winPos];
		return segments.get(id);
	}
	
	public void markCovered(final int winPos, final int length) {
		final List<NextPositionSegment> segments = getSegments(winPos, length);

		int tmpWindowPosition = winPos;
		int tmpLength = length;
		
		for (final NextPositionSegment segment : segments) {
			final int end = tmpWindowPosition + tmpLength;

			switch (segment.getType()) {
			
			case UNKNOWN:
				updateUnknown2Covered(segment, tmpWindowPosition, tmpLength);
				break;
			
			case NOT_COVERED:
				updateNotCovered2Covered(segment, tmpWindowPosition, tmpLength);
				break;
				
			case COVERED:
				break;
				
			default:
				break;
			}

			final int offset = Math.min(segment.getEnd(), end) - tmpWindowPosition;
			if (offset > 0) {
				tmpWindowPosition += offset;
				tmpLength -= offset;
			}
			
		}
	}
	
	private void updateUnknown2Covered(final NextPositionSegment unknown, final int winPos, final int length) {
		final int end = winPos + length;

		// create new or extends previous covered segment
		final NextPositionSegment previousCovered = getPreviousCovered(unknown);
		if (previousCovered != null) {
			// extend previous covered
			updateEnd(previousCovered, end);
		} else {
			// create uncovered
			createNotCovered(unknown.getStart(), winPos, winPos);
			// create covered
			createCovered(winPos, end, -1);
		}

		// update unknown segment
		updateStart(unknown, end);
	}
	
	private void updateNotCovered2Covered(final NextPositionSegment notCovered, final int winPos, final int length) {
		final int end = winPos + length;
		
		// create new or extend previous covered segment
		final NextPositionSegment previousCovered = getPreviousCovered(notCovered);
		if (previousCovered != null) {
			// is the next segment covered too? 
			final NextPositionSegment nextCovered = getNextCovered(notCovered);
			if (nextCovered != null) {
				mergeCovered(previousCovered, nextCovered);
			} else {
				// extend previous covered segment
				updateEnd(previousCovered, end);
				// accordingly shrink notCovered
				updateStart(notCovered, end);
			}
		} else {
			// is the next segment covered? 
			final NextPositionSegment nextCovered = getNextCovered(notCovered);
			if (nextCovered != null) {
				// extend adjacent covered segment
				updateStart(nextCovered, winPos);
				// accordingly shrink notCovered 
				updateEnd(notCovered, winPos);
				// update next of notCovered
				notCovered.updateNext(winPos);
			} else {
				final int tmpNotCoveredEnd = notCovered.getEnd();
				
				// store next
				final int nextPosition = notCovered.getNext();
				
				// shrink not covered
				updateEnd(notCovered, winPos);
				// update to next created covered segment
				notCovered.updateNext(winPos);

				// create covered
				createCovered(winPos, end, nextPosition);

				// create notCovered
				// if (end < tmpNotCoveredEnd) {
				createNotCovered(end, tmpNotCoveredEnd, nextPosition);
				//}
			}
		}
	}
	
	private void mergeCovered(final NextPositionSegment previous, final NextPositionSegment next) {
		// pick biggest segment
		NextPositionSegment tmp = previous;
		if (next.getEnd() - next.getStart() > previous.getEnd() - previous.getStart()) {
			tmp = next;
			updateStart(tmp, previous.getStart());
		} else {
			updateEnd(tmp, next.getEnd()); 
		}
		tmp.updateNext(next.getNext());
	}
	
	public void markNotCovered(int winPos, int length, final int nextPosition) {
		final List<NextPositionSegment> segments = getSegments(winPos, length);
		
		for (final NextPositionSegment segment : segments) {
			final int end = winPos + length;
			
			switch (segment.getType()) {
			
			case UNKNOWN:
				updateUnknown2NotCovered(segment, winPos, length, nextPosition);
				break;
			
			case NOT_COVERED:
			case COVERED:
				// nothing to be done - already covered
				break;
				
			default:
				break;
			}
			
			final int offset = Math.min(segment.getEnd(), end) - winPos;
			if (offset > 0) {
				winPos += offset;
				length -= offset;
			}
			
		}
	}
	
	private void updateUnknown2NotCovered(final NextPositionSegment unknown, final int winPos, final int length, final int nextPosition) {
		assert winPos != unknown.getStart();
		
		final int end = winPos + length;

		// shrink unknown
		updateStart(unknown, winPos);
		// create not covered segment
		createNotCovered(winPos, end, nextPosition);
	}

	private List<NextPositionSegment> getSegments(final int winPos, final int length) {
 		final int end = winPos + length;
		final List<NextPositionSegment> segments = new ArrayList<NextPositionSegment>(3);
		NextPositionSegment segment = get(winPos);

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
		add(new NextPositionSegment(this, TYPE.UNKNOWN, start, end));
	}

	private void createCovered(final int start, final int end, final int nextPosition) {
		add(new NextPositionSegment(this, TYPE.COVERED, start, end, nextPosition));
	}

	private void createNotCovered(final int start, final int end, final int nextPosition) {
		add(new NextPositionSegment(this, TYPE.NOT_COVERED, start, end, nextPosition));
	}
	
	private void updateStart(final NextPositionSegment segment, final int start) {
		if (start < segment.getStart()) {
			setId(segment.getId(), start, segment.getStart()); 
		}
		segment.updateStart(start);
	}

	private void updateEnd(final NextPositionSegment segment, final int end) {
		if (end > segment.getEnd()) {
			setId(segment.getId(), segment.getEnd(), end);
		}
		segment.updateEnd(end);
	}
	
	private void add(final NextPositionSegment segment) {
		segments.add(segment);
		setId(segment.getId(), segment.getStart(), segment.getEnd());
	}
	
	private void setId(final int id, final int start, final int end) {
		Arrays.fill(winPos2id, start, end, id);
	}
	
	public int getNextId() {
		return segments.size();
	}
	
	private NextPositionSegment getPreviousCovered(final NextPositionSegment current) {
		if (current.getStart() <= 0) {
			return null;
		}
		final NextPositionSegment previous = get(current.getStart() - 1);
		return previous.getType() == TYPE.COVERED ? previous : null;
	}

	private NextPositionSegment getNextCovered(final NextPositionSegment current) {
		if (current.getEnd() >= activeWindowSize) {
			return null;
		}
		final NextPositionSegment next = get(current.getEnd());
		return next.getType() == TYPE.COVERED ? next : null;
	}

}
