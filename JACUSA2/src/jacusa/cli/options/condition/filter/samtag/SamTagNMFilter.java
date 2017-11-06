package jacusa.cli.options.condition.filter.samtag;

import net.sf.samtools.SAMRecord;

public class SamTagNMFilter implements SamTagFilter {

	private int value;

	public SamTagNMFilter(int value) {
		this.value = value;
	}

	@Override
	public boolean filter(SAMRecord samRecord) {
		Object object = samRecord.getAttribute("NM");
		if(object == null) {
			return false;
		}
		return (Integer)object > value;
	}

}
