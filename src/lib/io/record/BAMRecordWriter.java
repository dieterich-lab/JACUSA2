package lib.io.record;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMFileWriter;

import java.io.File;
import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;

public class BAMRecordWriter 
extends AbstractRecordFactoryWriter {
	
	public BAMRecordWriter(final String filename, final AbstractRecordFormat format, 
			final SAMFileHeader header) {
		super(filename, format, header);
		final File file = new File(filename);
		final SAMFileWriter writer = getFactory().makeBAMWriter(header, true, file);
		setWriter(writer);
	}

	@Override
	public void addHeader(List<AbstractConditionParameter<?>> conditionParameters) { 
		// handled elsewhere...
	}
	
}
