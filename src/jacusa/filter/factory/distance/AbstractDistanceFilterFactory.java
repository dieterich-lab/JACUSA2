package jacusa.filter.factory.distance;

import jacusa.filter.factory.AbstractDataFilterFactory;
import jacusa.filter.factory.AbstractFilterFactory;
import lib.data.AbstractData;

public abstract class AbstractDistanceFilterFactory<T extends AbstractData>
extends AbstractDataFilterFactory<T> {

	private int filterDistance;
	private double filterMinRatio;
	private int filterMinCount;
		
	public AbstractDistanceFilterFactory(final char c, final String desc, 
			final int defaultFilterDistance, 
			final double defaultFilterMinRatio, 
			final int defaultFilterMinCount) {

		super(c, desc + "\n   Default: " + 
			defaultFilterDistance + ":" 
			+ defaultFilterMinRatio + ":" 
			+ defaultFilterMinCount + 
			" (" + c+ ":distance:min_ratio:min_count)");

		filterDistance = defaultFilterDistance;
		filterMinRatio = defaultFilterMinRatio;
		filterMinCount = defaultFilterMinCount;
	}

	@Override
	public void processCLI(String line) throws IllegalArgumentException {
		if (line.length() == 1) {
			return;
		}

		final String[] s = line.split(Character.toString(AbstractFilterFactory.OPTION_SEP));

		// format D:distance:minRatio:minCount
		for (int i = 1; i < s.length; ++i) {
			switch(i) {
			case 1:
				final int filterDistance = Integer.valueOf(s[i]);
				if (filterDistance < 0) {
					throw new IllegalArgumentException("Invalid distance " + line);
				}
				this.filterDistance = filterDistance;
				break;

			case 2:
				final double filterMinRatio = Double.valueOf(s[i]);
				if (filterMinRatio < 0.0 || filterMinRatio > 1.0) {
					throw new IllegalArgumentException("Invalid minRatio " + line);
				}
				this.filterMinRatio = filterMinRatio;
				break;

			case 3:
				final int filterMinCount = Integer.valueOf(s[i]);
				if (filterMinCount < 0) {
					throw new IllegalArgumentException("Invalid minCount " + line);
				}
				this.filterMinCount = filterMinCount;
				break;
				
			default:
				throw new IllegalArgumentException("Invalid argument: " + line);
			}
		}
	}

	public int getDistance() {
		return filterDistance;
	}

	public double getMinRatio() {
		return filterMinRatio;
	}

	public int getMinCount() {
		return filterMinCount;
	}

}
