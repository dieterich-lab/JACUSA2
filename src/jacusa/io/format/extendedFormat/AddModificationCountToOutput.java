package jacusa.io.format.extendedFormat;

import lib.data.DataContainer;
import lib.data.ParallelData;

import java.util.ArrayList;
import java.util.List;

import static lib.data.count.ModificationCount.modCountToString;

public class AddModificationCountToOutput implements ParallelToString {

    String id;
    String desc;

    public AddModificationCountToOutput(){
        id = "modification_count";
        desc = "Add modification count to output";
    };

    public String getId(){
        return id;
    }

    public String getDesc(){
        return desc;
    }

    public String getStringFromParallel(ParallelData parallelData){

        List<String> modificationCounts = new ArrayList<>();
        for (DataContainer combined : parallelData.getCombinedData()){
            //final int reads = combined.getPileupCount().getReads();
            modificationCounts.add(modCountToString(combined.getPileupCount().getModCount().getModCount()));
        }

        return modificationCounts.toString();
        //return "modCount_TODO";
    }

}

