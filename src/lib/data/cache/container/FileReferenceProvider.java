package lib.data.cache.container;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;

import lib.tmp.CoordinateController;
import lib.util.coordinate.Coordinate;

public class FileReferenceProvider implements ReferenceProvider {

	private final IndexedFastaSequenceFile indexedFastaSequenceFile;
	private final CoordinateController coordinateController;

	private byte[] reference;
	private Coordinate window;

	public FileReferenceProvider(final IndexedFastaSequenceFile indexedFastaSequenceFile, 
			final CoordinateController coordinateController) {

		this.indexedFastaSequenceFile = indexedFastaSequenceFile;
		this.coordinateController = coordinateController;
	}
	
	@Override
	public void update() {
		if (window == null || window != coordinateController.getActive()) {
			window = coordinateController.getActive();

			final String contig = coordinateController.getActive().getContig();
			final long start = coordinateController.getActive().getStart();
			final long end = coordinateController.getActive().getEnd();
			final ReferenceSequence refSeq = indexedFastaSequenceFile.getSubsequenceAt(contig, start, end);
			reference = refSeq.getBases();
		}
	}
	
	@Override
	public byte getReference(final Coordinate coordinate) {
		final int windowPosition = coordinateController.convert2windowPosition(coordinate);
		return getReference(windowPosition);
	}
	
	@Override
	public byte getReference(final int windowPosition) {
		return reference[windowPosition];
	}
	
}
