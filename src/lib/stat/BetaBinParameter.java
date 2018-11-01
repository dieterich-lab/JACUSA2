package lib.stat;

public class BetaBinParameter {

	private boolean showAlpha;

	private double threshold;
	
	public BetaBinParameter() {
		showAlpha 			= false;

		threshold			= Double.NaN;
	}

	public boolean isShowAlpha() {
		return showAlpha;
	}

	public void setShowAlpha(boolean showAlpha) {
		this.showAlpha = showAlpha;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
}
