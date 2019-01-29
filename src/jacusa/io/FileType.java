package jacusa.io;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import htsjdk.samtools.util.IOUtil;
import htsjdk.tribble.Feature;
import htsjdk.tribble.FeatureCodec;
import htsjdk.tribble.bed.BEDCodec;
import htsjdk.tribble.bed.BEDCodec.StartOffset;
import htsjdk.tribble.readers.LineIterator;
import htsjdk.variant.vcf.VCFCodec;
import lib.io.codec.JACUSA2codec;

public enum FileType {
	BED(
			"BED", 
			new HashSet<>(Arrays.asList(BEDCodec.BED_EXTENSION)), 
			new BEDCodec(StartOffset.ZERO)),
	
	VCF(
			"VCF", 
			new HashSet<>(Arrays.asList(IOUtil.VCF_FILE_EXTENSION,"vcf4")), 
			new VCFCodec()), 
	
	JACUSA("JACUSA2", 
			new HashSet<String>(), 
			new JACUSA2codec());
	
	private final String name;
	private final Set<String> suffix;
	private final FeatureCodec<? extends Feature, LineIterator> codec;
	
	private FileType(
			final String name, 
			final Set<String> suffix, 
			final FeatureCodec<? extends Feature, LineIterator> codec) {
		
		this.name 	= name;
		this.suffix = suffix;
		this.codec 	= codec;
	}
	
	public String getName() {
		return name;
	}
	
	public Set<String> getSuffix() {
		return suffix;
	}
	
	public FeatureCodec<? extends Feature, LineIterator> getCodec() {
		return codec;
	}
	
	public static FileType valueOfFileName(final String fileName) {
		for (final FileType fileType : FileType.values()) {
			for (final String suffix : fileType.getSuffix()) {
				if (fileName.endsWith(suffix)) {
					return fileType;
				}
			}
		}
		return null;
	}
	
}