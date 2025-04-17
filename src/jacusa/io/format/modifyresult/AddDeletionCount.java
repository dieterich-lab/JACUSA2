package jacusa.io.format.modifyresult;

import lib.cli.parameter.GeneralParameter;
import lib.data.ParallelData;
import lib.data.count.PileupCount;
import lib.data.result.Result;

public class AddDeletionCount extends AbstractResultModifier {

    public AddDeletionCount() {
    	super("deletions", "Add deletion count to output");
    }

    public void modify(Result result){
    	final ParallelData parallelData = result.getParellelData();

        for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); conditionIndex++) {
        	for (int replicateIndex = 0; replicateIndex < parallelData.getReplicates(conditionIndex); replicateIndex++) {
        		final PileupCount pileupCount = parallelData.getDataContainer(conditionIndex, replicateIndex).getPileupCount(); 
        		result.getResultInfo().add(
        				getID(),
        				conditionIndex,
        				replicateIndex, 
        				Integer.toString(pileupCount.getINDELCount().getDeletionCount()));
        	}
        }
    }

    @Override
    public void registerKeys(GeneralParameter parameter) {
    	parameter.registerConditionReplictaKeys(getID());
    }
}
