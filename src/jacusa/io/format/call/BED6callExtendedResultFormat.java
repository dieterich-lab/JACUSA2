package jacusa.io.format.call;

import jacusa.io.format.extendedFormat.AddDeletionRatioToOutput;
import jacusa.io.format.extendedFormat.AddInsertionRatioToOutput;
import jacusa.io.format.extendedFormat.AddModificationCountToOutput;
import jacusa.io.format.extendedFormat.ParallelToString;
import lib.cli.parameter.GeneralParameter;
import lib.io.InputOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BED6callExtendedResultFormat extends BED6callResultFormat{

    //available options for output-format X
    private List<ParallelToString> available;

    //options selected in command line for output-format X
    private List<ParallelToString> selected;

    public BED6callExtendedResultFormat(
            final String methodName,
            final GeneralParameter parameter){
        super('X', "BED6-extended result format", methodName, parameter);

        available = new ArrayList<>();
        available.add(new AddInsertionRatioToOutput());
        available.add(new AddDeletionRatioToOutput());
        available.add(new AddModificationCountToOutput());
        selected = new ArrayList<>();
    }



    //implemented functions of ResultFormat IntF
    @Override
    public void processCLI(String line) {

        //splits at ','
        final String[] args = line.split(Character.toString(InputOutput.SEP4));

        //checks which options found in available are selected in command line and copies them to selected
        for(String arg : args){
            for(ParallelToString p : available){
                if(Objects.equals(arg, p.getId())){
                    selected.add(p);
                }
            }
        }

    }

    @Override
    public List<ParallelToString> getAvailable() {
        return available;
    }

    @Override
    public List<ParallelToString> getSelected() {
        return selected;
    }
}
