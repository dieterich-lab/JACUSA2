package jacusa.filter.factory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option.Builder;

import htsjdk.samtools.util.IOUtil;
import htsjdk.tribble.AbstractFeatureCodec;
import htsjdk.tribble.Feature;
import htsjdk.tribble.bed.BEDCodec;
import htsjdk.tribble.bed.BEDCodec.StartOffset;
import htsjdk.tribble.readers.LineIterator;
import jacusa.filter.AbstractFilter;
import jacusa.filter.factory.exclude.ContainedCoordinate;
import jacusa.filter.factory.exclude.DefaultContainedCoordinate;
import lib.cli.parameter.AbstractConditionParameter;
import lib.data.DataTypeContainer;
import lib.data.ParallelData;
import lib.data.DataTypeContainer.AbstractBuilder;
import lib.data.assembler.ConditionContainer;
import lib.data.cache.container.SharedCache;
import lib.data.cache.record.RecordWrapperProcessor;
import lib.io.codec.JACUSA2codec;
import lib.util.Util;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;

// FIXME test, and finish ResultFeature.
/*
 * Currently, only the coordinates are read and 
 * the rest(e.g.: BaseCallCount, filters etc.) is ignored!
 * 
 */
public class ExcludeSiteFilterFactory 
extends AbstractFilterFactory {

	private String fileName;
	private FileType fileType;
	
	private AbstractFeatureCodec<? extends Feature, LineIterator> codec;
	
	public ExcludeSiteFilterFactory() {
		super(getOptionBuilder().build());

		fileType = FileType.AUTO;
	}

	private AbstractFeatureCodec<? extends Feature, LineIterator> initBySuffix(final String fileName) {
		for (final FileType fileType : FileType.values()) {
			for (final String suffix : fileType.getSuffix()) {
				if (fileName.endsWith(suffix)) {
					final AbstractFeatureCodec<?, LineIterator> tmpCodec = fileType.getCodec();
					if (tmpCodec.canDecode(fileName)) {
						return tmpCodec;
					}
				}
			}
		}

		return null;
	}
	
	private AbstractFeatureCodec<? extends Feature, LineIterator> initByBruteForce(final String filename) {
		for (final FileType fileType : FileType.values()) {
			final AbstractFeatureCodec<?, LineIterator> tmpCodec = fileType.getCodec();
			if (tmpCodec.canDecode(fileName)) {
				return tmpCodec;
			}
		}

		return null;
	}
	
	@Override
	public void initDataTypeContainer(AbstractBuilder builder) {
		// not needed
	}
	
	@Override
	protected Set<Option> processCLI(CommandLine cmd) {
		final Set<Option> processed = new HashSet<>();
		for (final Option option : cmd.getOptions()) {
			final String longOpt = option.getLongOpt();
			switch (longOpt) {
			case "file":
				final String tmpFileName = cmd.getOptionValue(longOpt);
				IOUtil.assertInputIsValid(tmpFileName);
				fileName = tmpFileName;

				break;

			case "type":
				final String tmpFileType = cmd.getOptionValue(longOpt);
				fileType = FileType.valueOf(tmpFileType);
				processed.add(option);
				break;
				
			default:
				break;
			}
		}

		switch (fileType) {
		
		case AUTO:
			// first - try by suffix
			codec = initBySuffix(fileName);
			if (codec == null) {
				codec = initByBruteForce(fileName);
				if (codec == null) {
					throw new IllegalStateException("No matching codec could be found for: " + fileName);
				}
			}	
			break;

		default:
			codec = fileType.getCodec();
			break;
		}
		
		return processed;
	}

	@Override
	public Options getOptions() {
		final Options options = new Options();
		options.addOption(getFileNameOptionBuilder().build());
		options.addOption(getFileTypeOptionBuilder().build());
		return options;
	}
	
	@Override
	protected AbstractFilter createFilter(
			CoordinateController coordinateController,
			ConditionContainer conditionContainer) {
		return new ExcludeSiteFilter(getC());
	}
	
	@Override
	public RecordWrapperProcessor createFilterCache(
			AbstractConditionParameter conditionParameter,
			SharedCache sharedCache) {
		return null;
	}
	
	public static Builder getOptionBuilder() {
		return Option.builder(Character.toString('E'))
				.desc("Exclude sites contained in file (VCF, BED, or JACUSA 2.x output");
	}

	public Builder getFileNameOptionBuilder() {
		return Option.builder()
			.longOpt("file")
			.argName("FILE")
			.hasArg()
			.required()
			.desc("FILE that contains sites to be exclude from output. Supported file types: see type");
	}
	
	public Builder getFileTypeOptionBuilder() {
		final StringBuilder sb = new StringBuilder();
		for (final FileType fileType : FileType.values()) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(fileType.getName());
		}
		return Option.builder()
			.longOpt("type")
			.argName("TYPE")
			.hasArg()
			.required()
			.desc("TYPE of FILE to be exclude from output. Supported file types: \n" +
					sb.toString() + "\n" + 
					"Default: " + FileType.AUTO.getName());
	}
	
	@Override
	public void addFilteredData(StringBuilder sb, DataTypeContainer data) {
		sb.append(Util.EMPTY_FIELD);	
	}
	
	/**
	 * TODO add comments. 
	 */
	private class ExcludeSiteFilter 
	extends AbstractFilter {

		private final ContainedCoordinate containedCoordinate; 
		
		public ExcludeSiteFilter(final char c) {
			super(c);
			containedCoordinate = new DefaultContainedCoordinate(fileName, codec);
		}

		@Override
		public boolean filter(final ParallelData parallelData) {
			final Coordinate coordinate = parallelData.getCoordinate();
			return containedCoordinate.isContained(coordinate);
		}

		@Override
		public int getOverhang() { 
			return 0; 
		}

	}

	private enum FileType {
		AUTO(
				"auto", 
				new HashSet<>(),
				null),
		BED(
				"BED", 
				new HashSet<>(Arrays.asList(
						BEDCodec.BED_EXTENSION)),
				new BEDCodec(StartOffset.ZERO)),
		VCF(
				"VCF", 
				new HashSet<>(Arrays.asList(
						IOUtil.VCF_FILE_EXTENSION,
						"vcf4")),
				new BEDCodec(StartOffset.ZERO)),
		JACUSA2(
				"JACUSA2", 
				new HashSet<>(),
				new JACUSA2codec());
		
		private final String name;
		private final Set<String> suffix;
		private final AbstractFeatureCodec<? extends Feature, LineIterator> codec;
		
		private FileType(
				final String name, 
				final Set<String> suffix, 
				final AbstractFeatureCodec<? extends Feature, LineIterator> codec) {
			
			this.name = name;
			this.suffix = suffix;
			this.codec = codec;
		}
		
		public String getName() {
			return name;
		}
		
		public Set<String> getSuffix() {
			return suffix;
		}
		
		public AbstractFeatureCodec<? extends Feature, LineIterator> getCodec() {
			return codec;
		}

	}
	
}
