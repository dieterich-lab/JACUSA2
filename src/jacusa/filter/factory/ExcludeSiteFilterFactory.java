package jacusa.filter.factory;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;

import htsjdk.tribble.Feature;
import htsjdk.tribble.FeatureCodec;
import htsjdk.tribble.readers.LineIterator;
import jacusa.filter.ExcludeSiteFilter;
import jacusa.filter.Filter;
import jacusa.io.FileType;
import lib.cli.options.filter.FileNameOption;
import lib.cli.options.filter.FileTypeOption;
import lib.cli.options.filter.has.HasFileName;
import lib.cli.options.filter.has.HasFileType;
import lib.cli.parameter.ConditionParameter;
import lib.data.DataContainer;
import lib.data.DataContainer.AbstractBuilder;
import lib.data.storage.Cache;
import lib.data.storage.container.SharedStorage;
import lib.io.InputOutput;
import lib.util.ConditionContainer;
import lib.util.coordinate.CoordinateController;

// FIXME test, and finish ResultFeature.
/*
 * Currently, only the coordinates are read and 
 * the rest(e.g.: BaseCallCount, filters etc.) is ignored!
 * 
 */
public class ExcludeSiteFilterFactory
extends AbstractFilterFactory 
implements HasFileName, HasFileType {

	private String fileName;
	private FileType fileType;
	
	private FeatureCodec<? extends Feature, LineIterator> codec;
	
	public ExcludeSiteFilterFactory() {
		super(getOptionBuilder().build());
		getACOption().add(new FileNameOption(this));
		getACOption().add(new FileTypeOption(this));
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
	
	public FeatureCodec<? extends Feature, LineIterator> initByBruteForce(final String fileName) {
		final FileType fileType = FileType.valueOfFileContent(fileName);
		if (fileType == null) {
			return null;
		}
		
		return fileType.getCodec();
	}
	
	@Override
	public void initDataContainer(AbstractBuilder builder) {
		// not needed
	}
	
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

	default:
		codec = fileType.getCodec();
		break;
	}
			*/
	
	@Override
	public Filter createFilter(
			CoordinateController coordinateController,
			ConditionContainer conditionContainer) {
		
		return new ExcludeSiteFilter(getC(), fileName, codec);
	}
	
	@Override
	public Cache createFilterCache(
			ConditionParameter conditionParameter,
			SharedStorage sharedStorage) {
		return null;
	}
	
	@Override
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	@Override
	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}
	
	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
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
	
	@Override
	public void addFilteredData(StringBuilder sb, DataContainer data) {
		sb.append(InputOutput.EMPTY_FIELD);	
	}
	
}
