package lib.io.record;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMFileWriterFactory;
import htsjdk.samtools.SAMRecord;

public abstract class AbstractRecordFactoryWriter 
extends AbstractRecordWriter {

	private SAMFileHeader header;
	private SAMFileWriterFactory factory;
	private SAMFileWriter writer;
	
	public AbstractRecordFactoryWriter(final String filename, final AbstractRecordFormat format,
			final SAMFileHeader header) {
		super(filename, format);

		this.header = header;
		factory = new SAMFileWriterFactory();
	}

	protected void setWriter(final SAMFileWriter writer) {
		this.writer = writer;
	}
	
	protected SAMFileWriterFactory getFactory() {
		return factory;
	}

	protected SAMFileHeader getHeader() {
		return header;
	}
	
	protected SAMFileWriter getWriter() {
		return writer;
	}
	
	public void addRecord(final SAMRecord record) {
		writer.addAlignment(record);
	}

	@Override
	public void close() {
		writer.close();
	}
	
}
