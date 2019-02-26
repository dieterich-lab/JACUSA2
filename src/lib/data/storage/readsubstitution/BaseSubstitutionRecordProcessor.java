package lib.data.storage.readsubstitution;

import lib.util.Base;
import lib.util.Util;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.AllAlignmentBlocksPositionProvider;
import lib.util.position.AllDeletionsPositionProvider;
import lib.util.position.ConsumingReferencePositionProviderBuilder;
import lib.util.position.MismatchPositionProvider;
import lib.util.position.Position;
import lib.util.position.PositionProvider;
import lib.recordextended.SAMRecordExtended;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMTag;
import htsjdk.samtools.SamReader;
import lib.cli.options.filter.has.HasReadSubstitution.BaseSubstitution;
import lib.data.storage.PositionProcessor;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordExtendedPrePostProcessor;
import lib.data.validator.Validator;

public class BaseSubstitutionRecordProcessor 
implements RecordExtendedPrePostProcessor {

	private final SharedStorage sharedStorage;
	
	private final BaseCallInterpreter bci;
	private final Validator validator;
	
	private final Map<String, SAMRecordExtended> internalRecordExtended;
	private final Set<SAMRecordExtended> externalRecordExtended;
	
	private final Set<BaseSubstitution> queryBaseSubs;
	
	private final Map<BaseSubstitution, PositionProcessor> alignedPosProcessors;
	private final Map<BaseSubstitution, PositionProcessor> covPosProcessors;
	private final Map<BaseSubstitution, PositionProcessor> delPosProcessors;
	
	public BaseSubstitutionRecordProcessor(
			final SharedStorage sharedStorage,
			final BaseCallInterpreter bci,
			final Validator validator,
			final Set<BaseSubstitution> queryBaseSubs,
			final Map<BaseSubstitution, PositionProcessor> alignedPosProcessors,
			final Map<BaseSubstitution, PositionProcessor> covPosProcessors,
			final Map<BaseSubstitution, PositionProcessor> delPosProcessors) {

		this.sharedStorage 		= sharedStorage;
		
		this.bci 				= bci;
		this.validator 			= validator;

		internalRecordExtended 	= new HashMap<>(Util.noRehashCapacity(100));
		externalRecordExtended 	= new HashSet<>(Util.noRehashCapacity(100));
		
		this.queryBaseSubs 			= queryBaseSubs;
		
		this.alignedPosProcessors	= alignedPosProcessors;
		this.covPosProcessors		= covPosProcessors;
		this.delPosProcessors		= delPosProcessors;
	}
	
	protected void processSE(
			final Set<BaseSubstitution> queryBaseSubs, 
			final SAMRecordExtended recordExtended) {
		
		final Set<BaseSubstitution> observedBaseSubs = collectBaseSubstitutions(
				queryBaseSubs, recordExtended);
		stratifyBaseCallCounts(observedBaseSubs, recordExtended);
	}

	private CoordinateController getController() {
		return sharedStorage.getCoordinateController();
	}
	
	private CoordinateTranslator getTranslator() {
		return getController().getCoordinateTranslator();
	}
	
	protected void stratifyBaseCallCounts(
			final Set<BaseSubstitution> observedBaseSubs, final SAMRecordExtended recordExtended) {
		
		if (observedBaseSubs.size() == 0) {
			return;
		}
		
		// process base call counts and store dependent on base substitutions
		final PositionProvider alignedPosProvider = new AllAlignmentBlocksPositionProvider(recordExtended, getTranslator());
		while (alignedPosProvider.hasNext()) {
			final Position alignedPos = alignedPosProvider.next();
			for (final BaseSubstitution baseSub : observedBaseSubs) {
				final PositionProcessor positionProcessor = alignedPosProcessors.get(baseSub);
				if (positionProcessor.checkValidators(alignedPos)) {
					positionProcessor.processStorages(alignedPos);
				}
			}
		}
		
		// process deletions and store dependent on base substitutions
		final PositionProvider delPosProvider = new AllDeletionsPositionProvider(recordExtended, getTranslator());
		while (delPosProvider.hasNext()) {
			final Position delPos = delPosProvider.next();
			for (final BaseSubstitution baseSub : observedBaseSubs) {
				final PositionProcessor positionProcessor = delPosProcessors.get(baseSub);
				if (positionProcessor.checkValidators(delPos)) {
					positionProcessor.processStorages(delPos);
				}
			}
		}
		
		// process coverage and store dependent on base substitutions
		final PositionProvider covPosProvider = new ConsumingReferencePositionProviderBuilder(recordExtended, getTranslator()).build();
		while (covPosProvider.hasNext()) {
			final Position covPos = covPosProvider.next();
			for (final BaseSubstitution baseSub : observedBaseSubs) {
				final PositionProcessor positionProcessor = covPosProcessors.get(baseSub);
				if (positionProcessor.checkValidators(covPos)) {
					positionProcessor.processStorages(covPos);
				}
			}
		}
		
	}
	
	protected void processPE(
			final Set<BaseSubstitution> queryBaseSubs, 
			final SAMRecordExtended recordExtended) {
		
		final SAMRecord record 	= recordExtended.getSAMRecord();
		final String readName 	= record.getReadName();
		
		final SamReader samReader = record.getFileSource().getReader();
		final SAMRecord mateRecord = samReader.queryMate(record);
		if (mateRecord.overlaps(getController().getActive())) { // mate within window
			if (! internalRecordExtended.containsKey(readName)) {
				internalRecordExtended.put(readName, recordExtended);
			} else {
				processInternalPE(queryBaseSubs, recordExtended);
			}
		} else { // outside
			externalRecordExtended.add(recordExtended);
		}
	}
	
	protected void processExternalPE(final Set<BaseSubstitution> queryBaseSubs) {
		for (final SAMRecordExtended recordExtended : externalRecordExtended) {
			final SAMRecord record = recordExtended.getSAMRecord();
			final SamReader samReader = record.getFileSource().getReader();
			final SAMRecord mateRecord = samReader.queryMate(record);
			if (mateRecord == null) {
				throw new IllegalStateException();
			}
			final SAMRecordExtended mateRecordExtended = new SAMRecordExtended(mateRecord);
			processPE(queryBaseSubs, recordExtended, mateRecordExtended);
		}
	}
	
	protected void processInternalPE(final Set<BaseSubstitution> queryBaseSubs, final SAMRecordExtended recordExtended) {
		final SAMRecord record = recordExtended.getSAMRecord();
		final String readName = record.getReadName();
		if (! internalRecordExtended.containsKey(readName)) {
			throw new IllegalStateException();
		}
		final SAMRecordExtended mate = internalRecordExtended.get(readName);
		processPE(queryBaseSubs, recordExtended, mate);
		internalRecordExtended.remove(readName);
	}
	
	protected void processPE(
			final Set<BaseSubstitution> queryBaseSubs, 
			final SAMRecordExtended recordExtended1,
			final SAMRecordExtended recordExtended2) {
		
		final Set<BaseSubstitution> obsBaseSubs = collectBaseSubstitutions(queryBaseSubs, recordExtended1);
		obsBaseSubs.addAll(collectBaseSubstitutions(queryBaseSubs, recordExtended2));
		stratifyBaseCallCounts(obsBaseSubs, recordExtended1);
		stratifyBaseCallCounts(obsBaseSubs, recordExtended2);
	}
	
	@Override
	public void preProcess() {
		// nothing to be done
	}
	
	@Override
	public void process(final SAMRecordExtended recordExtended) {
		final SAMRecord record = recordExtended.getSAMRecord();
		// if mate not mapped process it as it was SE
		if (record.getReadPairedFlag() && ! record.getMateUnmappedFlag()) {
			// ignore trans spliced reads
			if (! record.getReferenceName().equals(record.getMateReferenceName())) { 
				return;
			}
			processPE(queryBaseSubs, recordExtended);
		} else {
			processSE(queryBaseSubs, recordExtended);
		}
	}
	
	@Override
	public void postProcess() {
		processExternalPE(queryBaseSubs);
		internalRecordExtended.clear();
		externalRecordExtended.clear();
	}
	
	protected Set<BaseSubstitution> collectBaseSubstitutions(
			final Set<BaseSubstitution> queryBaseSubs, 
			final SAMRecordExtended recordExtended) {
		
		final Set<BaseSubstitution> foundBaseSubs 	= new HashSet<>(queryBaseSubs.size());
		final SAMRecord record 						= recordExtended.getSAMRecord();
		
		final int nm = record.getIntegerAttribute(SAMTag.NM.name());
		if (nm == 0) {
			return foundBaseSubs;
		}
		
		final String md = record.getStringAttribute(SAMTag.MD.name());
		if (md == null) {
			throw new IllegalStateException("No MD field for record: " + record.toString());
		}

		final MismatchPositionProvider mismatchPositionProvider = 
				new MismatchPositionProvider(recordExtended, getTranslator(), validator);
		
		while (mismatchPositionProvider.hasNext()) {
			final Position pos = mismatchPositionProvider.next();
			
			final int readMismatchPos 	= pos.getReadPosition();
			final Base readBase 		= bci.getReadBase(recordExtended, readMismatchPos);
			final int refPos 			= pos.getReferencePosition();
			final Base refBase 			= bci.getRefBase(recordExtended, refPos);
			
			final BaseSubstitution observedBaseSub = BaseSubstitution.bases2enum(refBase, readBase);
			if (queryBaseSubs.contains(observedBaseSub)) {
				foundBaseSubs.add(observedBaseSub);
			}
		}
		
		return foundBaseSubs;
	}

}
