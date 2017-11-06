package jacusa.data;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class ReadInfoCount {

	// container
	private int start;
	private int inner;
	private int end;

	private int arrest;
	private int through;
	
	public ReadInfoCount() {
		start 	= 0;
		inner 	= 0;
		end 	= 0;
		
		arrest	= 0;
		through = 0;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param readInfoCount
	 */
	public ReadInfoCount(final ReadInfoCount readInfoCount) {
		this.start 		= readInfoCount.start;
		this.inner 		= readInfoCount.inner;
		this.end 		= readInfoCount.end;
		
		this.arrest 	= readInfoCount.arrest;
		this.through	= readInfoCount.through;
	}
	
	public int getStart() {
		return start;
	}

	public void setStart(final int start) {
		this.start = start;
	}

	public int getInner() {
		return inner;
	}

	public void setInner(final int inner) {
		this.inner = inner;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getCoverage() {
		return start + inner + end;
	}

	public int getArrest() {
		return arrest;
	}
	
	public void setArrest(final int arrest) {
		this.arrest = arrest;
	}
	
	public int getThrough() {
		return through;
	}
	
	public void setThrough(final int through) {
		this.through = through;
	}
	
	public void add(final ReadInfoCount readInfoCount) {
		start 	+= readInfoCount.start;
		inner 	+= readInfoCount.inner;
		end 	+= readInfoCount.end;
		
		arrest	+= readInfoCount.arrest;
		through	+= readInfoCount.through;
	}
	
	public ReadInfoCount copy() {
		return new ReadInfoCount(this);
	}
	
}