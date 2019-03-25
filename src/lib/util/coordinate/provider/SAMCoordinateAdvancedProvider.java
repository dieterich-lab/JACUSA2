/**
 * 
 */
package lib.util.coordinate.provider;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil.STRAND;
import lib.util.coordinate.OneCoordinate;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SamReader;

/**
 * @author mpiechotta
 *
 */
public class SAMCoordinateAdvancedProvider implements CoordinateProvider {

	private final boolean isStranded;
	
	
	private final Iterator<SAMSequenceRecord> sequenceRecordIterator;
	private final Map<String, SAMSequenceRecord> contig2sequenceRecord;
	
	private final int total;
	
	private final GeneralParameter parameter;
	
	private final List<List<SamReader>> readers;

	private SAMSequenceRecord sequenceRecord;
	private Coordinate currentCoordinate;
	
	/**
	 * 
	 */
	public SAMCoordinateAdvancedProvider(
			final boolean isStranded, 
			final List<SAMSequenceRecord> sequenceRecords, 
			final GeneralParameter parameter) {

		this.isStranded 		= isStranded;
		int tmpTotal = 0;
		sequenceRecords.size();
		for (final SAMSequenceRecord sr : sequenceRecords) {
			tmpTotal += sr.getSequenceLength() / parameter.getReservedWindowSize() + 1;
		}
		total = tmpTotal;
		sequenceRecordIterator 	= sequenceRecords.iterator();
		
		contig2sequenceRecord 	= sequenceRecords.stream()
				.collect(Collectors.toMap(SAMSequenceRecord::getSequenceName, Function.identity()));
		
		this.parameter			= parameter;
		
				
		// create readers for conditions and replicate
		readers = new ArrayList<List<SamReader>>(parameter.getConditionsSize());
		for (final ConditionParameter condition : parameter.getConditionParameters()) {
			final List<SamReader> tmpReaders = new ArrayList<SamReader>(condition.getReplicateSize());
			for (String fileName : condition.getRecordFilenames()) {
				tmpReaders.add(ConditionParameter.createSamReader(fileName));
			}
			readers.add(tmpReaders);
		}
		
		sequenceRecord = sequenceRecordIterator.next();
		
		// init
		final String contig = sequenceRecord.getSequenceName();
		final STRAND strand = isStranded ? STRAND.FORWARD : STRAND.UNKNOWN;
		currentCoordinate 	= new OneCoordinate(contig, 0, 0, strand);
	}

	@Override
	public boolean hasNext() {
		while (currentCoordinate == null) {
			if (! sequenceRecordIterator.hasNext()) {
				return false;
			}
			sequenceRecord 		= sequenceRecordIterator.next();
			final String contig = sequenceRecord.getSequenceName();
			final STRAND strand = isStranded ? STRAND.FORWARD : STRAND.UNKNOWN;
			currentCoordinate 	= searchNext(new OneCoordinate(contig, 0, 0, strand));
		}
		
		return true;
	}

	@Override
	public Coordinate next() {
		if (hasNext()) {
			final Coordinate tmpCoordinate = currentCoordinate.copy();
			currentCoordinate = searchNext(currentCoordinate);
			return tmpCoordinate;
		}

		return null;
	}

	@Override
	public void close() throws IOException {
		for (final List<SamReader> tmpList : readers) {
			for (final SamReader reader : tmpList) {
				reader.close();
			}	
		}
		readers.clear();
	}

	public int getTotal() {
		return total;
	}

	private Coordinate searchNext(Coordinate current) {
		final String contig 	= current.getContig();
		final int maxPosition 	= contig2sequenceRecord.get(contig).getSequenceLength() - 1;
		final int nextPosition	= current.getEnd() + 1;
		int newStart			= Integer.MAX_VALUE;
		
		for (int condition = 0; condition < parameter.getConditionsSize(); ++condition) {
			final int replicates = parameter.getConditionParameter(condition).getReplicateSize();
			for (int replicate = 0; replicate < replicates; ++replicate) {
				final SamReader reader = readers.get(condition).get(replicate);
				final ConditionParameter conditionParameter = parameter.getConditionParameter(condition);
				final int tmpNewStart = getNextCoveredPosition(
						conditionParameter, 
						contig, nextPosition, maxPosition, 
						reader);
				if (tmpNewStart <= nextPosition) {
					return getNextCoordinate(contig, nextPosition, maxPosition);					
				}
				newStart = Math.min(newStart, tmpNewStart);
			}
		}
		return getNextCoordinate(contig, newStart, maxPosition);
	}
	
	private Coordinate getNextCoordinate(final String contig, final int start, final int maxPosition) {
		if (start == SAMRecord.NO_ALIGNMENT_START || start == Integer.MAX_VALUE || start > maxPosition) {
			return null;
		}
		final int end 		= Math.min(start + parameter.getReservedWindowSize() - 1, maxPosition); 
		final STRAND strand = isStranded ? STRAND.FORWARD : STRAND.UNKNOWN;
		return new OneCoordinate(contig, start, end, strand);
	}
	
	private int getNextCoveredPosition(
			final ConditionParameter conditionParameter,
			final String contig, final int newPosition, final int maxPosition, 
			final SamReader reader) {

		final SAMRecordIterator it 	= reader.queryOverlapping(contig, newPosition, maxPosition);
		
		while (it.hasNext()) {
			final SAMRecord record = it.next();
			if (! conditionParameter.isValid(record)) {
				continue;
			}
			
			final int start = record.getAlignmentStart();
			if (start != SAMRecord.NO_ALIGNMENT_START) {
				it.close();
				return start;
			}
		}
		it.close();
		return SAMRecord.NO_ALIGNMENT_START;
	}

}