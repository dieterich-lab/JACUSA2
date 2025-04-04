package lib.record;

import java.util.List;

import lib.util.Base;

/**
 * DOCUMENT
 */
public interface RecordRefProvider {

	Base getRefBase(final int refPos, final int readPos);
	
	List<AlignedPosition> getMismatchPositions();
	
}
