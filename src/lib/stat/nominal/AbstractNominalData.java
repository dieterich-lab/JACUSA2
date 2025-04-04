package lib.stat.nominal;

/**
 * DOCUMENT
 */
class AbstractNominalData implements NominalData {

	private final int categories;
	private final double[][] data;

	AbstractNominalData(final int categories, final double[][] data) {
		this.categories = categories;
		this.data 		= data;
	}

	@Override
	public int getReplicates() {
		return data.length;
	}
	
	@Override
	public double[] getReplicate(final int replicate) {
		return data[replicate];
	}
	
	@Override
	public double getReplicate(final int replicate, final int category) {
		return data[replicate][category];
	}

	@Override
	public double[] getRowWiseSums() {
		final int replicates = data.length;
		final double[] sums = new double[replicates];
		for (int replicateIndex = 0; replicateIndex < replicates; ++replicateIndex) {
			sums[replicateIndex] = 0;
			for (int i = 0; i < categories; ++i) {
				sums[replicateIndex] += data[replicateIndex][i];
			}
		}
		return sums;
	}
	
	@Override
	public int getCategories() {
		return categories;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
	
		sb.append("Categories: ");
		sb.append(categories);
		sb.append('\n');
		
		sb.append("Data: ");
		sb.append('\n');
		for (int replicateIndex = 0; replicateIndex < getReplicates(); ++replicateIndex) {
			for (int i = 0; i < categories; ++i) {
				if (i > 0) {
					sb.append(' ');					
				}
				sb.append(getReplicate(replicateIndex, i));
			}
			sb.append('\n');
		}
		
		return sb.toString();
	}

	public static class Parser implements lib.util.Parser<AbstractNominalData> {
		
		public static final char DEFAULT_SEP = ',';
		
		private final char sep;
		
		public Parser() {
			this(DEFAULT_SEP);
		}
		
		public Parser(final char sep) {
			this.sep = sep;
		}
		
		@Override
		public AbstractNominalData parse(String s) {
			if (s == null) {
				throw new IllegalArgumentException("s cannot be null");
			}
			
			final String[] e = s.split(Character.toString(sep));
			// number of elements
			final int n = e.length;
			// read number categories and replicates
			final int categories = Integer.parseInt(e[0]);
			// infer number of replicates
			final int replicates = (n - 1) / categories;
			// are value for alphaInit provided
			// create container(s)
			final double[][] data = new double[replicates][categories];

			// index to e - ignore first two values
			int i = 1;
			// fill data
			for (int replicate = 0; replicate < replicates; replicate++) {
				i = parseDouble(e, i, categories, data[replicate]);
			}
			return new AbstractNominalData(categories, data);
		}
		
		@Override
		public String wrap(AbstractNominalData o) {
			final StringBuilder sb = new StringBuilder();
			sb.append(o.getCategories());
			for (int replicate = 0; replicate < o.getReplicates(); replicate++) {
				for (int category = 0; category < o.getCategories(); category++) {
					sb.append(sep);
					sb.append(o.getReplicate(replicate, category));
				}
			}
			return sb.toString();
		}

		private int parseDouble(final String[] cols, int i, final int categories, final double[] dest) {
			for (int categoryIndex = 0; categoryIndex < categories; ++categoryIndex, ++i) {
				dest[categoryIndex] = Double.parseDouble(cols[i]);
			}
			return i;
		}

		
	}
	
}
