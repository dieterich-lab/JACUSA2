package lib.util.position;

import java.util.Collection;
import java.util.Iterator;

public class CollectionPositionProvider implements PositionProvider {

	private Iterator<Position> it;
	
	public CollectionPositionProvider(final Collection<Position> positions) {
		it = positions.iterator();
	}

	@Override
	public boolean hasNext() {
		return it.hasNext();
	}

	@Override
	public Position next() {
		return it.next();
	}

}
