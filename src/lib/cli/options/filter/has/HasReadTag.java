package lib.cli.options.filter.has;

import java.util.SortedSet;

public interface HasReadTag {
	
	public static final String READ_TAG = "tag";
	
	SortedSet<BaseSub> getReadTags();
	void addReadTag(BaseSub baseSub);
	
}
