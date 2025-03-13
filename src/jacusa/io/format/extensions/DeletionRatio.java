package jacusa.io.format.extensions;

import lib.data.DataContainer;
import lib.data.ParallelData;

import java.util.ArrayList;
import java.util.List;

public class DeletionRatio extends AbstractParallelDataToString {

    String id;
    String desc;

    public DeletionRatio() {
    	super("deletion_ratio", "Add deletions to output");
    };

    public String toString(ParallelData parallelData){

        List<Double> deletionRatios = new ArrayList<>();
        for (DataContainer combined : parallelData.getCombinedData()){
            final int reads = combined.getPileupCount().getReads();
            deletionRatios.add(combined.getPileupCount().getINDELCount().getDeletionRatio(reads));
        }

        return null;
        // FIXME return Util.pack(deletionRatios, ',');
    }

}
