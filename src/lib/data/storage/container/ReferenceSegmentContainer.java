package lib.data.storage.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lib.data.storage.container.ReferenceSegment.TYPE;
import lib.recordextended.SAMRecordExtended;

// FIXME segments not working
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

	public ReferenceSegment get(final int winPos) {
		final int id = winPos2id[winPos];
		return segments.get(id);
	}

	public void markCovered(final int winPos, final int readPos, final int length, 
			final SAMRecordExtended recordExtended) {

		final List<ReferenceSegment> segments = getSegments(winPos, length);

		int tmpWindowPosition 	= winPos;
		int tmpReadPosition 	= readPos;
		int tmpLength 			= length;
		
		for (final ReferenceSegment segment : segments) {
			final int end = tmpWindowPosition + tmpLength;

			switch (segment.getType()) {
			
			case UNKNOWN:
				updateUnknown2Covered(segment, tmpWindowPosition, tmpReadPosition, tmpLength, recordExtended);
				break;
			
			case NOT_COVERED:
				updateNotCovered2Covered(segment, tmpWindowPosition, tmpReadPosition, tmpLength, recordExtended);
				break;
				
			case COVERED:
				break;
				
			default:
				break;
			}

			final int offset = Math.min(segment.getEnd(), end) - tmpWindowPosition;
			if (offset > 0) {
				tmpWindowPosition 	+= offset;
				tmpReadPosition 	+= offset;
				tmpLength 			-= offset;
			}
			
		}
	}
	
	private void updateUnknown2Covered(final ReferenceSegment unknown, 
			final int winPos, final int readPos, final int length,
			final SAMRecordExtended recordExtended) {

		final int end = winPos + length;

		// create new or extends previous covered segment
		final ReferenceSegment previousCovered = getPreviousCovered(winPos, end);
		if (previousCovered != null) {
			// extend previous covered
			updateEnd(previousCovered, end, readPos, recordExtended);
		} else {
			// create uncovered
			createNotCovered(unknown.getStart(), winPos);
			// create covered
			createCovered(winPos, readPos, length, recordExtended);
		}

		// update unknown segment
		updateStart(unknown, end);
	}
	
	private void updateNotCovered2Covered(final ReferenceSegment notCovered, 
			final int winPos, final int readPos, final int length,
			final SAMRecordExtended recordExtended) {

		final int end = winPos + length;
		
		// create new or extend previous covered segment
		final ReferenceSegment previousCovered = getPreviousCovered(winPos, end);
		if (previousCovered != null) {
			// is the next segment covered too? 
			final ReferenceSegment nextCovered = getNextCovered(winPos, end);
			if (nextCovered != null) {
				mergeCovered(previousCovered, nextCovered, readPos, recordExtended);
			} else {
				// extend previous covered segment
				updateEnd(previousCovered, end, readPos, recordExtended);
				// accordingly shrink notCovered
				updateStart(notCovered, end);
			}
		} else {
			// is the next segment covered? 
			final ReferenceSegment nextCovered = getNextCovered(winPos, end);
			if (nextCovered != null) {
				// extend adjacent covered segment
				updateStart(nextCovered, winPos);
				// accordingly shrink notCovered 
				updateEnd(notCovered, winPos);
			} else {
				final int tmpNotCoveredEnd = notCovered.getEnd();
				
				// shrink not covered
				updateEnd(notCovered, winPos);
				// update to next created covered segment

				// create covered
				createCovered(winPos, end, readPos, recordExtended);
				// create notCovered
				createNotCovered(end, tmpNotCoveredEnd);
			}
		}
	}
	
	private void mergeCovered(final ReferenceSegment previous, final ReferenceSegment next, final int readPos, final SAMRecordExtended recordExtended) {
		// pick biggest segment
		ReferenceSegment tmp = previous;
		if (next.getEnd() - next.getStart() > previous.getEnd() - previous.getStart()) {
			tmp = next;
			updateStart(tmp, previous.getStart(), readPos, recordExtended);
		} else {
			updateEnd(tmp, next.getEnd(), readPos, recordExtended); 
		}
	}
	
	public void markNotCovered(int winPos, int length, final int nextPosition) {
	final List<ReferenceSegment> segments = getSegments(winPos, length);
		
		for (final ReferenceSegment segment : segments) {
			final int end = winPos + length;
			
			switch (segment.getType()) {
			
			case UNKNOWN:
				updateUnknown2NotCovered(segment, winPos, length);
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
	
	private void updateUnknown2NotCovered(final ReferenceSegment unknown, final int winPos, final int length) {
		assert winPos != unknown.getStart();
		
		final int end = winPos + length;

		// shrink unknown
		updateStart(unknown, winPos);
		// create not covered segment
		createNotCovered(winPos, end);
	}

	private List<ReferenceSegment> getSegments(final int winPos, final int length) {
 		final int end = winPos + length;
		final List<ReferenceSegment> segments = new ArrayList<ReferenceSegment>(3);
		ReferenceSegment segment = get(winPos);

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

	private void createCovered(final int windowStart, final int readStart, final int length, final SAMRecordExtended recordExtended) {
		add(new ReferenceSegment(this, TYPE.COVERED, windowStart, windowStart + length));
		setReference(windowStart, readStart, length, recordExtended);
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
	
	private void updateStart(final ReferenceSegment segment, final int windowStart, final int readPos, final SAMRecordExtended recordExtended) {
		if (windowStart < segment.getStart()) {
			setId(segment.getId(), windowStart, segment.getStart());
			final int length = segment.getStart() - windowStart;
			setReference(windowStart, readPos, length, recordExtended);
		}
		segment.updateStart(windowStart);
	}

	private void updateEnd(final ReferenceSegment segment, final int windowEnd) {
		if (windowEnd > segment.getEnd()) {
			setId(segment.getId(), segment.getEnd(), windowEnd);
		}
		segment.updateEnd(windowEnd);
	}
	
	private void updateEnd(final ReferenceSegment segment, final int windowEnd, final int readPos, final SAMRecordExtended recordExtended) {
		if (windowEnd > segment.getEnd()) {
			setId(segment.getId(), segment.getEnd(), windowEnd);
			if (segment.getType() == TYPE.COVERED) {
				final int length = windowEnd - segment.getEnd(); 
				setReference(segment.getEnd(), readPos, length, recordExtended);
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
	
	private void setReference(final int windowStart, final int readStart, final int length, SAMRecordExtended recordExtended) {
		//try{
			// FIXME System.arraycopy(recordExtended.getReferenceBlocks(), readStart, reference, windowStart, length);
		//} catch (ArrayIndexOutOfBoundsException e) {
		//	int i = 0;
		//	i++;
		//}
	}
	
	public int getNextId() {
		return segments.size();
	}
	
	private ReferenceSegment getPreviousCovered(final int start, final int end) {
		if (start <= 0) {
			return null;
		}
		final ReferenceSegment previous = get(start - 1);
		return previous.getType() == TYPE.COVERED ? previous : null;
	}

	private ReferenceSegment getNextCovered(final int start, final int end) {
		if (end >= activeWindowSize) {
			return null;
		}
		final ReferenceSegment next = get(end);
		return next.getType() == TYPE.COVERED ? next : null;
	}

	public byte getReference(final int winPos) {
		return reference[winPos];
	}
	
}
