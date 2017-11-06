package jacusa.cli.parameters;

import jacusa.cli.options.condition.filter.samtag.SamTagFilter;

import jacusa.data.AbstractData;
import jacusa.data.BaseCallConfig;
import jacusa.pileup.builder.AbstractDataBuilderFactory;
import jacusa.pileup.builder.hasLibraryType;

import java.util.ArrayList;
import java.util.List;

import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMValidationError;

public class ConditionParameters<T extends AbstractData>
implements hasLibraryType {
	
	// cache related
	private int maxDepth;

	// filter: read, base specific
	private byte minBASQ;
	private int minMAPQ;
	private int minCoverage;
	private boolean invertStrand;

	// filter: flags
	private int filterFlags;
	private int retainFlags;

	// filter based on SAM tags
	private List<SamTagFilter> samTagFilters;

	// dataA
	// path to BAM files
	private String[] pathnames;
	// properties for BAM files
	private BaseCallConfig baseConfig;
	private AbstractDataBuilderFactory<T> dataBuilderFactory;
	
	private ConditionParameters() {
		maxDepth 		= -1;
		minBASQ			= Byte.parseByte("20");
		minMAPQ 		= 20;
		minCoverage 	= 5;
		invertStrand	= false;

		filterFlags 	= 0;
		retainFlags	 	= 0;

		samTagFilters 		= new ArrayList<SamTagFilter>();
		pathnames 			= new String[0];
	}

	public ConditionParameters(
			final AbstractDataBuilderFactory<T> dataBuilderFactory) {
		this();
		this.dataBuilderFactory = dataBuilderFactory;
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
	public void setMaxDepth(int maxDepth) {
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
	public void setMinBASQ(byte minBASQ) {
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
	public void setMinMAPQ(int minMAPQ) {
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
	public void setMinCoverage(int minCoverage) {
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
	public void setFilterFlags(int filterFlags) {
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
	public void setRetainFlags(int retainFlags) {
		this.retainFlags = retainFlags;
	}

	/**
	 * @return the samTagFilters
	 */
	public List<SamTagFilter> getSamTagFilters() {
		return samTagFilters;
	}

	/**
	 * @param samTagFilters the samTagFilters to set
	 */
	public void setSamTagFilters(List<SamTagFilter> samTagFilters) {
		this.samTagFilters = samTagFilters;
	}

	/**
	 * @return the pathnames
	 */
	public String[] getPathnames() {
		return pathnames;
	}

	/**
	 * @param pathnames the pathnames to set
	 */
	public void setPathnames(String[] pathnames) {
		this.pathnames = pathnames;
	}

	/**
	 * @return the pileupBuilderFactory
	 */
	public AbstractDataBuilderFactory<T> getDataBuilderFactory() {
		return dataBuilderFactory;
	}

	/**
	 * @param pileupBuilderFactory the pileupBuilderFactory to set
	 */
	public void setPileupBuilderFactory(AbstractDataBuilderFactory<T> pileupBuilderFactory) {
		this.dataBuilderFactory = pileupBuilderFactory;
	}

	public BaseCallConfig getBaseConfig() {
		return baseConfig;
	}

	public void setBaseConfig(BaseCallConfig baseConfig) {
		this.baseConfig = baseConfig;
	}
	
	public void setInvertStrand(boolean invertStrand) {
		this.invertStrand = invertStrand;
	}
	
	public boolean isInvertStrand() {
		return invertStrand;
	}

	public LIBRARY_TYPE getLibraryType() {
		return getDataBuilderFactory().getLibraryType();
	}

	public int getReplicates() {
		return pathnames.length;
	}
	
	/**
	 * Checks if a record fulfills user defined criteria
	 * @param samRecord
	 * @return
	 */
	public boolean isValid(SAMRecord samRecord) {
		int mapq = samRecord.getMappingQuality();
		List<SAMValidationError> errors = samRecord.isValid();

		if (! samRecord.getReadUnmappedFlag()
				&& ! samRecord.getNotPrimaryAlignmentFlag() // ignore non-primary alignments CHECK
				&& (mapq < 0 || mapq >= getMinMAPQ()) // filter by mapping quality
				&& (getFilterFlags() == 0 || (getFilterFlags() > 0 && ((samRecord.getFlags() & getFilterFlags()) == 0)))
				&& (getRetainFlags() == 0 || (getRetainFlags() > 0 && ((samRecord.getFlags() & getRetainFlags()) > 0)))
				&& errors == null // isValid is expensive
				) { // only store valid records that contain mapped reads
			// custom filter 
			for (SamTagFilter samTagFilter : getSamTagFilters()) {
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
				 System.err.println(error.toString());
			}
		}

		// something went wrong
		return false;
	}
	
}
