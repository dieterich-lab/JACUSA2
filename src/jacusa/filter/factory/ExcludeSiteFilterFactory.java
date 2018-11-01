package jacusa.filter.factory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import htsjdk.variant.vcf.VCFCodec;
import jacusa.filter.AbstractFilter;
import jacusa.filter.factory.exclude.ContainedCoordinate;
import jacusa.filter.factory.exclude.DefaultContainedCoordinate;
import lib.cli.parameter.AbstractConditionParameter;
import lib.data.DataTypeContainer;
import lib.data.ParallelData;
import lib.data.DataTypeContainer.AbstractBuilder;
import lib.data.assembler.ConditionContainer;
import lib.data.cache.container.SharedCache;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.util.Util;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;

public class ExcludeSiteFilterFactory 
extends AbstractFilterFactory {

	private final Map<String, AbstractFeatureCodec<? extends Feature, LineIterator>> suffix2codec;
	
	private String filename;
	private AbstractFeatureCodec<? extends Feature, LineIterator> codec;
	
	public ExcludeSiteFilterFactory() {
		super(getOptionBuilder().build());

		suffix2codec = new HashMap<String, AbstractFeatureCodec<? extends Feature, LineIterator>>();

		// add default bed file format as implemented in htsjdk
		final BEDCodec bedCodec = new BEDCodec(StartOffset.ZERO);
		suffix2codec.put(BEDCodec.BED_EXTENSION, bedCodec);

		// add vcf 4 file format as implemented in htsjdk
		final VCFCodec vcfCodec = new VCFCodec();
		suffix2codec.put(IOUtil.VCF_FILE_EXTENSION, vcfCodec);
	}

	private AbstractFeatureCodec<? extends Feature, LineIterator> initBySuffix(final String filename) {
		for (final String suffix : suffix2codec.keySet()) {
			if (filename.endsWith(suffix)) {
				final AbstractFeatureCodec<?, LineIterator> tmpCodec = suffix2codec.get(suffix);
				if (tmpCodec.canDecode(filename)) {
					return tmpCodec;
				}
			}
		}

		return null;
	}
	
	private AbstractFeatureCodec<? extends Feature, LineIterator> initByBruteForce(final String filename) {
		for (final AbstractFeatureCodec<?, LineIterator> tmpCodec : suffix2codec.values()) {
			if (tmpCodec.canDecode(filename)) {
				return tmpCodec;
			}
		}

		return null;
	}
	
	@Override
	public void inidDataTypeContainer(AbstractBuilder builder) {
		// not needed
	}
	
	@Override
	protected Set<Option> processCLI(CommandLine cmd) {
		final Set<Option> processed = new HashSet<>();
		for (final Option option : cmd.getOptions()) {
			final String longOpt = option.getLongOpt();
			switch (longOpt) {
			case "file":
				final String tmpFilename = cmd.getOptionValue(longOpt);
				IOUtil.assertInputIsValid(tmpFilename);
				filename = tmpFilename;
				
				// first - try by suffix
				codec = initBySuffix(filename);
				if (codec == null) {
					codec = initByBruteForce(filename);
					if (codec == null) {
						throw new IllegalStateException("No matching codec can be found for: " + filename);
					}
				}
				processed.add(option);
				break;
				
			default:
				break;
			}
		}
		return processed;
	}

	@Override
	public Options getOptions() {
		final Options options = new Options();
		options.addOption(getCodecOptionBuilder().build());
		return options;
	}
	
	@Override
	protected AbstractFilter createFilter(
			CoordinateController coordinateController,
			ConditionContainer conditionContainer) {
		return new ExcludeSiteFilter(getC());
	}
	
	@Override
	public RecordWrapperDataCache createFilterCache(
			AbstractConditionParameter conditionParameter,
			SharedCache sharedCache) {
		return null;
	}
	
	public static Builder getOptionBuilder() {
		return Option.builder(Character.toString('E'))
				.desc("Exclude sites contained in file (VCF, BED, or JACUSA 2.x output");
	}

	public Builder getCodecOptionBuilder() {
		return Option.builder()
			.longOpt("file")
			.argName("FILE")
			.hasArg()
			.required()
			.desc("FILE that contains sites to be exclude from output. Supported file types: vcf, and bed");
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
			containedCoordinate = new DefaultContainedCoordinate(filename, codec);
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

}
