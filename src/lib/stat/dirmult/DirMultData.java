package lib.stat.dirmult;


public class DirMultData {

	private final int categories;
	private final double[][] data;

	public DirMultData(final int categories, final double[][] data) {
		this.categories = categories;
		this.data 		= data;
	}

	public int getReplicates() {
		return data.length;
	}
	
	public double[] getReplicate(final int replicate) {
		return data[replicate];
	}
	
	public double getReplicate(final int replicate, final int category) {
		return data[replicate][category];
	}

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
	
	public int getCategories() {
		return categories;
	}

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
	
}
