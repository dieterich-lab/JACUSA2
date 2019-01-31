package test.utlis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import htsjdk.samtools.SAMRecord;

public class SAMRecordArgumentConverter extends SimpleArgumentConverter {
	
	@Override
	protected Object convert(Object arg0, Class<?> arg1) throws ArgumentConversionException {
		assertEquals(SAMRecord.class, arg1, "Can only convert to SAMRecord");
		final String[] s = String.valueOf(arg0).split(",");
		
		final String contig 			= s[0];
		final int referenceStart 		= Integer.parseInt(s[1]); 
		final String cigarStr 			= s[2];
		final boolean negativeStrand 	= Boolean.parseBoolean(s[3]);
		final String MD					= s[4];
		
		return new SAMRecordBuilder()
				.withSERead(contig, referenceStart, negativeStrand, cigarStr, MD)
				.getRecords().iterator().next();
	}

}
