package lib.data.builder;

import jacusa.filter.FilterContainer;
import jacusa.filter.storage.ProcessAlignmentBlock;
import jacusa.filter.storage.ProcessAlignmentOperator;
import jacusa.filter.storage.ProcessDeletionOperator;
import jacusa.filter.storage.ProcessInsertionOperator;
import jacusa.filter.storage.ProcessRecord;
import jacusa.filter.storage.ProcessSkippedOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameters;
import lib.cli.parameters.JACUSAConditionParameters;
import lib.data.AbstractData;
import lib.data.cache.AbstractCache;
import lib.location.CoordinateAdvancer;
import lib.location.UnstrandedCoordinateAdvancer;
import lib.util.Coordinate;
import lib.util.Coordinate.STRAND;

import htsjdk.samtools.CigarElement;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;

public abstract class AbstractDataBuilder<T extends AbstractData>
implements hasLibraryType {

	private final AbstractConditionParameter<T> conditionParameter;
	private final AbstractParameters<T> parameters;
	private final FilterContainer<T> filterContainer;

	private final LIBRARY_TYPE libraryType;
	
	private final AbstractCache cache; 
	private Coordinate activewindowCoordinate;
	private CACHE_STATUS cacheStatus;
	
	// OLD
	// TODO not sure if needed move somewhere else?
	// move to SAMRecordWrapperIterator
	// private int filteredSAMRecords;
	// private int acceptedSAMRecords;
	//protected int distance;
	
	public AbstractDataBuilder(
			final AbstractConditionParameter<T> conditionParameter,
			final AbstractParameters<T> parameters,
			final LIBRARY_TYPE libraryType,
			final AbstractCache cache) {
		this.conditionParameter	= conditionParameter;
		this.parameters = parameters;
		this.filterContainer = null;
		// TODO filterContainer			= parameters.getFilterConfig().createFilterContainer(windowCoordinates, strand, condition);

		this.libraryType = libraryType;
		
		this.cache = cache;
		cacheStatus	= CACHE_STATUS.NOT_CACHED;
	}

	public List<SAMRecordWrapper> buildCache(final Coordinate activeWindowCoordinate,
			Iterator<SAMRecordWrapper> iterator) {
		
		this.activewindowCoordinate = activeWindowCoordinate;
		final List<SAMRecordWrapper> recordWrappers = new ArrayList<SAMRecordWrapper>();
		
		while (iterator.hasNext()) {
			final SAMRecordWrapper recordWrapper = iterator.next(); 
			cache.addRecordWrapper(recordWrapper);
			recordWrappers.add(recordWrapper);
		}
		
		return recordWrappers;
	}
	
	// Reset all caches in windows
	public void clearCache() {
		cache.clear();
		filterContainer.clear();
	}

	public abstract T getData(final Coordinate coordinate);
	
	public FilterContainer<T> getFilterContainer(final Coordinate coordinate) {
		return filterContainer; // TODO
	}
	
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
	
	// TODO adjust
	/* TODO
	public void processRecord(final SAMRecord record) {
		// init	
		int readPosition 	= 0;
		int genomicPosition = record.getAlignmentStart();
		int windowPosition  = Coordinate.makeRelativePosition(activewindowCoordinate, genomicPosition);
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
			storage.processRecord(activewindowCoordinate.getStart(), record);
		}
		
		// process CIGAR -> SNP, INDELs
		for (final CigarElement cigarElement : record.getCigar().getCigarElements()) {
			
			switch(cigarElement.getOperator()) {

			/*
			 * handle insertion
			 *
			case I:
				processInsertion(windowPosition, readPosition, genomicPosition, 
						alignmentBlockLength[alignmentBlockI], 
						alignmentBlockLength[alignmentBlockI + 1], 
						cigarElement, record);
				readPosition += cigarElement.getLength();
				break;

			/*
			 * handle alignment/sequence match and mismatch
			 *
			case M:
			case EQ:
			case X:
				processAlignmentMatch(windowPosition, readPosition, genomicPosition, 
						cigarElement, record, MDPosition, referenceBases);

				readPosition 	+= cigarElement.getLength();
				genomicPosition += cigarElement.getLength();
				MDPosition 		+= cigarElement.getLength();
				windowPosition 	= Coordinate.makeRelativePosition(activewindowCoordinate, genomicPosition);
				alignmentBlockI++;
				break;

			/*
			 * handle hard clipping 
			 *
			case H:
				processHardClipping(windowPosition, readPosition, genomicPosition, 
						cigarElement, record);
				break;

			/*
			 * handle deletion from the reference and introns
			 *
			case D:
				processDeletion(windowPosition, readPosition, genomicPosition, 
						alignmentBlockLength[alignmentBlockI], 
						alignmentBlockLength[alignmentBlockI + 1],
						cigarElement, record);
				genomicPosition += cigarElement.getLength();
				windowPosition  = Coordinate.makeRelativePosition(activewindowCoordinate, genomicPosition);
				break;

			case N:
				processSkipped(windowPosition, readPosition, genomicPosition, 
						alignmentBlockLength[alignmentBlockI], 
						alignmentBlockLength[alignmentBlockI + 1],
						cigarElement, record);
				genomicPosition += cigarElement.getLength();
				windowPosition  = Coordinate.makeRelativePosition(activewindowCoordinate, genomicPosition);
				break;

			/*
			 * soft clipping
			 *
			case S:
				processSoftClipping(windowPosition, readPosition, genomicPosition, 
						cigarElement, record);
				readPosition += cigarElement.getLength();
				break;

			/*
			 * silent deletion from padded sequence
			 *
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
				windowPosition = Coordinate.makeRelativePosition(activewindowCoordinate, genomicPosition + offset);
				int orientation = activewindowCoordinate.getOrientation(genomicPosition + offset);
				
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
			windowPosition = activewindowCoordinate.convert2WindowPosition(genomicPosition + offset);
			int orientation = activewindowCoordinate.getOrientation(genomicPosition + offset);
			
			switch (orientation) {
			case 1:
				if ((genomicPosition + offset) - activewindowCoordinate.getEnd() <= distance) {
					if (qualIndex >= conditionParameter.getMinBASQ()) {
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
				if (activewindowCoordinate.getStart() - (genomicPosition + offset) > distance) {
					offset += activewindowCoordinate.getStart() - (genomicPosition + offset) - distance - 1;
				} else {
					if (qualIndex >= conditionParameter.getMinBASQ()) {
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
					if (qualIndex >= conditionParameter.getMinBASQ()) {
						addHighQualityBaseCall(windowPosition, baseIndex, qualIndex);

						// process any alignmentMatch specific filters
						for (final ProcessAlignmentOperator filterAligmnentOperator : 
							filterContainer.getProcessAlignment()) {
							filterAligmnentOperator.processAlignmentOperator(
									windowPosition, readPosition + offset, genomicPosition + offset, 
									cigarElement, record, 
									baseIndex, qualIndex);
						}
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
	*/

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

	public Coordinate getActiveWindowCoordinate() {
		return activewindowCoordinate;
	}
	
	public int getActiveWindowSize() {
		return parameters.getActiveWindowSize();
	}
	
	public AbstractParameters<T> getParameters() {
		return parameters;
	}

	public LIBRARY_TYPE getLibraryType() {
		return libraryType;
	}
	
	public CACHE_STATUS getCacheStatus() {
		return cacheStatus;
	}

	public enum CACHE_STATUS {NOT_CACHED,CACHED,NOT_FOUND};

	
}
