package lib.data.storage.readsubstitution;

import lib.util.Base;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.AllAlignmentBlocksPositionProvider;
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
import lib.data.storage.container.SharedStorage;
import lib.data.storage.processor.RecordExtendedPrePostProcessor;
import lib.data.stroage.Storage;
import lib.data.validator.Validator;

public class BaseSubstitutionRecordProcessor 
implements RecordExtendedPrePostProcessor {

	private final SharedStorage sharedStorage;
	
	private final BaseCallInterpreter bci;
	private final Validator validator;
	
	private final Map<BaseSubstitution, Storage> baseSub2cache;
	
	private final Map<String, SAMRecordExtended> internalRecordExtended;
	private final Set<SAMRecordExtended> externalRecordExtended;
	
	private final Set<BaseSubstitution> queryBaseSubs;

	public BaseSubstitutionRecordProcessor(
			final SharedStorage sharedStorage,
			final BaseCallInterpreter bci,
			final Validator validator,
			final Map<BaseSubstitution, Storage> baseSub2storage) {

		this.sharedStorage 		= sharedStorage;
		
		this.bci 				= bci;
		this.validator 			= validator;
		this.baseSub2cache 		= baseSub2storage;

		internalRecordExtended 	= new HashMap<>(100);
		externalRecordExtended 	= new HashSet<>(100);
		
		queryBaseSubs 			= baseSub2storage.keySet();
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
		final PositionProvider positionProvider = new AllAlignmentBlocksPositionProvider(
				recordExtended, getTranslator());
		
		while (positionProvider.hasNext()) {
			final Position pos = positionProvider.next();
			if (validator.isValid(pos)) {
				for (final BaseSubstitution observedBaseSub : observedBaseSubs) {
						baseSub2cache.get(observedBaseSub).increment(pos);
				}
			}
		}
	}
	
	protected void processPE(
			final Set<BaseSubstitution> queryBaseSubs, 
			final SAMRecordExtended recordExtended) {
		
		final SAMRecord record 	= recordExtended.getSAMRecord();
		final String readName 	= record.getReadName();
		
		if (record.contains(getController().getActive())) { // mate within window
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
		processExternalPE(baseSub2cache.keySet());
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
