package lib.recordextended;

import java.util.List;

import lib.util.Base;

public interface RecordReferenceProvider {

	Base getReferenceBase(final int refPos, final int readPos);
	
	List<AlignedPosition> getMismatchPositions();
	
}
