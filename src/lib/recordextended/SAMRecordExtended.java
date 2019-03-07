package lib.recordextended;

import java.util.ArrayList;
import java.util.List;

import htsjdk.samtools.CigarElement;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.util.StringUtil;

public class SAMRecordExtended {

	private final SAMRecord record;
	
	private final SamReader mateReader;
	private SAMRecordExtended mate;
	
	private final List<CigarElementExtended> cigarElementExtended;

	// indices to cigarElementExtended
	private final List<Integer> skipped;
	private final List<Integer> insertions;
	private final List<Integer> deletions;
	private final List<Integer> INDELs;
	
	private RecordReferenceProvider recordRefProvider;

	public SAMRecordExtended(final SAMRecord record) {
		this(record, null);
	}
	
	public SAMRecordExtended(final SAMRecord record, final SamReader mateReader) {
		this(record, null, mateReader);
	}
	
	private SAMRecordExtended(final SAMRecord record, final SAMRecordExtended mate, final SamReader mateReader) {
		this.record 	= record;
		this.mate		= mate;
		this.mateReader = mateReader; 
		
		cigarElementExtended = new ArrayList<CigarElementExtended>(record.getCigarLength());
		skipped 	= new ArrayList<Integer>(2);
		insertions 	= new ArrayList<Integer>(2);
		deletions 	= new ArrayList<Integer>(2);
		INDELs 		= new ArrayList<Integer>(4);
		
		process();
	}
	
	public SAMRecord getSAMRecord() {
		return record;
	}

	public SAMRecordExtended getMate() {
		if (! record.getReadPairedFlag()) {
			return null;
		}
		if (mate == null) {
			final SAMRecord mateRecord = mateReader.queryMate(record);
			mate = new SAMRecordExtended(mateRecord, this, mateReader);
		}
		return mate;
	}
	
	public RecordReferenceProvider getRecordReferenceProvider() {
		if (recordRefProvider == null) {
			recordRefProvider = new MDRecordReferenceProvider(this);
		}
		return recordRefProvider;
	}
	
	private void process() {
		final AlignedPosition position = new AlignedPosition(record.getAlignmentStart());

		int index = 0;
		
		// process CIGAR -> SNP, INDELs
		for (final CigarElement cigarElement : record.getCigar().getCigarElements()) {
			
			switch (cigarElement.getOperator()) {

			/*
			 * handle insertion
			 */
			case I:
				insertions.add(index);
				INDELs.add(index);
				break;
			
			/*
			 * handle deletion from the reference and introns
			 */
			case D:
				deletions.add(index);
				INDELs.add(index);
				break;
			
			/*
			 * handle deletion from the reference and introns
			 */
			case N:
				skipped.add(index);
				break;
				
			default:
				break;
			}
		
			cigarElementExtended.add(new CigarElementExtended(position.copy(), cigarElement));
			index = cigarElementExtended.size();
			position.advance(cigarElement);
		}
	}

	public List<CigarElementExtended> getCigarElementExtended() {
		return cigarElementExtended;
	}
	
	public List<Integer> getSkipped() {
		return skipped;
	}
	
	public List<Integer> getInsertion() {
		return insertions;
	}
	
	public List<Integer> getDeletion() {
		return deletions;
	}

	public List<Integer> getINDELs() {
		return INDELs;
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(StringUtil.bytesToString(record.getReadBases()));
		return sb.toString();
	}
	
	public int getUpstreamMatch(final int index) {
		if (index == 0) {
			return 0;
		}
		
		final CigarElementExtended upstream = cigarElementExtended.get(index - 1);
		if (! upstream.getCigarElement().getOperator().isAlignment()) {
			return 0;
		}
		
		return upstream.getCigarElement().getLength();
	}
	
	public int getDownstreamMatch(final int index) {
		if (index == cigarElementExtended.size() - 1) {
			return 0;
		}
		
		final CigarElementExtended downstream = cigarElementExtended.get(index + 1);
		if (! downstream.getCigarElement().getOperator().isAlignment()) {
			return 0;
		}
		
		return downstream.getCigarElement().getLength();
	}
	
	public class CigarElementExtended {
		
		private AlignedPosition position;
		private CigarElement cigarElement;
		
		public CigarElementExtended(final AlignedPosition position, final CigarElement cigarElement) {
			this.position 		= position;
			this.cigarElement 	= cigarElement;
		}
		
		public int getReferenceBlockLength() {
			return cigarElement.getOperator().consumesReferenceBases() ?
					cigarElement.getLength() : 0;
		}
		
		public int getReadBlockLength() {
			return cigarElement.getOperator().consumesReadBases() ?
					cigarElement.getLength() : 0;
		}

		public AlignedPosition getPosition() {
			return position;
		}
		
		public CigarElement getCigarElement() {
			return cigarElement;
		}
	
	}

	@Override
	public int hashCode() {
		return record.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		return record.equals(o);
	}
	
}
