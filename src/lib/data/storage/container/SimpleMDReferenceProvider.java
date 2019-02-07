package lib.data.storage.container;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.util.SequenceUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lib.cli.parameter.ConditionParameter;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.AllAlignmentBlocksPositionProvider;
import lib.util.position.Position;
import lib.recordextended.SAMRecordExtended;
import lib.util.Base;
import lib.util.coordinate.Coordinate;

public class SimpleMDReferenceProvider implements ReferenceProvider {

	private static final int READ_AHEAD = 10;
	
	private final CoordinateController coordinateController;
	
	private final byte[] reference;
	private Coordinate window;

	private final List<SamReader> samReaders;
	
	private Map<Integer, Byte> referenceBaseBuffer;
	
	public SimpleMDReferenceProvider(
			final CoordinateController coordinateController,
			final List<String> recordFilenames) {

		this.coordinateController = coordinateController;		

		reference = new byte[coordinateController.getActiveWindowSize()];
		Arrays.fill(reference, (byte)'N');

		samReaders = createSamReaders(recordFilenames);

		referenceBaseBuffer = new HashMap<Integer, Byte>(READ_AHEAD);
	}

	private CoordinateTranslator getTranslator() {
		return coordinateController.getCoordinateTranslator();
	}
	
	public void addRecordExtended(final SAMRecordExtended recordExtended) {
		final AllAlignmentBlocksPositionProvider positionProvider = 
				new AllAlignmentBlocksPositionProvider(recordExtended, getTranslator());
		
		while (positionProvider.hasNext()) {
			final Position position = positionProvider.next();
			reference[position.getWindowPosition()] = 
					recordExtended
						.getRecordReferenceProvider()
						.getReferenceBase(position.getReferencePosition())
						.getByte();
		}
		
		/* TODO remove all code
		for (final AlignmentBlock block : recordExtended.getSAMRecord().getAlignmentBlocks()) {
			final int refStart = block.getReferenceStart();
			final int readStart = block.getReadStart() - 1;
			final int length = block.getLength();
			
			final WindowPositionGuard windowPositionGuard = coordinateController.convert(refStart, readStart, length);
			if (windowPositionGuard.isValid()) {
				for (int i = 0; i < windowPositionGuard.getLength(); ++i) {
					final int refPos = windowPositionGuard.getReferencePosition() + i;
					reference[windowPositionGuard.getWindowPosition() + i] = 
							recordExtended
								.getRecordReferenceProvider()
								.getReferenceBase(refPos)
								.getByte();
				}
			}
		}
		*/
	}

	@Override
	public void update() {
		if (window == null || window != coordinateController.getActive()) {
			window = coordinateController.getActive();
			Arrays.fill(reference, (byte)'N');

			if (referenceBaseBuffer.size() > 3 * READ_AHEAD) {
				referenceBaseBuffer = new HashMap<Integer, Byte>(READ_AHEAD);
			} else {
				referenceBaseBuffer.clear();
			}
		}
	}
	
	@Override
	public Base getReferenceBase(final Coordinate coordinate) {
		final int winPos = coordinateController.getCoordinateTranslator().coordinate2windowPosition(coordinate);
		if (winPos >= 0) {
			return getReferenceBase(winPos);
		}
		
		final int onePosition = coordinate.get1Position();
		if (! referenceBaseBuffer.containsKey(onePosition)) {
			for (SamReader samReader : samReaders) {
				addReference(referenceBaseBuffer, samReader, coordinate, READ_AHEAD);
				if (referenceBaseBuffer.containsKey(onePosition) && 
						SequenceUtil.isValidBase(referenceBaseBuffer.get(onePosition))) {
					return Base.valueOf(referenceBaseBuffer.get(onePosition));
				}
			}

			// FALLBACK - just return N
			referenceBaseBuffer.put(onePosition, Base.N.getByte());
		}

		return Base.valueOf(referenceBaseBuffer.get(onePosition));
	}

	private void addReference(final Map<Integer, Byte> ref2base, 
			final SamReader samReader, 
			final Coordinate coordinate, 
			final int readAhead) {

		final String contig = coordinate.getContig();
		final int start 	= coordinate.get1Position();
		final int end 	   	= start + readAhead - 1;
		final SAMRecordIterator it = samReader.query(contig, start, end, false);
		while (it.hasNext()) {
			final SAMRecord record = it.next();
			final SAMRecordExtended recordExtended = new SAMRecordExtended(record);
			
			for (final AlignmentBlock block : record.getAlignmentBlocks()) {
				final int refStart = block.getReferenceStart();
				for (int i = 0; i < block.getLength(); ++i) {
					final int refPos = refStart + 0;
					if (refPos > end) {
						it.close();
						return;
					}
					if (refPos >= start) {
						ref2base.put(
								refPos, 
								recordExtended
									.getRecordReferenceProvider()
									.getReferenceBase(refPos)
									.getByte() );
					}	
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
		return Base.valueOf(reference[winPos]);
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
