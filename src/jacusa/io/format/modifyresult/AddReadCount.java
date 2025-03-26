package jacusa.io.format.modifyresult;

import lib.cli.parameter.GeneralParameter;
import lib.data.ParallelData;
import lib.data.count.PileupCount;
import lib.data.result.Result;

public class AddReadCount extends AbstractResultModifier {

    public AddReadCount() {
    	super("reads", "Add total number of reads including basecalls, insertions and deletions.");
    }

    public void modify(Result result){
    	final ParallelData parallelData = result.getParellelData();

        for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); conditionIndex++) {
        	for (final int replicateIndex : parallelData.getReplicates()) {
        		final PileupCount pileupCount = parallelData.getDataContainer(conditionIndex, replicateIndex).getPileupCount(); 
        		final int reads = pileupCount.getReads();
        		result.getResultInfo().add(
        				getID() + conditionIndex + replicateIndex,
        				Integer.toString(reads));
        	}
        }
    }

    @Override
    public void registerKeys(GeneralParameter parameter) {
    	parameter.registerConditionReplictaKeys(getID());
    }
}
