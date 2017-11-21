package lib.cli.parameters;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMValidationError;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.SamReaderFactory.Option;
import htsjdk.samtools.ValidationStringency;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lib.cli.options.condition.filter.samtag.MaxValueSamTagFilter;
import lib.data.AbstractData;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;
import lib.util.AbstractTool;

public abstract class AbstractConditionParameter<T extends AbstractData> {

	private LIBRARY_TYPE libraryType;
	
	// cache related
	private int maxDepth;
	
	private byte minBASQ;
	private int minMAPQ;

	private int minCoverage;
	
	// filter: flags
	private int filterFlags;
	private int retainFlags;

	// filter based on SAM tags
	private List<MaxValueSamTagFilter> samTagFilters;
	
	// path to BAM files
	private String[] recordFilenames;
	
	public AbstractConditionParameter() {
		libraryType = LIBRARY_TYPE.UNSTRANDED;

		maxDepth 		= -1;
		
		minBASQ			= Byte.parseByte("20");
		minMAPQ 		= 20;
		
		minCoverage 	= 5;
		
		filterFlags 	= 0;
		retainFlags	 	= 0;
		
		samTagFilters 	= new ArrayList<MaxValueSamTagFilter>();
		
		recordFilenames = new String[0];
	}
	
	/**
	 * @return the maxDepth
	 */
	public int getMaxDepth() {
		return maxDepth;
	}

	/**
	 * @param maxDepth the maxDepth to set
	 */
	public void setMaxDepth(final int maxDepth) {
		this.maxDepth = maxDepth;
	}
	
	/**
	 * @return the minBASQ
	 */
	public byte getMinBASQ() {
		return minBASQ;
	}

	/**
	 * @param minBASQ the minBASQ to set
	 */
	public void setMinBASQ(final byte minBASQ) {
		this.minBASQ = minBASQ;
	}

	/**
	 * @return the minMAPQ
	 */
	public int getMinMAPQ() {
		return minMAPQ;
	}
	
	/**
	 * @param minMAPQ the minMAPQ to set
	 */
	public void setMinMAPQ(final int minMAPQ) {
		this.minMAPQ = minMAPQ;
	}

	/**
	 * @return the minCoverage
	 */
	public int getMinCoverage() {
		return minCoverage;
	}

	/**
	 * @param minCoverage the minCoverage to set
	 */
	public void setMinCoverage(final int minCoverage) {
		this.minCoverage = minCoverage;
	}
	
	/**
	 * @return the filterFlags
	 */
	public int getFilterFlags() {
		return filterFlags;
	}

	/**
	 * @param filterFlags the filterFlags to set
	 */
	public void setFilterFlags(final int filterFlags) {
		this.filterFlags = filterFlags;
	}

	/**
	 * @return the retainFlags
	 */
	public int getRetainFlags() {
		return retainFlags;
	}

	/**
	 * @param retainFlags the retainFlags to set
	 */
	public void setRetainFlags(final int retainFlags) {
		this.retainFlags = retainFlags;
	}
	
	/**
	 * @return the samTagFilters
	 */
	public List<MaxValueSamTagFilter> getSamTagFilters() {
		return samTagFilters;
	}

	/**
	 * @param samTagFilters the samTagFilters to set
	 */
	public void setSamTagFilters(final List<MaxValueSamTagFilter> samTagFilters) {
		this.samTagFilters = samTagFilters;
	}

	/**
	 * @return the recordFilenames
	 */
	public String[] getRecordFilenames() {
		return recordFilenames;
	}
	
	/**
	 * 
	 * @param recordFilenames
	 */
	public void setRecordFilenames(final String[] recordFilenames) {
		this.recordFilenames = recordFilenames;
	}
	
	public int getReplicateSize() {
		return recordFilenames.length;
	}
	
	/**
	 * Checks if a record fulfills user defined criteria
	 * @param samRecord
	 * @return
	 */
	public boolean isValid(SAMRecord samRecord) {
		final int mapq = samRecord.getMappingQuality();
		final List<SAMValidationError> errors = samRecord.isValid();

		if (! samRecord.getReadUnmappedFlag()
				&& ! samRecord.getNotPrimaryAlignmentFlag() // ignore non-primary alignments CHECK
				&& (mapq < 0 || mapq >= getMinMAPQ()) // filter by mapping quality
				&& (getFilterFlags() == 0 || (getFilterFlags() > 0 && ((samRecord.getFlags() & getFilterFlags()) == 0)))
				&& (getRetainFlags() == 0 || (getRetainFlags() > 0 && ((samRecord.getFlags() & getRetainFlags()) > 0)))
				&& errors == null // isValid is expensive
				) { // only store valid records that contain mapped reads
			// custom filter 
			for (final MaxValueSamTagFilter samTagFilter : getSamTagFilters()) {
				if (samTagFilter.filter(samRecord)) {
					return false;
				}
			}

			// no errors found
			return true;
		}

		// print error messages
		if (errors != null) {
			for (SAMValidationError error : errors) {
				 AbstractTool.getLogger().addError(error.toString());
			}
		}

		// something went wrong
		return false;
	}

	public LIBRARY_TYPE getLibraryType() {
		return libraryType;
	}

	public void setLibraryType(final LIBRARY_TYPE libraryType) {
		this.libraryType = libraryType;
	}

	public static SamReader createSamReader(final String inputFilename) {
		final File file = new File(inputFilename);
		final SamReader reader = SamReaderFactory
				.make()
				.setOption(Option.CACHE_FILE_BASED_INDEXES, true)
				.setOption(Option.DONT_MEMORY_MAP_INDEX, false) // disable memory mapping
				.validationStringency(ValidationStringency.LENIENT)
				.open(file);
		return reader;
	}
	
}
