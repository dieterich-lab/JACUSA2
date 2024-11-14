package jacusa.io.format.extendedFormat;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.util.Util;

import java.util.ArrayList;
import java.util.List;

public class AddDeletionRatioToOutput implements ParallelToString {

    String id;
    String desc;

    public AddDeletionRatioToOutput(){
        id = "deletion_ratio";
        desc = "Add deletions to output";
    };

    public String getId(){
        return id;
    }

    public String getDesc(){
        return desc;
    }

    public String getStringFromParallel(ParallelData parallelData){

        List<Double> deletionRatios = new ArrayList<>();
        for (DataContainer combined : parallelData.getCombinedData()){
            final int reads = combined.getPileupCount().getReads();
            deletionRatios.add(combined.getPileupCount().getINDELCount().getDeletionRatio(reads));
        }

        return Util.pack(deletionRatios, ',');
    }

}

