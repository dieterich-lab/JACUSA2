package lib.cli.options.condition.filter.samtag;

import htsjdk.samtools.SAMRecord;

public interface SamTagFilter {

	boolean filter(SAMRecord samRecord);

}
