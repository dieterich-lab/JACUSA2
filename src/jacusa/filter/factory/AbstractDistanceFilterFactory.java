package jacusa.filter.factory;

import lib.data.AbstractData;
import lib.data.generator.DataGenerator;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReferenceBase;

public abstract class AbstractDistanceFilterFactory<T extends AbstractData & hasBaseCallCount & hasReferenceBase, F extends AbstractData & hasBaseCallCount>
extends AbstractFilterFactory<T, F> {

	private int filterDistance;
	private double filterMinRatio;
	private int filterMinCount;
		
	public AbstractDistanceFilterFactory(final char c, final String desc, 
			final int defaultFilterDistance, 
			final double defaultFilterMinRatio, 
			final int defaultFilterMinCount, 
			final DataGenerator<F> dataGenerator) {
		super(c, desc + " Default: " + 
			defaultFilterDistance + ":" 
			+ defaultFilterMinRatio + ":" 
			+ defaultFilterMinCount + 
			" (" + c+ ":distance:min_ratio:min_count)",
			dataGenerator);
		filterDistance = defaultFilterDistance;
		filterMinRatio = defaultFilterMinRatio;
		filterMinCount = defaultFilterMinCount;
	}

	@Override
	public void processCLI(String line) throws IllegalArgumentException {
		if (line.length() == 1) {
			return;
		}

		final String[] s = line.split(Character.toString(AbstractFilterFactory.SEP));

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

	public int getFilterDistance() {
		return filterDistance;
	}

	public double getFilterMinRatio() {
		return filterMinRatio;
	}

	public int getFilterMinCount() {
		return filterMinCount;
	}

}
