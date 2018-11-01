package lib.data.cache.readsubstitution;

import lib.util.Base;
import lib.util.coordinate.Coordinate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMTag;
import htsjdk.samtools.SamReader;
import lib.cli.options.has.HasReadSubstitution.BaseSubstitution;
import lib.data.DataTypeContainer;
import lib.data.adder.AbstractDataContainerAdder;
import lib.data.adder.IncrementAdder;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.container.SharedCache;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.data.cache.region.isvalid.BaseCallValidator;

public abstract class AbstractReadSubstitutionCache 
extends AbstractDataContainerAdder 
implements RecordWrapperDataCache {
	
	private final BaseCallValidator validator;
	
	private final Map<BaseSubstitution, Integer> baseSub2int;
	private final IncrementAdder[] substBccAdders;
	
	private final Map<String, SAMRecordWrapper> internalRecordWrapperCache;
	private final Set<SAMRecordWrapper> externalRecordWrapperCache;
	
	public AbstractReadSubstitutionCache(
			final SharedCache sharedCache,
			final BaseCallValidator validator,
			final SortedSet<BaseSubstitution> baseSubs, 
			final IncrementAdder[] substBccAdders) {

		super(sharedCache);
		
		this.validator = validator;
		baseSub2int = new HashMap<>(baseSubs.size());
		for (final BaseSubstitution baseSub : baseSubs) {
			baseSub2int.put(baseSub, baseSub2int.size());
		}
		this.substBccAdders = substBccAdders;

		internalRecordWrapperCache = new HashMap<>(100);
		externalRecordWrapperCache = new HashSet<>(100);
	}

	
	protected void processSE(final Set<BaseSubstitution> queryBaseSubs, final SAMRecordWrapper recordWrapper) {
		final Set<BaseSubstitution> observedBaseSubs = collectBaseSubstitutions(queryBaseSubs, recordWrapper);
		stratifyBaseCallCounts(observedBaseSubs, recordWrapper);
	}

	protected void stratifyBaseCallCounts(final Set<BaseSubstitution> observedBaseSubs, final SAMRecordWrapper recordWrapper) {
		if (observedBaseSubs.size() == 0) {
			return;
		}
		
		final SAMRecord record = recordWrapper.getSAMRecord();
		for (final AlignmentBlock block : record.getAlignmentBlocks()) {
			final int refStart = block.getReferenceStart();
			for (int i = 0; i < block.getLength(); ++i) {
				final int refPos = refStart + i;
				final int winPos = getCoordinateController()
						.getCoordinateTranslator()
						.convert2windowPosition(refPos);
				if (winPos >= 0) {
					final int readPos = block.getReadStart() - 1 + i;
					final Base base = Base.valueOf(record.getReadBases()[readPos]);
					final byte baseQual = record.getBaseQualities()[readPos];
					for (final BaseSubstitution observedBaseSub : observedBaseSubs) {
						final int baseSubIndex = baseSub2int.get(observedBaseSub);
						substBccAdders[baseSubIndex].increment(
								refPos, winPos, readPos, 
								base, baseQual, 
								record);
					}
				}
			}
		}
	}
	
	protected void processPE(final Set<BaseSubstitution> queryBaseSubs, final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		final String readName = record.getReadName();
		if (record.contains(getCoordinateController().getActive())) { // mate within window
			if (! internalRecordWrapperCache.containsKey(readName)) {
				internalRecordWrapperCache.put(readName, recordWrapper);
			} else {
				processInternalPE(queryBaseSubs, recordWrapper);
			}
		} else { // outside
			externalRecordWrapperCache.add(recordWrapper);
		}
	}
	
	protected void processExternalPE(final Set<BaseSubstitution> queryBaseSubs) {
		for (final SAMRecordWrapper recordWrapper : externalRecordWrapperCache) {
			final SAMRecord record = recordWrapper.getSAMRecord();
			final SamReader samReader = record.getFileSource().getReader();
			final SAMRecord mateRecord = samReader.queryMate(record);
			if (mateRecord == null) {
				throw new IllegalStateException();
			}
			final SAMRecordWrapper mateRecordWrapper = new SAMRecordWrapper(mateRecord);
			mateRecordWrapper.process();
			processPE(queryBaseSubs, recordWrapper, mateRecordWrapper);
		}
	}
	
	protected void processInternalPE(final Set<BaseSubstitution> queryBaseSubs, final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		final String readName = record.getReadName();
		if (! internalRecordWrapperCache.containsKey(readName)) {
			throw new IllegalStateException();
		}
		final SAMRecordWrapper mate = internalRecordWrapperCache.get(readName);
		processPE(queryBaseSubs, recordWrapper, mate);
		internalRecordWrapperCache.remove(readName);
	}
	
	protected void processPE(
			final Set<BaseSubstitution> queryBaseSubs, 
			final SAMRecordWrapper recordWrapper1,
			final SAMRecordWrapper recordWrapper2) {
		
		final Set<BaseSubstitution> observedBaseSubs = collectBaseSubstitutions(queryBaseSubs, recordWrapper1);
		observedBaseSubs.addAll(collectBaseSubstitutions(queryBaseSubs, recordWrapper2));
		stratifyBaseCallCounts(observedBaseSubs, recordWrapper1);
		stratifyBaseCallCounts(observedBaseSubs, recordWrapper2);
	}
	
	@Override
	public void processRecordWrapper(final SAMRecordWrapper recordWrapper) {
		final Set<BaseSubstitution> queryBaseSubs = baseSub2int.keySet();
		if (recordWrapper.getSAMRecord().getReadPairedFlag()) {
			processPE(queryBaseSubs, recordWrapper);
		} else {
			processSE(queryBaseSubs, recordWrapper);
		}
	}
	
	protected abstract Base getReadBase(final SAMRecordWrapper recordWrapper, final int refPos);
	protected abstract Base getRefBase(final SAMRecordWrapper recordWrapper, final int refPos);
	
	protected Set<BaseSubstitution> collectBaseSubstitutions(final Set<BaseSubstitution> queryBaseSubs, final SAMRecordWrapper recordWrapper) {
		final Set<BaseSubstitution> foundBaseSubs = new HashSet<>(queryBaseSubs.size());
		final SAMRecord record = recordWrapper.getSAMRecord();

		final int nm = record.getIntegerAttribute(SAMTag.NM.name());
		if (nm == 0) {
			return foundBaseSubs;
		}
		final String md = record.getStringAttribute(SAMTag.MD.name());
		if (md == null) {
			throw new IllegalStateException("No MD field for record: " + record.toString());
		}
		
		for (final int refPos : recordWrapper.getRecordReferenceProvider().getMismatchRefPositions()) {			
			// FIXME embed this information in recordWrapper
			final int readMismatchPos = record.getReadPositionAtReferencePosition(refPos) - 1;
			final Base readBase = getReadBase(recordWrapper, readMismatchPos);

			if (readBase == Base.N) {
				continue;
			}
			
			final byte readBaseQuality = record.getBaseQualities()[readMismatchPos];
			if (! validator.isValid(-1, -1, 
					readMismatchPos, readBase, readBaseQuality, record)) {
				continue;
			}
			final Base refBase = getRefBase(recordWrapper, refPos);
			// recordWrapper.getRecordReferenceProvider().getReferenceBase(refPos);
			if (refBase == Base.N) {
				continue;
			}
			
			final BaseSubstitution observedBaseSub = BaseSubstitution.bases2enum(refBase, readBase);
			if (queryBaseSubs.contains(observedBaseSub)) {
				foundBaseSubs.add(observedBaseSub);
			}
		}
		
		return foundBaseSubs;
	}
	
	@Override
	public void populate(DataTypeContainer container, Coordinate coordinate) {
		for (final BaseSubstitution baseSub : baseSub2int.keySet()) {
			final int baseSubIndex = baseSub2int.get(baseSub);
			substBccAdders[baseSubIndex].populate(container, coordinate);	
		}
	}

	@Override
	public void clear() {
		for (final IncrementAdder substBcc : substBccAdders) {
			substBcc.clear();
		}
		internalRecordWrapperCache.clear();
		externalRecordWrapperCache.clear();
	}

}
