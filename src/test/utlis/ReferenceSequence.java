package test.utlis;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SAMSequenceRecord;

public abstract class ReferenceSequence {

	private static Map<String, String> CONTIG2REF_SEQ = new HashMap<String, String>(); 
	static {
		CONTIG2REF_SEQ.put("contig1", "TODO");
		CONTIG2REF_SEQ.put("contig2", "TODO");
		CONTIG2REF_SEQ.put("Contig3", "TODO");
		CONTIG2REF_SEQ.put("Contig4", "TODO");
		CONTIG2REF_SEQ.put("Contig5", "TODO");
		// add more sequences here 
	}
	
	private static SAMSequenceDictionary DICT 	= new SAMSequenceDictionary(
			CONTIG2REF_SEQ.keySet().stream()
			.map(name -> new SAMSequenceRecord(name, CONTIG2REF_SEQ.get(name).length()))
			.collect(Collectors.toList()) );
	
	public static SAMFileHeader HEADER 		= new SAMFileHeader(DICT);
	
	// don't create object
	public ReferenceSequence() {
		throw new UnsupportedOperationException();
	}

	public static String getReferenceSequence(final String contig) {
		return CONTIG2REF_SEQ.get(contig);
	}
	
}
