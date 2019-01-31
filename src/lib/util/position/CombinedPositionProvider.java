package lib.util.position;

import java.util.Iterator;
import java.util.List;

public class CombinedPositionProvider implements PositionProvider {

	private Iterator<PositionProvider> it;
	
	private PositionProvider positionProvider;
	
	public CombinedPositionProvider(final List<PositionProvider> positionProviders) {
		it = positionProviders.iterator();
		
		if (it.hasNext()) {
			positionProvider = it.next();
		}
	}
	
	@Override
	public boolean hasNext() {
		if (positionProvider != null || positionProvider.hasNext()) {
			return true;
		} else if (it.hasNext()) {
			positionProvider = it.next();
			return hasNext();
		}
		return false;
	}

	@Override
	public Position next() {
		return positionProvider.next();
	}
	
}
