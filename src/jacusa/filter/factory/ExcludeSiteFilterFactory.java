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

/**
 * Currently, only the coordinates are read and 
 * the rest(e.g.: BaseCallCount, filters etc.) is ignored!
 * TODO test, and finish ResultFeature.
 */
public class ExcludeSiteFilterFactory
extends AbstractFilterFactory 
implements HasFileName, HasFileType {

	private final static char FILTER = 'E';
	
	private String fileName;
	private FileType fileType;
	
	public ExcludeSiteFilterFactory() {
		super(getOptionBuilder().build());
		getACOption().add(new FileNameOption(this));
		getACOption().add(new FileTypeOption(this));
	}

	/* TODO implement auto
	 * This method tries to identify the file type by the suffix of the filename
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
	
	/* 
	 * This method tries to identify the file type by brute force trying all available codecs
	public FeatureCodec<? extends Feature, LineIterator> initByBruteForce(final String fileName) {
		final FileType fileType = FileType.valueOfFileContent(fileName);
		if (fileType == null) {
			return null;
		}
		
		return fileType.getCodec();
	}
	*/
	
	@Override
	public void initDataContainer(AbstractBuilder builder) {
		// not needed
	}
	
	@Override
	public Filter createFilter(
			CoordinateController coordinateController,
			ConditionContainer conditionContainer) {
		
		return new ExcludeSiteFilter(getC(), fileName, getCodec());
	}
	
	@Override
	public Cache createFilterCache(
			ConditionParameter conditionParameter,
			SharedStorage sharedStorage) {
		// now cache needed
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
		return fileType.getCodec();
	}
	
	public static Builder getOptionBuilder() {
		return Option.builder(Character.toString(FILTER))
				.desc("Exclude sites contained in file (VCF, BED, or JACUSA 2.x output).");
	}
	
	@Override
	public void addFilteredData(StringBuilder sb, DataContainer data) {
		sb.append(InputOutput.EMPTY_FIELD);	
	}
	
}
