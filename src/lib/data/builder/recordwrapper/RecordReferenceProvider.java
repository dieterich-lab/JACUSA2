package lib.data.builder.recordwrapper;

import java.util.List;

import lib.util.Base;

public interface RecordReferenceProvider {

	Base getReferenceBase(int referencePosition);
	// List<AlignmentPosition> getMismatchRefPositions();
	List<Integer> getMismatchRefPositions();
	
}
