package jacusa.io.format.call;

import lib.cli.parameter.GeneralParameter;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBCC;
import lib.io.AbstractResultFileFormat;
import lib.io.BEDlikeResultFileWriter;
import lib.io.InputOutput;
import lib.io.BEDlikeResultFileWriter.BEDlikeResultFileWriterBuilder;
import lib.io.format.bed.BED6adder;
import lib.io.format.bed.DataAdder;
import lib.io.format.bed.DefaultBED6adder;
import lib.io.format.bed.DefaultInfoAdder;

import java.util.HashSet;
import java.util.Set;

import jacusa.io.format.DefaultDataAdder;
import jacusa.io.format.extensions.ParallelDataToString;

/*
 * TODO move to method:
 * available.add(new AddInsertionRatioToOutput());
        available.add(new AddDeletionRatioToOutput());
        available.add(new AddModificationCountToOutput());
 */

public class BED6callExtendedResultFormat extends AbstractResultFileFormat {

    //available options for output-format X
    private Set<ParallelDataToString> available;

    //options selected in command line for output-format X
    private Set<ParallelDataToString> selected;

    public BED6callExtendedResultFormat(
            final String methodName,
            final GeneralParameter parameter,
            final Set<ParallelDataToString> available){
        super('X', "BED6-extended result format", methodName, parameter);

        this.available = new HashSet<ParallelDataToString>(available);
        this.selected = new HashSet<>(selected.size());
    }

    // TODO check
    //implemented functions of ResultFormat IntF
    public void processCLI(String line) {
        //splits at ','
        final String[] args = line.split(Character.toString(InputOutput.SEP4));

        //checks which options found in available are selected in command line and copies them to selected
        for(String arg : args){
            for(ParallelDataToString extension : available){
                if(arg == extension.getID()){
                    selected.add(extension);
                }
            }
        }
    }

    @Override
	public BEDlikeResultFileWriter createWriter(final String outputFileName) {
		final BaseCallCount.AbstractParser bccParser = 
				new DefaultBCC.Parser(InputOutput.VALUE_SEP, InputOutput.EMPTY_FIELD);
		
		return new BEDlikeResultFileWriterBuilder(outputFileName, getParameter())
				.addBED6Adder(new DefaultBED6adder(getMethodName(), "score"))
				.addDataAdder(new DefaultDataAdder(bccParser))
				.addInfoAdder(new ExtendedInfoAdder(getParameter()))
				.build();
	}
    
    public Set<ParallelDataToString> getAvailableExtenstions() {
        return available;
    }

    public Set<ParallelDataToString> getSelectedExtensions() {
        return selected;
    }
}
