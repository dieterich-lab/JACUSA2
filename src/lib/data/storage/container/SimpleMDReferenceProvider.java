package lib.data.storage.container;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.util.SequenceUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lib.cli.parameter.ConditionParameter;
import lib.record.ProcessedRecord;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.AllAlignmentBlocksPosProvider;
import lib.util.position.Position;
import lib.util.Base;
import lib.util.Util;
import lib.util.coordinate.Coordinate;

/**
 * TODO
 */
public class SimpleMDReferenceProvider implements ReferenceProvider {

	private static final int READ_AHEAD = 10;
	
	private final CoordinateController coordinateController;
	
	private final byte[] winPos2refBase;

	private final List<SamReader> samReaders;
	
	private Map<Integer, Byte> refPos2refBase;
	
	public SimpleMDReferenceProvider(
			final CoordinateController coordinateController,
			final List<String> recordFilenames) {

		this.coordinateController = coordinateController;		

		winPos2refBase = new byte[coordinateController.getActiveWindowSize()];
		Arrays.fill(winPos2refBase, (byte)'N');

		samReaders = createSamReaders(recordFilenames);

		refPos2refBase = new HashMap<>(Util.noRehashCapacity(READ_AHEAD));
	}

	private CoordinateTranslator getTranslator() {
		return coordinateController.getCoordinateTranslator();
	}

	@Override
	public void addrecord(final ProcessedRecord record) {
		final AllAlignmentBlocksPosProvider positionProvider = 
				new AllAlignmentBlocksPosProvider(record, getTranslator());
		
		while (positionProvider.hasNext()) {
			final Position position = positionProvider.next();
			winPos2refBase[position.getWindowPosition()] = record
					.getRecordReferenceProvider()
					.getRefBase(position.getReferencePosition(), position.getReadPosition())
					.getByte();
		}
	}

	@Override
	public void update() {
		Arrays.fill(winPos2refBase, (byte)'N');

		if (refPos2refBase.size() > 3 * READ_AHEAD) {
			refPos2refBase = new HashMap<>(Util.noRehashCapacity(READ_AHEAD));
		} else {
			refPos2refBase.clear();
		}
	}
	
	@Override
	public Base getReferenceBase(final Coordinate coordinate) {
		final int winPos = coordinateController.getCoordinateTranslator().coord2winPos(coordinate);
		if (winPos >= 0) {
			return getReferenceBase(winPos);
		}
		
		final int onePosition = coordinate.get1Position();
		if (! refPos2refBase.containsKey(onePosition)) {
			for (SamReader samReader : samReaders) {
				addReference(samReader, coordinate, READ_AHEAD);
				if (refPos2refBase.containsKey(onePosition) && 
						SequenceUtil.isValidBase(refPos2refBase.get(onePosition))) {
					return Base.valueOf(refPos2refBase.get(onePosition));
				}
			}

			// FALLBACK - just return N
			refPos2refBase.put(onePosition, Base.N.getByte());
		}

		return Base.valueOf(refPos2refBase.get(onePosition));
	}

	private void addReference( 
			final SamReader samReader, 
			final Coordinate coordinate, 
			final int readAhead) {

		final String contig = coordinate.getContig();
		final int start 	= coordinate.get1Position();
		final int end 	   	= start + readAhead - 1;
		final Set<Integer>  visited = new HashSet<>(readAhead + end - start + 1);
		int covered 		= 0;
		
		final SAMRecordIterator it = samReader.query(contig, start, end, false);
		while (it.hasNext()) {
			final SAMRecord samRecord = it.next();
			final ProcessedRecord record = new ProcessedRecord(samRecord);
			final AllAlignmentBlocksPosProvider positionProvider = 
					new AllAlignmentBlocksPosProvider(record, getTranslator());
			
			while (positionProvider.hasNext() && covered < end - start + 1) {
				final Position position = positionProvider.next();
				final int refPos = position.getReferencePosition();
				if (visited.contains(refPos)) {
					continue;
				}
				visited.add(refPos);
				if (refPos >= start && refPos <= end) {
					covered++;
				}
				if (! position.isWithinWindow()) {
					refPos2refBase.put(
							refPos, 
							record
								.getRecordReferenceProvider()
								.getRefBase(position.getReferencePosition(), position.getReadPosition())
								.getByte() );					
				} else {
					winPos2refBase[position.getWindowPosition()] = record
							.getRecordReferenceProvider()
							.getRefBase(position.getReferencePosition(), position.getReadPosition())
							.getByte();
				}
			}
		}
		it.close();
	}
	
	private final List<SamReader> createSamReaders(final List<String> recordFilenames) {
		return recordFilenames.stream()
			.map(ConditionParameter::createSamReader)
			.collect(Collectors.toList());
	}
	
	@Override
	public Base getReferenceBase(final int winPos) {
		return Base.valueOf(winPos2refBase[winPos]);
	}

	@Override
	public CoordinateController getCoordinateController() {
		return coordinateController;
	}
	
	public void close() {
		for (final SamReader samReader : samReaders) {
			try {
				samReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		samReaders.clear();
	}
	
}
