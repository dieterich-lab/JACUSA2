package lib.cli.options.filter.has;

import java.util.SortedSet;

public interface HasReadSubstitution {
	
	public static final String READ_SUB = "read_sub";
	
	SortedSet<BaseSub> getReadSubstitutions();
	void addReadSubstitution(BaseSub baseSubstitution);
	
}
