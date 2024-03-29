package lib.worker;

/**
 * TODO
 */
public class ThreadIdContainer {

	private final int threadId;

	private int previous;
	private int next;

	public ThreadIdContainer(final int threadId) {
		this.threadId = threadId;
		previous = -1;
		next = -1;
	}

	public int getThreadId() {
		return threadId;
	}	
	
	public void setPrevious(final int threadId) {
		this.previous = threadId;
	}

	public void setNext(final int threadId) {
		this.next = threadId;
	}

	public int getPrevious() {
		return previous;
	}
	
	public int getNext() {
		return next;
	}

}
