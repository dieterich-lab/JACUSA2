package test.jacusa.estimate;

import java.io.FileNotFoundException;

import org.junit.jupiter.params.provider.Arguments;

import lib.stat.dirmult.DirMultData;
import test.utlis.AbstractArgumentIterator;

public class DirMultCSVArgumentIterator extends AbstractArgumentIterator {

	public DirMultCSVArgumentIterator(final String fileName) throws FileNotFoundException {
		super(fileName);
	}
	
	@Override
	protected Arguments createArguments(String[] cols) {
		final int n = cols.length;
		final int categories = Integer.parseInt(cols[0]);
		final int replicates = Integer.parseInt(cols[1]);
		final boolean addInitAlpha = 2 + categories * replicates + categories + 1 < n;  

		final double[][] data = new double[replicates][categories];
		final double[] initAlpha = new double[categories];
		final double[] expectedAlpha = new double[categories];

		int i = 2;
		for (int replicateIndex = 0; replicateIndex < replicates; replicateIndex++) {
			i = parseDouble(cols, i, categories, data[replicateIndex]);
		}
		if (addInitAlpha) {
			i = parseDouble(cols, i, categories, initAlpha);
		}
		i = parseDouble(cols, i, categories, expectedAlpha);
		
		final DirMultData dirMultData = new DirMultData(categories, data);
		final double expectedLL = Double.parseDouble(cols[i]);
		
		if (addInitAlpha) {
			return Arguments.of(dirMultData, initAlpha, expectedAlpha, expectedLL);
		} else {
			return Arguments.of(dirMultData, expectedAlpha, expectedLL);
		}
	}

	private int parseDouble(final String[] cols, int i, final int categories, final double[] dest) {
		for (int categoryIndex = 0; categoryIndex < categories; ++categoryIndex, ++i) {
			dest[categoryIndex] = Double.parseDouble(cols[i]);
		}
		return i;
	}
	
}
