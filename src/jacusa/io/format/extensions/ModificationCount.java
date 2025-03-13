package jacusa.io.format.extensions;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.util.Util;

import java.util.ArrayList;
import java.util.List;


public class ModificationCount extends AbstractParallelDataToString {

    public ModificationCount() {
    	super("modification_count", "Add modification count to output")
    }

    @Override
    public String toString(ParallelData parallelData){

        List<String> modificationCounts = new ArrayList<>();
        for (DataContainer combined : parallelData.getCombinedData()){
            modificationCounts.add(modCountToString(combined.getPileupCount().getModCount().getModCount()));
        }

        //output: modification_count=[ref1-mod1:count,mod2:count;ref2-mod1:count|ref1-mod1:count;ref2-mod1:count]
        return Util.pack(modificationCounts,'|');
    }

}

