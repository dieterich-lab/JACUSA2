package jacusa.io.format;

import lib.cli.parameter.GeneralParameter;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBCC;
import lib.data.has.HasProcessCommandLine;
import lib.io.AbstractResultFileFormat;
import lib.io.BEDlikeResultFileWriter;
import lib.io.InputOutput;
import lib.io.BEDlikeResultFileWriter.BEDlikeResultFileWriterBuilder;
import lib.io.format.bed.DefaultBED6adder;
import lib.io.format.bed.ExtendedInfoAdder;
import lib.stat.dirmult.ProcessCommandLine;

import java.util.Collections;
import java.util.List;

import jacusa.io.format.modifyresult.ResultModifier;

public class BED6extendedResultFormat
extends AbstractResultFileFormat 
implements HasProcessCommandLine {

    //options selected in command line for output-format X
    private List<ResultModifier> selectedResultModifier;

    private ProcessCommandLine processingCommandLine; 
    
    public BED6extendedResultFormat(
            final String methodName,
            final GeneralParameter parameter,
            final List<ResultModifier> selected,
            final ProcessCommandLine processingCommandLine){
        super('X', "BED6-extended result format", methodName, parameter);

        this.selectedResultModifier = Collections.unmodifiableList(selected);
    }

    @Override
    public ProcessCommandLine getProcessCommandLine() {
    	return processingCommandLine;
    }
    
    // TODO mode to - change to 
    //implemented functions of ResultFormat IntF
    /*
    public void processCLI(String[] args) {
        //splits at ','
        final String[] args = line.split(Character.toString(InputOutput.SEP4));
    }
    */

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

    public List<ResultModifier> getSelectedResultModifier() {
        return selectedResultModifier;
    }

}
