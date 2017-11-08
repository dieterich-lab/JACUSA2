package lib.io.copytmp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lib.util.AbstractTool;

import htsjdk.samtools.SAMFileWriter;
import htsjdk.samtools.SAMFileWriterFactory;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;

public class CopyTmpSAMRecords 
implements CopyTmp {

	private final List<SAMFileWriter> recordWriters;
	
	private final List<SamReader> tmpRecordReaders;

	private final List<SAMFileWriter> tmpRecordWriters;
	private final List<SAMRecordIterator> recordIterators;
	
	private List<int[]> iteration2storedRecords;
	
	public CopyTmpSAMRecords(final int threadId, 
			final List<String> recordOutputFilenames, 
			final List<SAMFileWriter> recordWriters) throws IOException {
		this.recordWriters = recordWriters;

		final List<String> tmpRecordOutputFilename = createTmpRecordOutputFilenames(
				threadId, recordOutputFilenames);
		tmpRecordReaders = createSamReaders(tmpRecordOutputFilename);
		
		tmpRecordWriters = createTmpRecordWriters(tmpRecordOutputFilename, recordWriters);
		recordIterators = createSAMRecordIterators(tmpRecordReaders);
		iteration2storedRecords = new ArrayList<int[]>();
	}

	public void addRecord(final int conditionIndex, final SAMRecord record) {
		recordWriters.get(conditionIndex).addAlignment(record);
		final int iteration = iteration2storedRecords.size() - 1;
		final int[] storedRecords = iteration2storedRecords.get(iteration);
		storedRecords[conditionIndex]++;
		iteration2storedRecords.set(iteration, storedRecords); 
	}
	
	public void close() throws IOException {
		for (final SAMFileWriter tmpRecordWriter : tmpRecordWriters) {
			tmpRecordWriter.close();
		}
		tmpRecordWriters.clear();
		
		for (final SAMRecordIterator recordIterator : recordIterators) {
			recordIterator.close();
		}
		recordIterators.clear();
		
		for (final SamReader tmpRecordReader : tmpRecordReaders) {
			tmpRecordReader.close();
		}
		tmpRecordReaders.clear();
	}
	
	@Override
	public void copy(final int iteration) {
		for (int conditionIndex = 0; conditionIndex < getConditionSize(); conditionIndex++) {
			copyRecords(iteration, conditionIndex);
		}
	}

	@Override
	public void nextIteration() {
		iteration2storedRecords.add(createRecordCount());
	}
	
	// TODO make this faster - use power of BAM
	private void copyRecords(final int iteration, final int conditionIndex) {
		// countes how many records where read from tmp
		int copiedRecords = 0;
		final int storedRecords = iteration2storedRecords.get(iteration)[conditionIndex];
		final SAMRecordIterator recordIterator = recordIterators.get(conditionIndex);
		
		// switch when there are no more records
		// OR
		// when records from an other thread are needed...
		while (recordIterator.hasNext() && copiedRecords <= storedRecords) {
			final SAMRecord record = recordIterator.next();
			final SAMFileWriter writer = recordWriters.get(conditionIndex);
			writer.addAlignment(record);
			++copiedRecords;
		}
	} 

	private int getConditionSize() {
		return recordWriters.size();
	}

	private int[] createRecordCount() {
		return new int[getConditionSize()];
	}

	private List<String> createTmpRecordOutputFilenames(
			final int threadId, final List<String> recordOutputFilenames) throws IOException {
		final List<String> tmpRecordFilenames = new ArrayList<String>(getConditionSize());
		for (int conditionIndex = 0; conditionIndex < getConditionSize(); conditionIndex++) {
			String prefix = recordOutputFilenames.get(conditionIndex) + "_" + threadId + "_" + conditionIndex;
			final File file = File.createTempFile(prefix, ".sam.gz");
			if (! AbstractTool.getLogger().isDebug()) {
				file.deleteOnExit();
			}
			tmpRecordFilenames.add(file.getCanonicalPath());
		}
		return tmpRecordFilenames;
		
	}
	
	private List<SAMFileWriter> createTmpRecordWriters(
			final List<String> recordOutputFilenames,
			final List<SAMFileWriter> recordWriters) {
		final SAMFileWriterFactory factory = new SAMFileWriterFactory();
		// TODO set options

		final List<SAMFileWriter> tmpWriters = new ArrayList<SAMFileWriter>(getConditionSize());
		for (int conditionIndex = 0; conditionIndex < getConditionSize(); conditionIndex++) {
			final File file = new File(recordOutputFilenames.get(conditionIndex));
			SAMFileWriter tmpWriter = factory.makeBAMWriter(
					recordWriters.get(conditionIndex).getFileHeader(), true, file);
			tmpWriters.add(tmpWriter);
		}
				
		return tmpWriters;
	}
	
	private List<SamReader> createSamReaders(final List<String> recordFilenames) {
		final SamReaderFactory samReaderFactory = SamReaderFactory.make();
		final List<SamReader> samReaders = new ArrayList<SamReader>(getConditionSize());
		
		for (final String recordFilename : recordFilenames) {
			final File file = new File(recordFilename);
			final SamReader samReader = samReaderFactory.open(file);
			samReaders.add(samReader);
		}
				
		return samReaders;
	}
	
	private List<SAMRecordIterator> createSAMRecordIterators(final List<SamReader> recordReaders) {
		final List<SAMRecordIterator> samRecordIterators = new ArrayList<SAMRecordIterator>(getConditionSize());
		
		for (SamReader samReader : recordReaders) {
			final SAMRecordIterator samRecordIterator = samReader.iterator();
			samRecordIterators.add(samRecordIterator);
		}
				
		return samRecordIterators;
	}
	
}
