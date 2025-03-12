package jacusa.io.format.call;

import jacusa.io.format.extendedFormat.ParallelDataToString;
import lib.cli.parameter.GeneralParameter;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBCC;
import lib.io.AbstractResultFormat;
import lib.io.BEDlikeResultFileWriter;
import lib.io.InputOutput;
import lib.io.BEDlikeResultFileWriter.BEDlikeResultFileWriterBuilder;
import lib.io.format.bed.BED6adder;
import lib.io.format.bed.DataAdder;
import lib.io.format.bed.DefaultBED6adder;
import lib.io.format.bed.DefaultInfoAdder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * TODO move to method:
 * available.add(new AddInsertionRatioToOutput());
        available.add(new AddDeletionRatioToOutput());
        available.add(new AddModificationCountToOutput());
 */

public class BED6callExtendedResultFormat extends AbstractResultFormat {

    //available options for output-format X
    private List<ParallelDataToString> available;

    //options selected in command line for output-format X
    private List<ParallelDataToString> selected;

    public BED6callExtendedResultFormat(
            final String methodName,
            final GeneralParameter parameter,
            final List<ParallelDataToString> available){
        super('X', "BED6-extended result format", methodName, parameter);

        this.available = new ArrayList<ParallelDataToString>(available);
        this.selected = new ArrayList<>(selected.size());
    }

    //implemented functions of ResultFormat IntF
    public void processCLI(String line) {

        //splits at ','
        final String[] args = line.split(Character.toString(InputOutput.SEP4));

        //checks which options found in available are selected in command line and copies them to selected
        for(String arg : args){
            for(ParallelDataToString p : available){
                if(Objects.equals(arg, p.getId())){
                    selected.add(p);
                }
            }
        }

    }

	@Override
	public BEDlikeResultFileWriter createWriter(final String outputFileName) {
		final BaseCallCount.AbstractParser bccParser = 
				new DefaultBCC.Parser(InputOutput.VALUE_SEP, InputOutput.EMPTY_FIELD);
		
		BED6adder bed6adder = new DefaultBED6adder(getMethodName(), "score");
		DataAdder dataAdder = new CallDataAdder(bccParser);
		final BEDlikeResultFileWriterBuilder builder = new BEDlikeResultFileWriterBuilder(outputFileName, getParameter());

		builder.addBED6Adder(bed6adder);
		builder.addDataAdder(dataAdder);
		builder.addInfoAdder(new DefaultInfoAdder(getParameter()));
		return builder.build();
	}
    
    public List<ParallelDataToString> getAvailable() {
        return available;
    }

    public List<ParallelDataToString> getSelected() {
        return selected;
    }

}
