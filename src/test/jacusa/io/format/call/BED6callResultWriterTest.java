package test.jacusa.io.format.call;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import jacusa.cli.parameters.CallParameter;
import jacusa.io.format.call.BED6callResultFormat;
import lib.cli.parameter.GeneralParameter;
import lib.io.BEDlikeResultFileWriter;
import test.jacusa.io.format.AbstractResultWriterTest;

class BED6callResultWriterTest extends AbstractResultWriterTest{

	public static final String PATH = AbstractResultWriterTest.PATH + "call/";
	public static final String HEAEDER_SUFFIX = "BED6callResultWriterTest.header";
	public static final String RESULT_SUFFIX = "BED6callResultWriterTest.result";
	
	/*
	 * Method Source
	 */

	public Stream<Arguments> testWriteResult() {
		// TODO implement JACUSA reader
		return Stream.of(
				Arguments.of() );
	}
	
	public Stream<Arguments> testWriteHeader() {
		final List<List<Integer>> list = Arrays.asList(
				Arrays.asList(1),
				Arrays.asList(2),
				Arrays.asList(3),
				Arrays.asList(1, 2),
				Arrays.asList(2, 2),
				Arrays.asList(1, 2, 3) );
	
		return IntStream.range(0, list.size())
				.mapToObj(i -> Arguments.of(list.get(i), PATH + "actual" + (i + 1) + HEAEDER_SUFFIX, PATH + "expected" + (i + 1) + HEAEDER_SUFFIX));
	}

	@Override
	public GeneralParameter createParameter(int conditionSize) {
		return new CallParameter(conditionSize);
	}

	@Override
	public BEDlikeResultFileWriter createTestInstance(
			String outputFileName, GeneralParameter parameter) {
		
		return new BED6callResultFormat("testCall", parameter).createWriter(outputFileName);
	}
	
}