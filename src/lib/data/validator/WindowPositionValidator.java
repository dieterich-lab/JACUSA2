package lib.data.validator;

import lib.util.position.Position;

public class WindowPositionValidator implements Validator {
	
	@Override
	public boolean isValid(Position pos) {
		return pos.getWindowPosition() >= 0;
	}
	
}
