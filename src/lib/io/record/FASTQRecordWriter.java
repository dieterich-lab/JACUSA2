package lib.io.record;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;

import htsjdk.samtools.SAMRecord;

public class FASTQRecordWriter
extends AbstractRecordWriter {

	private BufferedWriter bw;
	
	public FASTQRecordWriter(final String filename, final AbstractRecordFormat format) {
		super(filename, format);
		try {
			bw = new BufferedWriter(new FileWriter(new File(filename)));
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	@Override
	public void addHeader(List<AbstractConditionParameter<?>> conditionParameters) {
		// no header for FASTQ
	}
	
	@Override
	public void addRecord(SAMRecord record) {
		try {
			bw.write("@" + record.getReadName() + "\n");
			bw.write(record.getReadString() + "\n");
			bw.write("+" + "\n");
			bw.write(record.getBaseQualityString() + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
