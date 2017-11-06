package jacusa.cli.options.condition.filter.samtag;

import net.sf.samtools.SAMRecord;

public class SamTagNHFilter implements SamTagFilter {

	private int value;

	public SamTagNHFilter(int value) {
		this.value = value;
	}

	@Override
	public boolean filter(SAMRecord samRecord) {
		Object object = samRecord.getAttribute("NH");
		if(object == null) {
			return false;
		}
		return (Integer)object > value;
	}

}
