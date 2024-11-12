package jacusa.io.format.extendedFormat;

import lib.data.ParallelData;

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
        return "Deletion_Ratio_TODO";
    }

}

