package jacusa.filter.factory;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option.Builder;

import htsjdk.samtools.util.IOUtil;
import htsjdk.tribble.Feature;
import htsjdk.tribble.FeatureCodec;
import htsjdk.tribble.readers.LineIterator;
import jacusa.filter.ExcludeSiteFilter;
import jacusa.filter.Filter;
import jacusa.io.FileType;
import lib.cli.parameter.ConditionParameter;
import lib.data.DataTypeContainer;
import lib.data.DataTypeContainer.AbstractBuilder;
import lib.data.assembler.ConditionContainer;
import lib.data.cache.container.SharedCache;
import lib.data.cache.record.RecordWrapperProcessor;
import lib.util.Util;
import lib.util.coordinate.CoordinateController;

// FIXME test, and finish ResultFeature.
/*
 * Currently, only the coordinates are read and 
 * the rest(e.g.: BaseCallCount, filters etc.) is ignored!
 * 
 */
/**
 * Tested in test.jacusa.filter.factory.ExcludeSiteFilterFactoryTest;
 */
public class ExcludeSiteFilterFactory extends AbstractFilterFactory {

	private String fileName;
	private FileType fileType;
	
	private FeatureCodec<? extends Feature, LineIterator> codec;
	
	public ExcludeSiteFilterFactory() {
		super(getOptionBuilder().build());
	}

	/* TODO implement auto
	private AbstractFeatureCodec<? extends Feature, LineIterator> initBySuffix(final String fileName) {
		final FileType fileType = FileType.valueOfFileName(fileName);
		if (fileType != null) {
			final AbstractFeatureCodec<?, LineIterator> tmpCodec = fileType.getCodec();
			if (tmpCodec.canDecode(fileName)) {
				return tmpCodec;
			}
		}
		
		return null;
	}
	*/
	
	public FeatureCodec<? extends Feature, LineIterator> initByBruteForce(final String filename) {
		for (final FileType fileType : FileType.values()) {
			final FeatureCodec<?, LineIterator> tmpCodec = fileType.getCodec();
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
				processed.add(option);
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
		
		/* TODO implement AUTO
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
			*/

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
	protected Filter createFilter(
			CoordinateController coordinateController,
			ConditionContainer conditionContainer) {
		
		return new ExcludeSiteFilter(getC(), fileName, codec);
	}
	
	@Override
	public RecordWrapperProcessor createFilterCache(
			ConditionParameter conditionParameter,
			SharedCache sharedCache) {
		return null;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public FileType getFileType() {
		return fileType;
	}
	
	public FeatureCodec<? extends Feature, LineIterator> getCodec() {
		return codec;
	}
	
	public static Builder getOptionBuilder() {
		return Option.builder(Character.toString('E'))
				.desc("Exclude sites contained in file (VCF, BED, or JACUSA 2.x output).");
	}

	public static Builder getFileNameOptionBuilder() {
		return Option.builder()
			.longOpt("file")
			.argName("FILE")
			.hasArg()
			.required()
			.desc("File that contains sites to be exclude from output. Supported file types: see type");
	}
	
	public static Builder getFileTypeOptionBuilder() {
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
			.required()
			.hasArg()
			.desc("File type: " +
					sb.toString() + ". Default: " + "TODO AUTO");
	}
	
	@Override
	public void addFilteredData(StringBuilder sb, DataTypeContainer data) {
		sb.append(Util.EMPTY_FIELD);	
	}
	
}
