package jacusa.io.format.modifyresult;

import lib.cli.parameter.GeneralParameter;
import lib.data.ParallelData;
import lib.data.count.PileupCount;
import lib.data.result.Result;
import lib.util.Base;

public class AddNonReferenceRatio extends AbstractResultModifier {

    public AddNonReferenceRatio() {
    	super("nonref_ratio", "Add non-reference ratio to output");
    }

    public void modify(Result result){
    	final ParallelData parallelData = result.getParellelData();
    	final Base refBase = parallelData.getCombPooledData().getAutoRefBase();
        for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); conditionIndex++) {
        	for (int replicateIndex = 0; replicateIndex < parallelData.getReplicates(conditionIndex); replicateIndex++) {
        		final PileupCount pileupCount = parallelData.getDataContainer(conditionIndex, replicateIndex).getPileupCount();
        		result.getResultInfo().add(
        				getID(), 
        				conditionIndex,
        				replicateIndex,
        				Double.toString(pileupCount.getBCC().getNonRefRatio(refBase)));
        	}
        }
    }
    
    @Override
    public void registerKeys(GeneralParameter parameter) {
    	parameter.registerConditionReplictaKeys(getID());
    }
}
