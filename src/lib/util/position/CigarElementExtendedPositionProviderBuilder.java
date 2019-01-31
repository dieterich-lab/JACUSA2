package lib.util.position;

import lib.util.coordinate.CoordinateTranslator;

import java.util.Arrays;

import lib.recordextended.CombinedPosition;
import lib.recordextended.SAMRecordExtended;

public class CigarElementExtendedPositionProviderBuilder implements lib.util.Builder<PositionProvider> {
	
	private final int cigarElementExtendedIndex;
	private final int offset;
	private final SAMRecordExtended recordExtended;
	private CoordinateTranslator translator;
	
	private final CombinedPosition position;

	/**
	 * 
	 * @param CigarElementExtended
	 * @param offset
	 * @param recordExtended
	 * @param translator
	 */
	public CigarElementExtendedPositionProviderBuilder(
			final int cigarElementExtendedIndex, 
			final int offset,
			final SAMRecordExtended recordExtended, 
			final CoordinateTranslator translator) {
		
		this.cigarElementExtendedIndex 	= cigarElementExtendedIndex;
		this.offset 					= offset;
		this.recordExtended 			= recordExtended;
		this.translator 				= translator;
		
		position 						= recordExtended
				.getCigarElementExtended().get(cigarElementExtendedIndex)
				.getPosition();
	}
	
	@Override
	public PositionProvider build() {
		final int upstreamMatch 		= recordExtended.getUpstreamMatch(cigarElementExtendedIndex);
		final int downstreamMatch 		= recordExtended.getUpstreamMatch(cigarElementExtendedIndex);
		final int cigarElementLength 	= recordExtended.getCigarElementExtended()
					.get(cigarElementExtendedIndex).getCigarElement().getLength();
				
		return new CombinedPositionProvider(
				Arrays.asList(
						buildUpstream(upstreamMatch, Math.abs(offset)),
						buildDownstream(downstreamMatch, offset, cigarElementLength)));
	}
	
	private PositionProvider buildUpstream(final int upstreamMatch, final int offset) {
		int length 			= Math.min(upstreamMatch, offset);
		final int refPos 	= position.getReferencePosition() - upstreamMatch;
		final int readPos 	= position.getReadPosition() - upstreamMatch;
		return build(refPos, readPos, length);
	}
	
	private PositionProvider buildDownstream(
			final int downstreamMatch, final int offset, final int cigarElementLength) {
		
		int length 			= Math.min(downstreamMatch, offset);
		final int refPos 	= position.getReferencePosition() - cigarElementLength;
		final int readPos 	= position.getReadPosition() - cigarElementLength;
		return build(refPos, readPos, length);
	}
	
	private PositionProvider build(final int refPos, final int readPos, final int length) {
		// winPos -1 will be set in PositionProvider.adjustForWindow 
		final MatchPosition matchPosition = new MatchPosition(refPos, readPos, -1, recordExtended);
		PositionProvider.adjustForWindow(matchPosition, length, translator);
		return new AlignedPositionProvider(matchPosition, length);
	}
}
