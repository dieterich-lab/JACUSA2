package jacusa.io.format.extendedFormat;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.util.Util;

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
            modificationCounts.add(modCountToString(combined.getPileupCount().getModCount().getModCount()));
        }

        //output: modification_count=[ref1-mod1:count,mod2:count;ref2-mod1:count|ref1-mod1:count;ref2-mod1:count]
        return Util.pack(modificationCounts,'|');
    }

}

