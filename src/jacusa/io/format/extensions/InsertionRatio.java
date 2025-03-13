package jacusa.io.format.extensions;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.util.Util;

import java.util.ArrayList;
import java.util.List;

public class InsertionRatio extends AbstractParallelDataToString {

    String id;
    String desc;

    public InsertionRatio() {
    	super("insertion_ratio", "Add insertion to output");
    };

    public String toString(ParallelData parallelData){

        List<Double> deletionRatios = new ArrayList<>();
        for (DataContainer combined : parallelData.getCombinedData()){
            final int reads = combined.getPileupCount().getReads();
            deletionRatios.add(combined.getPileupCount().getINDELCount().getInsertionRatio(reads));
        }

        return Util.pack(deletionRatios, ',');
    }

}

