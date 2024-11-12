package jacusa.io.format.extendedFormat;

import lib.data.ParallelData;

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
        return "Insertion_Ratio_TODO";
    }

}
