package jacusa.pileup.builder;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;
import jacusa.filter.FilterContainer;
import jacusa.filter.storage.ProcessDeletionOperator;
import jacusa.filter.storage.ProcessInsertionOperator;
import jacusa.filter.storage.ProcessRecord;
import jacusa.filter.storage.ProcessSkippedOperator;
import jacusa.filter.storage.ProcessAlignmentOperator;
import jacusa.filter.storage.ProcessAlignmentBlock;
import jacusa.pileup.iterator.location.CoordinateAdvancer;
import jacusa.pileup.iterator.location.UnstrandedCoordinateAdvancer;
import jacusa.util.Coordinate;
import jacusa.util.WindowCoordinate;
import jacusa.util.Coordinate.STRAND;

import java.util.Arrays;

import net.sf.samtools.CigarElement;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class AbstractDataBuilder<T extends AbstractData>
implements DataBuilder<T>, hasLibraryType {

	// in genomic coordinates
	protected CoordinateAdvancer advancer;
	protected WindowCoordinate windowCoordinates;

	// true if a valid read is found within genomicWindowStart and genomicWindowStart + windowSize
	protected SAMRecord[] SAMRecordsBuffer;
	protected SAMFileReader reader;

	protected int filteredSAMRecords;
	protected int SAMRecords;

	protected ConditionParameters<T> condition;
	protected AbstractParameters<T> parameters;

	protected WindowCache windowCache;
	
	protected FilterContainer<T> filterContainer;
	protected int[] byte2int;

	protected int distance;
	
	protected LIBRARY_TYPE libraryType;

	protected CACHE_STATUS cacheStatus;
	
	public AbstractDataBuilder (
			final WindowCoordinate windowCoordinates,
			final SAMFileReader SAMFileReader, 
			final ConditionParameters<T> condition,
			final AbstractParameters<T> parameters,
			final STRAND strand,
			final LIBRARY_TYPE libraryType) {
		advancer 				= new UnstrandedCoordinateAdvancer(new Coordinate(windowCoordinates.getContig(), Integer.MIN_VALUE, strand));
		this.windowCoordinates	= windowCoordinates;
		
		SAMRecordsBuffer		= new SAMRecord[20000];
		reader					= SAMFileReader;

		filteredSAMRecords		= 0;
		SAMRecords				= 0;

		this.condition			= condition;
		this.parameters			= parameters;

		windowCache				= new WindowCache(windowCoordinates);
		filterContainer			= parameters.getFilterConfig().createFilterContainer(windowCoordinates, strand, condition);
		byte2int 				= parameters.getBaseConfig().getbyte2int();

		this.libraryType		= libraryType;
		
		// get max overhang
		distance 				= filterContainer.getOverhang();
		cacheStatus				= CACHE_STATUS.NOT_CACHED;
	}


	
	@Override
	public Coordinate nextCoordinate() {
		return advancer.nextCoordinate();
	}
	
	@Override
	public void advance() {
		final Coordinate nextCoordinate = nextCoordinate();
		adjustPosition(nextCoordinate.getPosition(), nextCoordinate.getStrand());
	}
	
	/**
	 * 
	 * @param targetPosition
	 * @return
	 */
	@Override
	public SAMRecord getNextRecord(int targetPosition) {
		SAMRecordIterator iterator = reader.query(
				windowCoordinates.getContig(), 
				targetPosition, 
				windowCoordinates.getMaxGenomicPosition(), 
				false);

		while (iterator.hasNext() ) {
			SAMRecord record = iterator.next();

			if (condition.isValid(record)) {
				iterator.close();
				iterator = null;
				return record;
			}
		}
		iterator.close();
		iterator = null;

		// if no more reads are found 
		return null;
	}

	@Override
	public SAMRecordIterator getIterator(final int genomicPosition) {
		windowCoordinates.setStart(genomicPosition);

		// get iterator to fill the window
		return reader.query(
				windowCoordinates.getContig(), 
				windowCoordinates.getStart(), 
				windowCoordinates.getEnd(), 
				false);
	}
	
	@Override
	public void adjustPosition(final int newPosition, final STRAND newStrand) {
		if (cacheStatus == CACHE_STATUS.NOT_CACHED || ! windowCoordinates.isContainedInWindow(newPosition)) {
			if (fillWindow(this, condition, SAMRecordsBuffer, newPosition)) {
				cacheStatus = CACHE_STATUS.CACHED;
			} else {
				cacheStatus = CACHE_STATUS.NOT_FOUND;
			}
		}
		advancer.adjustPosition(newPosition, newStrand);
	}

	// Reset all caches in windows
	@Override
	public void clearCache() {
		windowCache.clear();
		filterContainer.clear();
		Arrays.fill(SAMRecordsBuffer, null);
		cacheStatus	= CACHE_STATUS.NOT_CACHED;
	}

	protected abstract void addHighQualityBaseCall(final int windowPosition, 
			final int baseIndex, final int qualIndex);

	protected abstract void addLowQualityBaseCall(final int windowPosition, 
			final int baseIndex, final int qualIndex);

	/*
	 * process CIGAR string methods
	 */
	
	protected void processHardClipping(
			int windowPosition, int readPosition, int genomicPosition, 
			final CigarElement cigarElement, final SAMRecord record) {
		// System.err.println("Hard Clipping not handled yet!");
	}
	
	protected void processSoftClipping(
			int windowPosition, int readPosition, int genomicPosition, 
			final CigarElement cigarElement, final SAMRecord record) {
		// override if needed
	}

	protected void processPadding(
			int windowPosition, int readPosition, int genomicPosition,
			int upstreamMatch, int downstreamMatch,
			final CigarElement cigarElement, final SAMRecord record) {
		System.err.println("Padding not handled yet!");
	}

	protected byte[] parseMDField(final SAMRecord record) {
		String tag = "MD";
		Object o = record.getAttribute(tag);
		if (o == null) {
			return new byte[0]; // no MD field :-(
		}

		// init container size with read length
		final byte[] referenceBases = new byte[record.getReadLength()];
		int destPos = 0;
		// copy read sequence to reference container / concatenate mapped segements ignor DELs
		for (int i = 0; i < record.getAlignmentBlocks().size(); i++) {
			if (referenceBases != null) {
				final int srcPos = record.getAlignmentBlocks().get(i).getReadStart() - 1;
				final int length = record.getAlignmentBlocks().get(i).getLength();
				System.arraycopy(
						record.getReadBases(), 
						srcPos, 
						referenceBases, 
						destPos, 
						length);
				destPos += length;
			}
		}

		// get MD string
		String MD = (String)o;
		// add potential missing number(s)
		MD = "0" + MD.toUpperCase();

		int position = 0;
		boolean nextInteger = true;
		// change to reference base based on MD string
//		int j = 0;
		for (String e : MD.split("((?<=[0-9]+)(?=[^0-9]+))|((?<=[^0-9]+)(?=[0-9]+))")) {
			if (nextInteger) { // match
				// use read sequence
				int matchLength = Integer.parseInt(e);
				position += matchLength;
				nextInteger = false;	
			} else if (e.charAt(0) == '^') {
				// ignore deletions from reference
				nextInteger = true;
			} else { // mismatch
//				try {
				referenceBases[position] = (byte)e.toCharArray()[0];
//				} catch (ArrayIndexOutOfBoundsException e2) {
//					String[] tmp = MD.split("((?<=[0-9]+)(?=[^0-9]+))|((?<=[^0-9]+)(?=[0-9]+))");
//					System.out.println(e2.toString());
//				}

				position += 1;
				nextInteger = true;
			}
//			++j;
		}
		// resize container if MD < read length
		if (position < referenceBases.length) {
			Arrays.copyOf(referenceBases, position);
		}

		return referenceBases;
	}
	
	public void processRecord(final SAMRecord record) {
		// init	
		int readPosition 	= 0;
		int genomicPosition = record.getAlignmentStart();
		int windowPosition  = windowCoordinates.convert2WindowPosition(genomicPosition);
		int alignmentBlockI = 0;

		int MDPosition = 0;
		byte[] referenceBases = null;

		// collect alignment length of blocks
		int alignmentBlockLength[] = new int[record.getAlignmentBlocks().size() + 2];
		alignmentBlockLength[0] = 0;
		
		for (int i = 0; i < record.getAlignmentBlocks().size(); i++) {
			alignmentBlockLength[i + 1] = record.getAlignmentBlocks().get(i).getLength();
		}
		alignmentBlockLength[record.getAlignmentBlocks().size() + 1] = 0;

		// process record specific filters
		for (ProcessRecord storage : filterContainer.getProcessRecord()) {
			storage.processRecord(windowCoordinates.getStart(), record);
		}
		
		// process CIGAR -> SNP, INDELs
		for (final CigarElement cigarElement : record.getCigar().getCigarElements()) {
			
			switch(cigarElement.getOperator()) {

			/*
			 * handle insertion
			 */
			case I:
				processInsertion(windowPosition, readPosition, genomicPosition, 
						alignmentBlockLength[alignmentBlockI], 
						alignmentBlockLength[alignmentBlockI + 1], 
						cigarElement, record);
				readPosition += cigarElement.getLength();
				break;

			/*
			 * handle alignment/sequence match and mismatch
			 */
			case M:
			case EQ:
			case X:
				processAlignmentMatch(windowPosition, readPosition, genomicPosition, 
						cigarElement, record, MDPosition, referenceBases);

				readPosition 	+= cigarElement.getLength();
				genomicPosition += cigarElement.getLength();
				MDPosition 		+= cigarElement.getLength();
				windowPosition 	= windowCoordinates.convert2WindowPosition(genomicPosition);
				alignmentBlockI++;
				break;

			/*
			 * handle hard clipping 
			 */
			case H:
				processHardClipping(windowPosition, readPosition, genomicPosition, 
						cigarElement, record);
				break;

			/*
			 * handle deletion from the reference and introns
			 */
			case D:
				processDeletion(windowPosition, readPosition, genomicPosition, 
						alignmentBlockLength[alignmentBlockI], 
						alignmentBlockLength[alignmentBlockI + 1],
						cigarElement, record);
				genomicPosition += cigarElement.getLength();
				windowPosition  = windowCoordinates.convert2WindowPosition(genomicPosition);
				break;

			case N:
				processSkipped(windowPosition, readPosition, genomicPosition, 
						alignmentBlockLength[alignmentBlockI], 
						alignmentBlockLength[alignmentBlockI + 1],
						cigarElement, record);
				genomicPosition += cigarElement.getLength();
				windowPosition  = windowCoordinates.convert2WindowPosition(genomicPosition);
				break;

			/*
			 * soft clipping
			 */
			case S:
				processSoftClipping(windowPosition, readPosition, genomicPosition, 
						cigarElement, record);
				readPosition += cigarElement.getLength();
				break;

			/*
			 * silent deletion from padded sequence
			 */
			case P:
				processPadding(windowPosition, readPosition, genomicPosition,
						alignmentBlockLength[alignmentBlockI], 
						alignmentBlockLength[alignmentBlockI + 1],
						cigarElement, record);
				break;

			default:
				throw new RuntimeException("Unsupported Cigar Operator: " + cigarElement.getOperator().toString());
			}
		}
	}

	protected void processAlignmentMatch(
			int windowPosition, int readPosition, int genomicPosition, 
			final CigarElement cigarElement, final SAMRecord record,
			final int MDPosition, byte[] referenceBases) {
		// process alignmentBlock specific filters
		for (final ProcessAlignmentBlock filterAlignmentBlock : filterContainer.getProcessAlignmentBlock()) {
			filterAlignmentBlock.process(windowPosition, readPosition, genomicPosition, 
					cigarElement, record);
		}
		
		for (int offset = 0; offset < cigarElement.getLength(); ++offset) {
			final int baseIndex = byte2int[record.getReadBases()[readPosition + offset]];
			int qualIndex = record.getBaseQualities()[readPosition + offset];

			if (baseIndex == -1) {
				windowPosition = windowCoordinates.convert2WindowPosition(genomicPosition + offset);
				int orientation = windowCoordinates.getOrientation(genomicPosition + offset);
				
				// process MD on demand
				if (record.getAttribute("MD") != null && orientation == 0 && windowCache.getReferenceBase(windowPosition) == (byte)'N') {
					if (referenceBases == null) {
						referenceBases = parseMDField(record);
					}
					if (referenceBases.length > 0) {
						windowCache.addReferenceBase(windowPosition, referenceBases[MDPosition + offset]);
					}
				}

				continue;
			}

			// speedup: if orientation == 1 the remaining part of the read will be outside of the windowCache
			// ignore the overhanging part of the read until it overlaps with the window cache
			windowPosition = windowCoordinates.convert2WindowPosition(genomicPosition + offset);
			int orientation = windowCoordinates.getOrientation(genomicPosition + offset);
			
			switch (orientation) {
			case 1:
				if ((genomicPosition + offset) - windowCoordinates.getEnd() <= distance) {
					if (qualIndex >= condition.getMinBASQ()) {
						for (final ProcessAlignmentOperator filterAligmnentOperator : 
							filterContainer.getProcessAlignment()) {
							filterAligmnentOperator.processAlignmentOperator(
									windowPosition, readPosition + offset, genomicPosition + offset, 
									cigarElement, record, 
									baseIndex, qualIndex);
						}
					}
				} else {
					return;
				}
				break;
			case -1: // speedup jump to covered position
				if (windowCoordinates.getStart() - (genomicPosition + offset) > distance) {
					offset += windowCoordinates.getStart() - (genomicPosition + offset) - distance - 1;
				} else {
					if (qualIndex >= condition.getMinBASQ()) {
						for (final ProcessAlignmentOperator filterAligmnentOperator : 
							filterContainer.getProcessAlignment()) {
							filterAligmnentOperator.processAlignmentOperator(
									windowPosition, readPosition + offset, genomicPosition + offset, 
									cigarElement, record, 
									baseIndex, qualIndex);
						}
					}
				}
				break;
			case 0:
				if (windowPosition >= 0) {
					if (qualIndex >= condition.getMinBASQ()) {
						addHighQualityBaseCall(windowPosition, baseIndex, qualIndex);

						// process any alignmentMatch specific filters
						for (final ProcessAlignmentOperator filterAligmnentOperator : 
							filterContainer.getProcessAlignment()) {
							filterAligmnentOperator.processAlignmentOperator(
									windowPosition, readPosition + offset, genomicPosition + offset, 
									cigarElement, record, 
									baseIndex, qualIndex);
						}
					} else if (parameters.collectLowQualityBaseCalls()) { 
						addLowQualityBaseCall(windowPosition, baseIndex, qualIndex);
					}
					// process MD on demand
					if (record.getAttribute("MD") != null && windowCache.getReferenceBase(windowPosition) == (byte)'N') {
						if (referenceBases == null) {
							referenceBases = parseMDField(record);
						}
						if (referenceBases.length > 0) {
							windowCache.addReferenceBase(windowPosition, referenceBases[MDPosition + offset]);
						}
					}
				}
				break;
			}
		}
	}

	protected void processInsertion(int windowPosition, int readPosition, int genomicPosition,
			int upstreamMatch, int downstreamMatch,
			final CigarElement cigarElement, final SAMRecord record) {
		for (final ProcessInsertionOperator storage : filterContainer.getProcessInsertion()) {
			storage.processInsertionOperator(windowPosition, readPosition, genomicPosition, 
					upstreamMatch, downstreamMatch, 
					cigarElement, record);
		}
	}

	protected void processDeletion(int windowPosition, int readPosition, int genomicPosition, 
			int upstreamMatch, int downstreamMatch,
			final CigarElement cigarElement, final SAMRecord record) {
		for (final ProcessDeletionOperator storage : filterContainer.getProcessDeletion()) {
			storage.processDeletionOperator(windowPosition, readPosition, genomicPosition, 
					upstreamMatch, downstreamMatch,
					cigarElement, record);
		}
	}

	protected void processSkipped(int windowPosition, int readPosition, int genomicPosition,
			int upstreamMatch, int downstreamMatch,
			final CigarElement cigarElement, final SAMRecord record) {
		for (final ProcessSkippedOperator storage : filterContainer.getProcessSkipped()) {
			storage.processSkippedOperator(windowPosition, readPosition, genomicPosition,
					upstreamMatch, downstreamMatch,
					cigarElement, record);
		}
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public int getFilteredSAMRecords() {
		return filteredSAMRecords;
	}

	@Override
	public WindowCoordinate getWindowCoordinates() {
		return windowCoordinates;
	}
	
	@Override
	public LIBRARY_TYPE getLibraryType() {
		return libraryType;
	}
	
	@Override
	public int getSAMRecords() {
		return SAMRecords;
	}
	
	@Override
	public SAMRecord[] getSAMRecordsBuffer() {
		return SAMRecordsBuffer;
	}

	
	@Override
	final public void incrementFilteredSAMRecords() {
		filteredSAMRecords++;
	}
	
	@Override
	final public void incrementSAMRecords() {
		SAMRecords++;
	}
	
	public Coordinate getCurrentCoordinate() {
		return advancer.getCurrentCoordinate();
	}

	@Override
	public DataBuilder.CACHE_STATUS getCacheStatus() {
		return cacheStatus;
	}

	// Helper prevent code duplication
	public static <S extends AbstractData> int processIterator(
			final DataBuilder<S> builder, 
			final ConditionParameters<S> condition,
			final SAMRecord[] SAMRecordsBuffer,
			final SAMRecordIterator iterator) {
		int SAMReocordsInBuffer = 0;
		while (iterator.hasNext()) {
			SAMRecord record = iterator.next();

			if(condition.isValid(record)) {
				SAMRecordsBuffer[SAMReocordsInBuffer++] = record;
				builder.incrementSAMRecords();
			} else {
				builder.incrementFilteredSAMRecords();
			}

			// process buffer
			if (SAMReocordsInBuffer >= SAMRecordsBuffer.length) {
				SAMReocordsInBuffer = processBuffer(builder, SAMReocordsInBuffer, SAMRecordsBuffer);
			}
		}
		iterator.close();
		
		return SAMReocordsInBuffer;
	}
	
	public static <S extends AbstractData> boolean fillWindow(
			final DataBuilder<S> builder, 
			final ConditionParameters<S> condition,
			final SAMRecord[] SAMRecordBuffer,
			final int genomicPosition) {
		builder.clearCache();

		// get iterator to fill the window
		SAMRecordIterator iterator = builder.getIterator(genomicPosition);
		final int SAMReocordsInBuffer = processIterator(builder,
				condition, 
				SAMRecordBuffer,
				iterator);

		if (SAMReocordsInBuffer > 0) {
			// process any left SAMrecords in the buffer
			processBuffer(builder, SAMReocordsInBuffer, builder.getSAMRecordsBuffer());
		}

		if (builder.getSAMRecords() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public static <S extends AbstractData> int processBuffer(
			final DataBuilder<S> builder, 
			final int SAMReocordsInBuffer, 
			final SAMRecord[] SAMRecordsBuffer) {
		for (int i = 0; i < SAMReocordsInBuffer; ++i) {
			try {
				builder.processRecord(SAMRecordsBuffer[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return 0;
	}	
	
}
