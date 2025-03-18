package lib.util.position;

import lib.record.AlignedPosition;
import lib.record.Record;
import lib.record.Record.AlignedPositionCigarElement;
import lib.util.coordinate.CoordinateTranslator;

import java.util.Arrays;

/**
 * TODO add documentation
 */
public class CigarDetailPosProviderBuilder implements lib.util.Builder<PositionProvider> {
	
	private final int cigarElementIndex;
	private final int offset;
	private final Record record;
	private CoordinateTranslator translator;
	
	/**
	 * 
	 * @param AlignedPositionCigarElement
	 * @param upDownStream
	 * @param record
	 * @param translator
	 */
	public CigarDetailPosProviderBuilder(
			final int cigarElementIndex, 
			final int upDownStream,
			final Record record, 
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
		final int refPos 		= position.getRefPosition() - length;
		final int readPos 		= position.getReadPosition() - length;
		return build(refPos, readPos, length);
	}
	
	private PositionProvider buildDownstream(final int offset) {
		final AlignedPositionCigarElement cigarDetail = record.getCigarDetail()
				.get(cigarElementIndex);
		final AlignedPosition position = new AlignedPosition(record.getCigarDetail()
				.get(cigarElementIndex).getPosition());
		position.advance(cigarDetail.getCigarElement());
		final int downstreamMatch = record.getDownstreamMatch(cigarElementIndex);
		final int length 	= Math.min(downstreamMatch, offset);
		final int refPos 	= position.getRefPosition();
		final int readPos 	= position.getReadPosition();
		return build(refPos, readPos, length);
	}
	
	private PositionProvider build(final int refPos, final int readPos, int length) {
		// winPos -1 will be set in PositionProvider.adjustForWindow 
		final MatchPosition matchPosition = new MatchPosition(refPos, readPos, -1, record);
		length = PositionProvider.adjustWindowPos(matchPosition, length, translator);
		return new IntervalPosProvider(matchPosition, length);
	}
}
