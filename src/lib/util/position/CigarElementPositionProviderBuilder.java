package lib.util.position;

import lib.record.AlignedPosition;
import lib.record.Record;
import lib.record.Record.CigarElementExtended;
import lib.util.coordinate.CoordinateTranslator;

import java.util.Arrays;

/**
 * TODO
 */
public class CigarElementPositionProviderBuilder implements lib.util.Builder<PositionProvider> {
	
	private final int cigarElementIndex;
	private final int offset;
	private final Record record;
	private CoordinateTranslator translator;
	
	/**
	 * 
	 * @param CigarElementExtended
	 * @param upDownStream
	 * @param record
	 * @param translator
	 */
	public CigarElementPositionProviderBuilder(
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
		final AlignedPosition position = record.getCigarElementExtended()
				.get(cigarElementIndex).getPosition();
		final int refPos 		= position.getRefPos() - length;
		final int readPos 		= position.getReadPos() - length;
		return build(refPos, readPos, length);
	}
	
	private PositionProvider buildDownstream(final int offset) {
		final CigarElementExtended cigarElementExtended = record.getCigarElementExtended()
				.get(cigarElementIndex);
		final AlignedPosition position = new AlignedPosition(record.getCigarElementExtended()
				.get(cigarElementIndex).getPosition());
		position.advance(cigarElementExtended.getCigarElement());
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
