package lib.data.cache.extractor;

import lib.data.AbstractData;
import lib.data.cache.container.ReferenceProvider;
import lib.data.has.HasReferenceBase;
import lib.util.coordinate.Coordinate;

public class ReferenceBaseSetter<T extends AbstractData & HasReferenceBase> 
implements ReferenceSetter<T> {

	public void setReference(final Coordinate coordinate, final T data, ReferenceProvider referenceProvider) {
		final byte refBase = referenceProvider.getReference(coordinate);
		data.setReferenceBase(refBase);
	}
	
}
