package lib.data.storage.readsubstitution;

import lib.util.Base;
import lib.util.Util;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.AllAlignmentBlocksPosProvider;
import lib.util.position.AllDeletionsPositionProvider;
import lib.util.position.AllInsertionsPosProvider;
import lib.util.position.ConsumingRefPosProviderBuilder;
import lib.util.position.MismatchPosProvider;
import lib.util.position.Position;
import lib.util.position.PositionProvider;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMTag;
import lib.cli.options.filter.has.BaseSub;
import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.GeneralRecordProcessor;
import lib.data.validator.Validator;
import lib.record.Record;

public class BaseSubRecordProcessor 
implements GeneralRecordProcessor {

	private final SharedStorage sharedStorage;
	
	private final BaseCallInterpreter bci;
	private final Validator validator;
	
	private final Map<String, Record> internalRecords;
	private final Set<Record> externalrecord;
	
	private final Set<BaseSub> queryBaseSubs;
	
	private final Map<BaseSub, PositionProcessor> alignedPosProcs;
	
	private final Map<BaseSub, PositionProcessor> covPosProcs;
	private final Map<BaseSub, PositionProcessor> insPosProcs;
	private final Map<BaseSub, PositionProcessor> delPosProcs;
	private final boolean onlyStart;
	
	public BaseSubRecordProcessor(
			final SharedStorage sharedStorage,
			final BaseCallInterpreter bci,
			final Validator validator,
			final Set<BaseSub> queryBaseSubs,
			final Map<BaseSub, PositionProcessor> alignedPosProcessors,
			final boolean onlyStart) {
		this(
				sharedStorage,
				bci,
				validator,
				queryBaseSubs,
				alignedPosProcessors,
				new EnumMap<>(BaseSub.class),
				new EnumMap<>(BaseSub.class),
				new EnumMap<>(BaseSub.class),
				onlyStart);
	}
	
	public BaseSubRecordProcessor(
			final SharedStorage sharedStorage,
			final BaseCallInterpreter bci,
			final Validator validator,
			final Set<BaseSub> queryBaseSubs,
			final Map<BaseSub, PositionProcessor> alignedPosProcs,
			final Map<BaseSub, PositionProcessor> covPosProcs,
			final Map<BaseSub, PositionProcessor> insPosProcs,
			final Map<BaseSub, PositionProcessor> delPosProcs,
			final boolean onlyStart) {

		this.sharedStorage 		= sharedStorage;
		
		this.bci 				= bci;
		this.validator 			= validator;

		internalRecords 	= new HashMap<>(Util.noRehashCapacity(100));
		externalrecord 	= new HashSet<>(Util.noRehashCapacity(100));
		
		this.queryBaseSubs 			= queryBaseSubs;
		
		this.alignedPosProcs	= alignedPosProcs;

		this.covPosProcs		= covPosProcs;
		this.insPosProcs		= insPosProcs;
		this.delPosProcs		= delPosProcs;
		
		this.onlyStart 			= onlyStart;
	}
	
	protected void processSE(
			final Set<BaseSub> queryBaseSubs, 
			final Record record) {
		
		final Set<BaseSub> observedBaseSubs = collectBaseSubs(
				queryBaseSubs, record);
		stratifyBaseCallCounts(observedBaseSubs, record);
	}

	private CoordinateController getController() {
		return sharedStorage.getCoordinateController();
	}
	
	private CoordinateTranslator getTranslator() {
		return getController().getCoordinateTranslator();
	}
	
	protected void stratifyBaseCallCounts(
			final Set<BaseSub> observedBaseSubs, final Record record) {
		
		if (observedBaseSubs.isEmpty()) {
			return;
		}
		
		// process base call counts and store dependent on base substitutions
		final PositionProvider alignedPosProvider = new AllAlignmentBlocksPosProvider(record, getTranslator());
		while (alignedPosProvider.hasNext()) {
			final Position alignedPos = alignedPosProvider.next();
			for (final BaseSub baseSub : observedBaseSubs) {
				final PositionProcessor positionProcessor = alignedPosProcs.get(baseSub);
				if (positionProcessor.checkValidators(alignedPos)) {
					positionProcessor.processStorages(alignedPos);
				}
			}
		}
		
		if (! delPosProcs.isEmpty()) {
			// process deletions and store dependent on base substitutions
			final PositionProvider delPosProvider = new AllDeletionsPositionProvider(record, getTranslator());
			while (delPosProvider.hasNext()) {
				final Position delPos = delPosProvider.next();
				for (final BaseSub baseSub : observedBaseSubs) {
					final PositionProcessor positionProcessor = delPosProcs.get(baseSub);
					if (positionProcessor.checkValidators(delPos)) {
						positionProcessor.processStorages(delPos);
					}
				}
			}
		}
		
		if (! insPosProcs.isEmpty()) {
			// process insertions and store dependent on base substitutions
			final PositionProvider insPosProvider = new AllInsertionsPosProvider(record, getTranslator(), onlyStart);
			while (insPosProvider.hasNext()) {
				final Position insPos = insPosProvider.next();
				for (final BaseSub baseSub : observedBaseSubs) {
					final PositionProcessor positionProcessor = insPosProcs.get(baseSub);
					if (positionProcessor.checkValidators(insPos)) {
						positionProcessor.processStorages(insPos);
					}
				}
			}
		}

		if (! delPosProcs.isEmpty() || ! insPosProcs.isEmpty()) {
			// process coverage and store dependent on base substitutions
			final PositionProvider covPosProvider = new ConsumingRefPosProviderBuilder(record, getTranslator()).build();
			while (covPosProvider.hasNext()) {
				final Position covPos = covPosProvider.next();
				for (final BaseSub baseSub : observedBaseSubs) {
					final PositionProcessor positionProcessor = covPosProcs.get(baseSub);
					if (positionProcessor.checkValidators(covPos)) {
						positionProcessor.processStorages(covPos);
					}
				}
			}
		}
	}
	
	protected void processPE(
			final Set<BaseSub> queryBaseSubs, 
			final Record record) {
		
		final SAMRecord samRecord 	= record.getSAMRecord();
		final String readName 		= samRecord.getReadName();
		
		final SAMRecord mateRecord = record.getMate().getSAMRecord();
		if (mateRecord.overlaps(getController().getActive())) { // mate within window
			if (! internalRecords.containsKey(readName)) {
				internalRecords.put(readName, record);
			} else {
				processInternalPE(queryBaseSubs, record);
			}
		} else { // outside
			externalrecord.add(record);
		}
	}
	
	protected void processExternalPE(final Set<BaseSub> queryBaseSubs) {
		for (final Record record : externalrecord) {
			final SAMRecord mateRecord = record.getMate().getSAMRecord();
			if (mateRecord == null) {
				throw new IllegalStateException();
			}
			final Record materecord = new Record(mateRecord);
			processPE(queryBaseSubs, record, materecord);
		}
	}
	
	protected void processInternalPE(final Set<BaseSub> queryBaseSubs, final Record record) {
		final SAMRecord samRecord = record.getSAMRecord();
		final String readName = samRecord.getReadName();
		if (! internalRecords.containsKey(readName)) {
			throw new IllegalStateException();
		}
		final Record mate = internalRecords.get(readName);
		processPE(queryBaseSubs, record, mate);
		internalRecords.remove(readName);
	}
	
	protected void processPE(
			final Set<BaseSub> queryBaseSubs, 
			final Record record1,
			final Record record2) {
		
		final Set<BaseSub> obsBaseSubs = collectBaseSubs(queryBaseSubs, record1);
		obsBaseSubs.addAll(collectBaseSubs(queryBaseSubs, record2));
		stratifyBaseCallCounts(obsBaseSubs, record1);
		stratifyBaseCallCounts(obsBaseSubs, record2);
	}
	
	@Override
	public void preProcess() {
		// nothing to be done
	}
	
	@Override
	public void process(final Record record) {
		final SAMRecord samRecord = record.getSAMRecord();
		// if mate not mapped process it as it was SE
		if (samRecord.getReadPairedFlag() && ! samRecord.getMateUnmappedFlag()) {
			// ignore trans spliced reads
			if (! samRecord.getReferenceName().equals(samRecord.getMateReferenceName())) { 
				return;
			}
			processPE(queryBaseSubs, record);
		} else {
			processSE(queryBaseSubs, record);
		}
	}
	
	@Override
	public void postProcess() {
		processExternalPE(queryBaseSubs);
		internalRecords.clear();
		externalrecord.clear();
	}
	
	protected Set<BaseSub> collectBaseSubs(
			final Set<BaseSub> queryBaseSubs, 
			final Record record) {
		
		final Set<BaseSub> foundBaseSubs 	= new HashSet<>(queryBaseSubs.size());
		final SAMRecord samRecord 					= record.getSAMRecord();
		
		final int nm = samRecord.getIntegerAttribute(SAMTag.NM.name());
		if (nm == 0) {
			return foundBaseSubs;
		}
		
		final String md = samRecord.getStringAttribute(SAMTag.MD.name());
		if (md == null) {
			throw new IllegalStateException("No MD field for record: " + record.toString());
		}

		final MismatchPosProvider mismatchPositionProvider = 
				new MismatchPosProvider(record, getTranslator(), validator);
		
		while (mismatchPositionProvider.hasNext()) {
			final Position pos = mismatchPositionProvider.next();
			
			final int readMismatchPos 	= pos.getReadPosition();
			final Base readBase 		= bci.getReadBase(record, readMismatchPos);
			final Base refBase 			= bci.getRefBase(record, pos);
			
			final BaseSub observedBaseSub = BaseSub.bases2enum(refBase, readBase);
			if (queryBaseSubs.contains(observedBaseSub)) {
				foundBaseSubs.add(observedBaseSub);
			}
		}
		
		return foundBaseSubs;
	}

}
