package lib.io.record;

import java.io.File;
import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMFileWriter;

public class SAMRecordWriter 
extends AbstractRecordFactoryWriter {

	public SAMRecordWriter(final String filename, final AbstractRecordFormat format, 
			final SAMFileHeader header) {
		super(filename, format, header);
		final File file = new File(filename);
		final SAMFileWriter writer = getFactory().makeSAMWriter(header, true, file);
		setWriter(writer);
	}

	@Override
	public void addHeader(List<AbstractConditionParameter<?>> conditionParameters) {
		// handled elsewhere...
	}
	
}
