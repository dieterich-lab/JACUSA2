package lib.cli.options.filter.has;

import java.util.SortedSet;

public interface HasReadSub {
	
	public static final String READ_SUB = "read_sub";
	
	SortedSet<BaseSub> getReadSubs();
	void addReadSub(BaseSub baseSub);
	
}
