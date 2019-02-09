package lib.util.position;

import java.util.Iterator;
import java.util.List;

public class CombinedPositionProvider implements PositionProvider {

	private Iterator<PositionProvider> it;
	
	private PositionProvider positionProvider;
	
	public CombinedPositionProvider(final List<PositionProvider> positionProviders) {
		it = positionProviders.iterator();
	}
	
	@Override
	public boolean hasNext() {
		while (positionProvider == null || ! positionProvider.hasNext()) {
			if (it.hasNext()) {
				positionProvider = it.next();
			} else {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public Position next() {
		return positionProvider.next();
	}
	
}
