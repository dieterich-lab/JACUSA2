package jacusa.io.format.extensions;

import lib.data.ParallelData;


public class ModificationCount extends AbstractParallelDataToString {

    public ModificationCount() {
    	super("modification_count", "Add modification count to output");
    }

    @Override
    public String toString(ParallelData parallelData){

    	/* TODO
        List<String> modificationCounts = new ArrayList<>();
        for (DataContainer combined : parallelData.getCombinedData()){
            modificationCounts.add(modCountToString(combined.getPileupCount().getModCount().getModCount()));
        }
        */

        //output: modification_count=[ref1-mod1:count,mod2:count;ref2-mod1:count|ref1-mod1:count;ref2-mod1:count]
        // FIXME return Util.pack(modificationCounts,'|');
    	return null;
    }

}

