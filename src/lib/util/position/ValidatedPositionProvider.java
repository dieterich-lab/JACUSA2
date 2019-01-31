package lib.util.position;

import java.util.List;

import lib.data.validator.CombinedValidator;
import lib.data.validator.Validator;

public class ValidatedPositionProvider implements PositionProvider {

	private final PositionProvider positionProvider;
	private final Validator validator;
	
	private Position nextPos;
	
	public ValidatedPositionProvider(
			final PositionProvider positionProvider,
			final Validator validator) {
		
		this.positionProvider 	= positionProvider;
		this.validator			= validator;
	}
	
	public ValidatedPositionProvider(
			final PositionProvider positionProvider,
			final List<Validator> validators) {
		
		this(positionProvider, new CombinedValidator(validators));
	}
	
	@Override
	public boolean hasNext() {
		while (nextPos != null && positionProvider.hasNext()) {
			final Position tmpNextPos = positionProvider.next();
			if (validator.isValid(tmpNextPos)) {
				nextPos = tmpNextPos;
				return true;
			}
		}
		return false;
	}

	@Override
	public Position next() {
		final Position tmpNextPos = nextPos.copy();
		nextPos = null;
		return tmpNextPos;
	}

}
