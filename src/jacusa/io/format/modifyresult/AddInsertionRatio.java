package jacusa.io.format.modifyresult;

import lib.data.ParallelData;
import lib.data.count.PileupCount;
import lib.data.result.Result;

public class AddInsertionRatio extends AbstractResultModifier {

    public AddInsertionRatio() {
    	super("insertion_ratio", "Add insertion to output");
    }

    public void modify(Result result){
    	final ParallelData parallelData = result.getParellelData();

        for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); conditionIndex++) {
        	for (final int replicateIndex : parallelData.getReplicates()) {
        		final PileupCount pileupCount = parallelData.getDataContainer(conditionIndex, replicateIndex).getPileupCount(); 
        		final int reads = pileupCount.getReads();
        		result.getResultInfo().addReplicate(
        				conditionIndex, replicateIndex,
        				getID(),
        				pileupCount.getINDELCount().getInsertionRatio(reads));
        	}
        }
    }

}
