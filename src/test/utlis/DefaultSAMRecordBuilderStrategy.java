package test.utlis;

import htsjdk.samtools.CigarOperator;

public class DefaultSAMRecordBuilderStrategy implements SAMRecordBuilderStrategy {

	private final int readLength;
	
	public DefaultSAMRecordBuilderStrategy(final int readLength) {
		this.readLength = readLength;
	}

	@Override
	public void useStrategy(final String contig, final SAMRecordBuilder builder) {
		final int refSeqLength = builder.getContig2refSeq().get(contig).length();
		for (int refStart = 1; refStart < refSeqLength - readLength; ++refStart) {
			final String cigarStr	= readLength + CigarOperator.M.toString();
			final String MD 		= Integer.toString(readLength);
			builder.withSERead(contig, refStart, false, cigarStr, MD);
		}
	}	
}
