package lib.stat.nominal;

public interface NominalData {

	int getReplicates();
	
	double[] getReplicate(int replicate);

	double getReplicate(int replicate, int category);

	double[] getRowWiseSums();

	int getCategories();

	public static NominalData build(final int categories, final double[][] data) {
		if (categories == 2) {
			return new BiNomialData(categories, data); 
		} else if (categories > 2) {
			return new MultiNomialData(categories, data); 
		} else {
			throw new IllegalArgumentException();			
		}
	}
	
	public static class Parser implements lib.util.Parser<NominalData> {
		
		public static final char SEP = ',';
		
		private final char sep;
		
		public Parser() {
			this(SEP);
		}
		
		public Parser(final char sep) {
			this.sep = sep;
		}
		
		@Override
		public NominalData parse(String s) {
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
			return build(categories, data);
		}
		
		@Override
		public String wrap(NominalData o) {
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