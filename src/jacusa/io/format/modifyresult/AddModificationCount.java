package jacusa.io.format.modifyresult;

import lib.cli.parameter.GeneralParameter;
import lib.data.result.Result;


public class AddModificationCount extends AbstractResultModifier {

    public AddModificationCount() {
    	super("modification_count", "Add modification count to output");
    }
    
    public void modify(Result result){

    	/* TODO implement
    	 * final ParallelData parallelData = result.getParellelData();
        List<String> modificationCounts = new ArrayList<>();
        for (DataContainer combined : parallelData.getCombinedData()){
            modificationCounts.add(modCountToString(combined.getPileupCount().getModCount().getModCount()));
        }
        */

        //output: modification_count=[ref1-mod1:count,mod2:count;ref2-mod1:count|ref1-mod1:count;ref2-mod1:count]
        // FIXME return Util.pack(modificationCounts,'|');
    }

    @Override
    public void registerKeys(GeneralParameter parameter) {
    	parameter.registerConditionReplictaKeys(getID());
    }
    
}

