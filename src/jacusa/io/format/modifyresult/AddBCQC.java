package jacusa.io.format.modifyresult;

import lib.data.ParallelData;
import lib.data.count.PileupCount;
import lib.data.count.basecallquality.BaseCallQualityCount;
import lib.data.result.Result;
import lib.util.Base;

public class AddBCQC extends AbstractResultModifier {

    public AddBCQC() {
    	super("bcqc", "Add basecall and quality score counts");
    }

    public void modify(Result result){
    	final ParallelData parallelData = result.getParellelData();

        for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); conditionIndex++) {
        	for (final int replicateIndex : parallelData.getReplicates()) {
        		final PileupCount pileupCount = parallelData.getDataContainer(conditionIndex, replicateIndex).getPileupCount();
        		final BaseCallQualityCount bcqc = pileupCount.getBaseCallQualityCount();
        		final StringBuilder sb = new StringBuilder();

        		boolean check1 = false;
				boolean check2 = false;
				for (final Base base: Base.validValues()) {
					if (check1) {
						sb.append(',');
					}
					for (final Byte qual : bcqc.getBaseCallQuality(base)) {
						if (check2) {
							sb.append('&');
						}
						sb.append(qual);
						sb.append(':');
						sb.append(bcqc.getBaseCallQuality(base, qual));
						check2 = true;
					}
					check1 = true;
				}
        		result.getResultInfo().add(
        				getID() + conditionIndex + replicateIndex,
        				sb.toString());
        	}
        }
    }

}
