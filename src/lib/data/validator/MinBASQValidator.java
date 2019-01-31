package lib.data.validator;

import lib.util.position.Position;

public class MinBASQValidator implements Validator {

	private final byte minBASQ;
	
	public MinBASQValidator(final byte minBASQ) {
		this.minBASQ = minBASQ;
	}

	@Override
	public boolean isValid(Position pos) {
		return pos.getReadBaseCallQuality() >= minBASQ;
	}

}
