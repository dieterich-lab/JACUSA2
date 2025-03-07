package lib.data.count;

public class INDELCount {

	private int insertions;
	private int deletions;
	
	public INDELCount() {
		insertions = 0;
		deletions = 0;
	}
	
	public INDELCount(final INDELCount indelCount) {
		this.insertions = indelCount.insertions;
		this.deletions	= indelCount.deletions;
	}
	
	public INDELCount(final int insertions, final int deletions) {
		this.insertions = insertions;
		this.deletions	= deletions;
	}
	
	public INDELCount copy() {
		return new INDELCount(this);
	}
	
	public void clear() {
		insertions = 0;
		deletions = 0;
	}
	
	public void add(INDELCount indelCount) {
		this.insertions += indelCount.insertions;
		this.deletions += indelCount.deletions;
	}

	public void addDeletion(final int deletions) {
		this.deletions += deletions;
	}
	
	public void addInsertion(final int insertions) {
		this.insertions += insertions;
	}

	public int getInsertionCount() {
		return insertions;
	}
	
	public int getDeletionCount() {
		return deletions;
	}

	public int getReads() {
		return getInsertionCount() + getDeletionCount();
	}
	
	public double getInsertionRatio(final int reads) {
		return (double) insertions / reads;
	}
	
	public double getDeletionRatio(final int reads) {
		return (double) deletions / reads;
	}
	
	boolean specificEquals(final INDELCount indelCount) {
		if (indelCount == null) {
			return false;
		}
		if (indelCount == this) {
			return true;
		}

		return this.insertions == indelCount.insertions && deletions == indelCount.deletions;
	}
	
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
		sb.append("Insertions: " + this.insertions + "\n");
		sb.append("Deletions: " + this.deletions + "\n");
		
		return sb.toString();
	}
	
}
