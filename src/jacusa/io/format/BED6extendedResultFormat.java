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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.cli.DefaultParser;

import jacusa.io.format.modifyresult.AddBCQC;
import jacusa.io.format.modifyresult.AddDeletionRatio;
import jacusa.io.format.modifyresult.AddInsertionRatio;
import jacusa.io.format.modifyresult.AddReadCount;
import jacusa.io.format.modifyresult.ResultModifier;
import jacusa.io.format.modifyresult.ResultModifierOption;

public class BED6extendedResultFormat
extends AbstractResultFileFormat 
implements HasProcessCommandLine {

	private final List<ResultModifier> DEFAULT = 
			Collections.unmodifiableList(
					Arrays.asList(
							new AddReadCount(),
							new AddBCQC(),
							new AddInsertionRatio(),
							new AddDeletionRatio()));
	
    private ProcessCommandLine processingCommandLine; 
    
    public BED6extendedResultFormat(final String methodName, final GeneralParameter parameter){
        super('X', "BED6-extended result format", methodName, parameter);

        this.processingCommandLine 	= new ProcessCommandLine(
        		new DefaultParser(),
        		DEFAULT.stream()
        		.map(rm -> new ResultModifierOption(rm, parameter.getResultModifiers()))
        		.collect(Collectors.toList()));
    }
   
    @Override
    public ProcessCommandLine getProcessCommandLine() {
    	return processingCommandLine;
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
    
}
