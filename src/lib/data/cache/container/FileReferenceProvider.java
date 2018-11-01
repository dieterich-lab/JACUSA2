package lib.data.cache.container;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;

import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.util.coordinate.CoordinateController;
import lib.util.Base;
import lib.util.coordinate.Coordinate;

public class FileReferenceProvider implements ReferenceProvider {

	private static final int READ_AHEAD = 10;
	
	private final IndexedFastaSequenceFile indexedFastaSequenceFile;
	private final CoordinateController coordinateController;

	private byte[] reference;
	private Coordinate window;

	private Map<Integer, Byte> referenceBaseBuffer;
	
	public FileReferenceProvider(final IndexedFastaSequenceFile indexedFastaSequenceFile, 
			final CoordinateController coordinateController) {

		this.indexedFastaSequenceFile = indexedFastaSequenceFile;
		this.coordinateController = coordinateController;

		referenceBaseBuffer = new HashMap<Integer, Byte>(READ_AHEAD);
	}
	
	@Override
	public void addRecordWrapper(SAMRecordWrapper recordWrapper) {
		// nothing to be done here
	}
	
	@Override
	public void update() {
		if (window == null || window != coordinateController.getActive()) {
			window = coordinateController.getActive();

			final String contig = coordinateController.getActive().getContig();
			final int start = coordinateController.getActive().getStart();
			final int end = coordinateController.getActive().getEnd();
			final ReferenceSequence refSeq = indexedFastaSequenceFile.getSubsequenceAt(contig, start, end);
			reference = refSeq.getBases();
			
			if (referenceBaseBuffer.size() > 3 * READ_AHEAD) {
				referenceBaseBuffer = new HashMap<Integer, Byte>(READ_AHEAD);
			} else {
				referenceBaseBuffer.clear();
			}
		}
	}
	
	@Override
	public Base getReferenceBase(final Coordinate coordinate) {
		final int windowPosition = coordinateController.getCoordinateTranslator().convert2windowPosition(coordinate);
		if (windowPosition >= 0 ) {
			return getReferenceBase(windowPosition);
		}

		final int position = coordinate.getPosition();
		if (! referenceBaseBuffer.containsKey(position)) {
			final String contig = coordinate.getContig();

			final int length 	= indexedFastaSequenceFile.getSequenceDictionary().getSequence(contig).getSequenceLength();
			final int end 		= Math.min(length, position + READ_AHEAD - 1);

			final ReferenceSequence refSeq = indexedFastaSequenceFile.getSubsequenceAt(contig, position, end);
			for (int i = 0; end - position >= 0; i++) {
				referenceBaseBuffer.put(position + i, refSeq.getBases()[i]);
			}			
		}

		return Base.valueOf(referenceBaseBuffer.get(position));

	}
	
	@Override
	public Base getReferenceBase(final int windowPosition) {
		return Base.valueOf(reference[windowPosition]);
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
