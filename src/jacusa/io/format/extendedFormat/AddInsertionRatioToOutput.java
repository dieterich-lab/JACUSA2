package jacusa.io.format.extendedFormat;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.util.Util;

import java.util.ArrayList;
import java.util.List;

public class AddInsertionRatioToOutput implements ParallelToString {

    String id;
    String desc;

    public AddInsertionRatioToOutput(){
        id = "insertion_ratio";
        desc = "Add insertions to output";
    };

    public String getId(){
        return id;
    }

    public String getDesc(){
        return desc;
    }

    public String getStringFromParallel(ParallelData parallelData){

        List<Double> insertionRatios = new ArrayList<>();
        for (DataContainer combined : parallelData.getCombinedData()){
            final int reads = combined.getPileupCount().getReads();
            insertionRatios.add(combined.getPileupCount().getINDELCount().getInsertionRatio(reads));
        }

        return Util.pack(insertionRatios, ',');
    }

}
