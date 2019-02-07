package test.utlis;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SAMSequenceRecord;

public final class ReferenceSequence {

	private static Map<String, String> CONTIG2REF_SEQ = new HashMap<String, String>(); 
	static {
		CONTIG2REF_SEQ.put("homopolymerTest", 			"ACGAACGT");
		CONTIG2REF_SEQ.put("processRecordTest", 		"ACGAACGT");
		CONTIG2REF_SEQ.put("BaseCallInterpreterTest", 	"ACGAACGT");
		CONTIG2REF_SEQ.put("PositionProviderTest", 		"ACGAACGT");
		// CONTIG2REF_SEQ.put("Contig3", 			"TODO");
		// add more sequences here 
	}
	
	private static SAMSequenceDictionary DICT 	= new SAMSequenceDictionary(
			CONTIG2REF_SEQ.keySet().stream()
			.map(name -> new SAMSequenceRecord(name, CONTIG2REF_SEQ.get(name).length()))
			.collect(Collectors.toList()) );
	
	public static SAMFileHeader HEADER 		= new SAMFileHeader(DICT);
	
	// don't create object
	private ReferenceSequence() {
		throw new AssertionError();
	}

	public static String getReferenceSequence(final String contig) {
		return CONTIG2REF_SEQ.get(contig);
	}
	
	public static Map<String, String> get() {
		return Collections.unmodifiableMap(CONTIG2REF_SEQ);
	}
}
