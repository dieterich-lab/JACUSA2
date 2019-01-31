package lib.recordextended;

import java.util.List;

import lib.util.Base;

public interface RecordReferenceProvider {

	Base getReferenceBase(int refPos);
	List<CombinedPosition> getMismatchPositions();
	
}
