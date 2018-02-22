package lib.data;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class ReadArrestCount {

	// counter start,internal,end
	private int readStart;
	private int readInternal;
	private int readEnd;

	// read arrest, through
	private int readArrest;
	private int readThrough;

	public ReadArrestCount() {
		readStart 		= 0;
		readInternal	= 0;
		readEnd 		= 0;
		
		readArrest	= 0;
		readThrough = 0;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param readArrestCount
	 */
	public ReadArrestCount(final ReadArrestCount readArrestCount) {
		this.readStart 		= readArrestCount.readStart;
		this.readInternal 	= readArrestCount.readInternal;
		this.readEnd 		= readArrestCount.readEnd;
		
		this.readArrest 	= readArrestCount.readArrest;
		this.readThrough	= readArrestCount.readThrough;
	}
	
	public int getReadStart() {
		return readStart;
	}

	public void setReadStart(final int readStart) {
		this.readStart = readStart;
	}

	public int getReadInternal() {
		return readInternal;
	}

	public void setReadInternal(final int readInternal) {
		this.readInternal = readInternal;
	}

	public int getReadEnd() {
		return readEnd;
	}

	public void setReadEnd(int readEnd) {
		this.readEnd = readEnd;
	}

	public int getReadArrest() {
		return readArrest;
	}
	
	public void setReadArrest(final int readArrest) {
		this.readArrest = readArrest;
	}
	
	public int getReadThrough() {
		return readThrough;
	}
	
	public void setReadThrough(final int readThrough) {
		this.readThrough = readThrough;
	}
	
	public void add(final ReadArrestCount readArrestCount) {
		readStart 		+= readArrestCount.readStart;
		readInternal 	+= readArrestCount.readInternal;
		readEnd 		+= readArrestCount.readEnd;
		
		readArrest	+= readArrestCount.readArrest;
		readThrough	+= readArrestCount.readThrough;
	}
	
	public ReadArrestCount copy() {
		return new ReadArrestCount(this);
	}
	
}