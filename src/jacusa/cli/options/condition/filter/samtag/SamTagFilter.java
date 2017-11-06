package jacusa.cli.options.condition.filter.samtag;

import net.sf.samtools.SAMRecord;

public interface SamTagFilter {

	boolean filter(SAMRecord samRecord);

}
