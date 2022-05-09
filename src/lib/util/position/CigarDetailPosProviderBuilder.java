package lib.util.position;

import lib.record.AlignedPosition;
import lib.record.ProcessedRecord;
import lib.record.ProcessedRecord.CigarDetail;
import lib.util.coordinate.CoordinateTranslator;

import java.util.Arrays;

/**
 * TODO
 */
public class CigarDetailPosProviderBuilder implements lib.util.Builder<PositionProvider> {
	
	private final int cigarElementIndex;
	private final int offset;
	private final ProcessedRecord record;
	private CoordinateTranslator translator;
	
	/**
	 * 
	 * @param CigarDetail
	 * @param upDownStream
	 * @param record
	 * @param translator
	 */
	public CigarDetailPosProviderBuilder(
			final int cigarElementIndex, 
			final int upDownStream,
			final ProcessedRecord record, 
			final CoordinateTranslator translator) {
		
		this.cigarElementIndex 	= cigarElementIndex;
		this.offset 			= upDownStream;
		this.record 			= record;
		this.translator 		= translator;
	}
	
	@Override
	public PositionProvider build() {
		final PositionProvider upStream 	= buildUpstream(Math.abs(offset));
		final PositionProvider downStream	= buildDownstream(offset);
		return new CombinedPositionProvider(Arrays.asList(upStream, downStream));
	}
	
	private PositionProvider buildUpstream(final int offset) {
		final int upstreamMatch = record.getUpstreamMatch(cigarElementIndex);
		final int length 		= Math.min(upstreamMatch, offset);
		final AlignedPosition position = record.getCigarDetail()
				.get(cigarElementIndex).getPosition();
		final int refPos 		= position.getRefPos() - length;
		final int readPos 		= position.getReadPos() - length;
		return build(refPos, readPos, length);
	}
	
	private PositionProvider buildDownstream(final int offset) {
		final CigarDetail cigarDetail = record.getCigarDetail()
				.get(cigarElementIndex);
		final AlignedPosition position = new AlignedPosition(record.getCigarDetail()
				.get(cigarElementIndex).getPosition());
		position.advance(cigarDetail.getCigarElement());
		final int downstreamMatch = record.getDownstreamMatch(cigarElementIndex);
		final int length 	= Math.min(downstreamMatch, offset);
		final int refPos 	= position.getRefPos();
		final int readPos 	= position.getReadPos();
		return build(refPos, readPos, length);
	}
	
	private PositionProvider build(final int refPos, final int readPos, int length) {
		// winPos -1 will be set in PositionProvider.adjustForWindow 
		final MatchPosition matchPosition = new MatchPosition(refPos, readPos, -1, record);
		length = PositionProvider.adjustWindowPos(matchPosition, length, translator);
		return new IntervalPosProvider(matchPosition, length);
	}
}
