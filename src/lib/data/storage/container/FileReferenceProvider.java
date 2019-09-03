package lib.data.storage.container;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;
import lib.util.coordinate.CoordinateController;
import lib.record.Record;
import lib.util.Base;
import lib.util.Util;
import lib.util.coordinate.Coordinate;

/**
 * TODO
 */
public class FileReferenceProvider implements ReferenceProvider {

	private static final int READ_AHEAD = 10;
	
	private final IndexedFastaSequenceFile indexedFastaSequenceFile;
	private final CoordinateController coordinateController;

	private byte[] reference;
	private Coordinate window;

	private Map<Integer, Byte> referenceBaseBuffer;
	
	public FileReferenceProvider(final IndexedFastaSequenceFile indexedFastaSequenceFile, 
			final CoordinateController coordinateController) {

		this.indexedFastaSequenceFile 	= indexedFastaSequenceFile;
		this.coordinateController 		= coordinateController;

		referenceBaseBuffer = new HashMap<Integer, Byte>(Util.noRehashCapacity(READ_AHEAD));
	}
	
	@Override
	public void addrecord(Record record) {
		// nothing to be done here
	}
	
	@Override
	public void update() {
		if (window == null || window != coordinateController.getActive()) {
			window = coordinateController.getActive();

			final String contig 			= coordinateController.getActive().getContig();
			final int start 				= coordinateController.getActive().getStart();
			final int end 					= coordinateController.getActive().getEnd();
			final ReferenceSequence refSeq 	= indexedFastaSequenceFile
					.getSubsequenceAt(contig, start, end);
			reference 						= refSeq.getBases();
			
			if (referenceBaseBuffer.size() > 3 * READ_AHEAD) {
				referenceBaseBuffer = new HashMap<Integer, Byte>(Util.noRehashCapacity(READ_AHEAD));
			} else {
				referenceBaseBuffer.clear();
			}
		}
	}
	
	@Override
	public Base getReferenceBase(final Coordinate coordinate) {
		final int winPos = coordinateController.getCoordinateTranslator().coord2winPos(coordinate);
		if (winPos >= 0 ) {
			return getReferenceBase(winPos);
		}

		final int onePosition = coordinate.get1Position();
		if (! referenceBaseBuffer.containsKey(onePosition)) {
			final String contig = coordinate.getContig();

			final int length 	= indexedFastaSequenceFile.getSequenceDictionary().getSequence(contig).getSequenceLength();
			final int end 		= Math.min(length, onePosition + READ_AHEAD - 1);

			final ReferenceSequence refSeq = indexedFastaSequenceFile.getSubsequenceAt(contig, onePosition, end);
			for (int i = 0; end - onePosition >= 0; i++) {
				referenceBaseBuffer.put(onePosition + i, refSeq.getBases()[i]);
			}			
		}

		return Base.valueOf(referenceBaseBuffer.get(onePosition));

	}
	
	@Override
	public Base getReferenceBase(final int winPos) {
		return Base.valueOf(reference[winPos]);
	}

	@Override
	public CoordinateController getCoordinateController() {
		return coordinateController;
	}
	
	@Override
	public void close() {
		try {
			indexedFastaSequenceFile.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
