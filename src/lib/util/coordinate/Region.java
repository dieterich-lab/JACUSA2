package lib.util.coordinate;

public class Region {

	private final int referenceStart;
	private final int readStart;
	private final int length;
	
	public Region(
			final int referenceStart,
			final int readStart,
			final int length) {
		
		this.referenceStart = referenceStart;
		this.readStart = readStart;
		this.length = length;
	}

	public int getReferenceStart() {
		return referenceStart;
	}
	
	public int getReadStart() {
		return readStart;
	}
	
	public int getLength() {
		return length;
	}
	
}
