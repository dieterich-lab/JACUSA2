package lib.util.position;

import lib.util.coordinate.CoordinateTranslator;

import java.util.Arrays;


import lib.recordextended.AlignedPosition;
import lib.recordextended.SAMRecordExtended;
import lib.recordextended.SAMRecordExtended.CigarElementExtended;

public class CigarElementExtendedPositionProviderBuilder implements lib.util.Builder<PositionProvider> {
	
	private final int cigarElementExtendedIndex;
	private final int offset;
	private final SAMRecordExtended recordExtended;
	private CoordinateTranslator translator;
	
	/**
	 * 
	 * @param CigarElementExtended
	 * @param upDownStream
	 * @param recordExtended
	 * @param translator
	 */
	public CigarElementExtendedPositionProviderBuilder(
			final int cigarElementExtendedIndex, 
			final int upDownStream,
			final SAMRecordExtended recordExtended, 
			final CoordinateTranslator translator) {
		
		this.cigarElementExtendedIndex 	= cigarElementExtendedIndex;
		this.offset 					= upDownStream;
		this.recordExtended 			= recordExtended;
		this.translator 				= translator;
	}
	
	@Override
	public PositionProvider build() {
		return new CombinedPositionProvider(
				Arrays.asList(
						buildUpstream(cigarElementExtendedIndex, Math.abs(offset)),
						buildDownstream(cigarElementExtendedIndex, offset)) );
	}
	
	private PositionProvider buildUpstream(final int cigarEEIndex, final int offset) {
		final int upstreamMatch = recordExtended.getUpstreamMatch(cigarElementExtendedIndex);
		final int length 		= Math.min(upstreamMatch, offset);
		final AlignedPosition position = recordExtended.getCigarElementExtended()
				.get(cigarElementExtendedIndex).getPosition();
		final int refPos 		= position.getReferencePosition() - length;
		final int readPos 		= position.getReadPosition() - length;
		return build(refPos, readPos, length);
	}
	
	private PositionProvider buildDownstream(final int cigarEEIndex, final int offset) {
		final CigarElementExtended cigarElementExtended = recordExtended.getCigarElementExtended()
				.get(cigarElementExtendedIndex);
		final AlignedPosition position = recordExtended.getCigarElementExtended()
				.get(cigarElementExtendedIndex).getPosition();
		position.advance(cigarElementExtended.getCigarElement());
		final int downstreamMatch = recordExtended.getDownstreamMatch(cigarElementExtendedIndex);
		final int length 	= Math.min(downstreamMatch, offset);
		final int refPos 	= position.getReferencePosition();
		final int readPos 	= position.getReadPosition();
		return build(refPos, readPos, length);
	}
	
	private PositionProvider build(final int refPos, final int readPos, int length) {
		// winPos -1 will be set in PositionProvider.adjustForWindow 
		final MatchPosition matchPosition = new MatchPosition(refPos, readPos, -1, recordExtended);
		length = PositionProvider.adjustWindowPos(matchPosition, length, translator);
		return new IntervalPositionProvider(matchPosition, length);
	}
}
