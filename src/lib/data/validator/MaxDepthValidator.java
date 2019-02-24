package lib.data.validator;

import lib.data.storage.WindowCoverage;
import lib.util.position.Position;

public class MaxDepthValidator implements Validator {

	private final int maxDepth;
	private final WindowCoverage hasWindowCoverage;
	
	public MaxDepthValidator(final int maxDepth, final WindowCoverage hasWindowCoverage) {
		this.maxDepth 			= maxDepth;
		this.hasWindowCoverage 	= hasWindowCoverage;
	}
	
	@Override
	public boolean isValid(Position pos) {
		final int winPos = pos.getWindowPosition();
		// ensure max depth
		return hasWindowCoverage.getCoverage(winPos) < maxDepth;
	}
	
}
