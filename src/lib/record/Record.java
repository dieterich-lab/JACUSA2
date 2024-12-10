package lib.record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.util.StringUtil;

public class Record {

	private final SAMRecord samRecord;
	
	private final SamReader mateReader;
	private Record mate;
	
	private final List<CigarDetail> cigarDetail;

	// indices to cigarDetail
	private final List<Integer> skipped;
	private final List<Integer> insertions;
	private final List<Integer> deletions;
	private final List<Integer> INDELs;

	//Map<ReadPos, List<mod>>
	private Map<Integer, List<String>> mmValues;
	
	private RecordRefProvider recordRefProvider;

	public Record(final SAMRecord record) {
		this(record, null);
	}
	
	public Record(final SAMRecord record, final SamReader mateReader) {
		this(record, null, mateReader);
	}
	
	private Record(final SAMRecord samRecord, final Record mate, final SamReader mateReader) {
		this.samRecord 	= samRecord;
		this.mate		= mate;
		this.mateReader = mateReader; 
		
		cigarDetail = new ArrayList<>(samRecord.getCigarLength());
		skipped 	= new ArrayList<>(2);
		insertions 	= new ArrayList<>(2);
		deletions 	= new ArrayList<>(2);
		INDELs 		= new ArrayList<>(4);
		mmValues	= new HashMap<>(4);
		
		process();

		if(mate.getSAMRecord().hasAttribute("MM")){
			processMM();
		}
	}
	
	public SAMRecord getSAMRecord() {
		return samRecord;
	}

	public Record getMate() {
		if (! samRecord.getReadPairedFlag()) {
			return null;
		}
		if (mate == null) {
			final SAMRecord mateRecord = mateReader.queryMate(samRecord);
			mate = new Record(mateRecord, this, mateReader);
		}
		return mate;
	}
	
	public RecordRefProvider getRecordReferenceProvider() {
		if (recordRefProvider == null) {
			recordRefProvider = new MDRecordRefProvider(this);
		}
		return recordRefProvider;
	}
	
	private void process() {
		final AlignedPosition position = new AlignedPosition(samRecord.getAlignmentStart());

		int index = 0;
		
		// process CIGAR -> SNP, INDELs
		for (final CigarElement cigarElement : samRecord.getCigar().getCigarElements()) {
			
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
		
			cigarDetail.add(new CigarDetail(position.copy(), cigarElement));
			index = cigarDetail.size();
			position.advance(cigarElement);
		}

	}

	public void processMM(){

		//handle MM tag (modifications)
		String mmValue = (String) samRecord.getAttribute("MM");

		if(!mmValue.isEmpty()){
			//split whole MM tag value of eg "C+mh,2,4;A+123,3,6;..." into array at ";"
			String[] mmValueStrings = mmValue.split(";");

			//iterate through array of single mm value strings & process each string
			for(String mod:mmValueStrings){
				if(mod.isEmpty()){
					continue;
				}

				//split string at + or -
				int baseIndex = mod.indexOf('+');
				if(baseIndex == -1){
					baseIndex = mod.indexOf('-');
				}
				if(baseIndex == -1){
					continue;
				}

				String base = mod.substring(0,baseIndex);
				//currently ignoring the option of "[.?]"
				String modification = mod.substring(baseIndex+1,mod.indexOf(',')).replaceAll("[.?]","");
				String[] positions = mod.substring(mod.indexOf(',')+1).split(",");

				//if chembl code -> save whole code on position, if character code -> save characters separately
				List<String> splitMods = new ArrayList<>();
				if(modification.matches("[a-z]+")){
					for(char c : modification.toCharArray()){
						splitMods.add(String.valueOf(c));
					}
				} else if(modification.matches("[0-9]+")){
					splitMods.add(modification);
				}

				//save modification in Map
				int currentPos = 0;
				for(String relPos : positions){
					//calculate absolute position of modifications
					currentPos += Integer.parseInt(relPos);
					//if nothing saved at position, create new List as value; otherwise add modification to existing list
					mmValues.computeIfAbsent(currentPos, k -> new ArrayList<>())
							.addAll(splitMods);
				}

			}

		}
	}

	public List<CigarDetail> getCigarDetail() {
		return cigarDetail;
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

	public Map<Integer, List<String>> getMMValues(){
		return mmValues;
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(StringUtil.bytesToString(samRecord.getReadBases()));
		return sb.toString();
	}
	
	public int getUpstreamMatch(final int index) {
		if (index == 0) {
			return 0;
		}
		
		final CigarDetail upstream = cigarDetail.get(index - 1);
		if (! upstream.getCigarElement().getOperator().isAlignment()) {
			return 0;
		}
		
		return upstream.getCigarElement().getLength();
	}
	
	public int getDownstreamMatch(final int index) {
		if (index == cigarDetail.size() - 1) {
			return 0;
		}
		
		final CigarDetail downstream = cigarDetail.get(index + 1);
		if (! downstream.getCigarElement().getOperator().isAlignment()) {
			return 0;
		}
		
		return downstream.getCigarElement().getLength();
	}
	
	// TODO change name
	public class CigarDetail {
		
		private AlignedPosition position;
		private CigarElement cigarElement;
		
		public CigarDetail(final AlignedPosition position, final CigarElement cigarElement) {
			this.position 		= position;
			this.cigarElement 	= cigarElement;
		}
		
		public int getReferenceBlockLength() {
			return cigarElement.getOperator().consumesReferenceBases() ?
					cigarElement.getLength() : 0;
		}
		
		public int getNonSkippedMatches() {
			return cigarElement.getOperator() == CigarOperator.N ? 0 : getReferenceBlockLength();
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
			
		public String toString() {
			return new StringBuilder()
					.append(cigarElement.getOperator()).append(' ')
					.append(position.getRefPos()).append(' ')
					.append(position.getReadPos()).append(' ')
					.append(position.getNonSkippedMatches()).append(' ')
					.toString();
		}
		
	}

	@Override
	public int hashCode() {
		return samRecord.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		return samRecord.equals(o);
	}
	
}
