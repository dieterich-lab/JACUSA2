package lib.cli.options.condition.filter.samtag;

import htsjdk.samtools.SAMRecord;

public class MaxValueSamTagFilter {

	private int value;
	private String tag;

	public MaxValueSamTagFilter(String tag, int value) {
		this.value = value;
		this.tag = tag;
	}

	public boolean filter(SAMRecord samRecord) {
		if (! samRecord.hasAttribute(tag)) {
			return false;
		}
		return samRecord.getIntegerAttribute(tag) > value;
	}

	public String getTag() {
		return tag;
	}
	
	public int getValue() {
		return value;
	}
}
