package test.jacusa.io.format.rtarrest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import jacusa.cli.parameters.RTarrestParameter;
import jacusa.io.format.rtarrest.BED6rtArrestResultFormat;
import lib.cli.parameter.GeneralParameter;
import lib.io.BEDlikeResultFileWriter;
import test.jacusa.io.format.AbstractResultWriterTest;

class BED6rtArrestResultWriterTest extends AbstractResultWriterTest {

	public static final String PATH = AbstractResultWriterTest.PATH + "rtarrest/";
	public static final String HEAEDER_SUFFIX = "BED6rtArrestResultWriterTest.header";
	public static final String RESULT_SUFFIX = "BED6rtArrestResultWriterTest.result";
	
	public Stream<Arguments> testWriteHeader() {
		final List<List<Integer>> list = Arrays.asList(
				Arrays.asList(2, 2),
				Arrays.asList(3, 4) );
	
		return IntStream.range(0, list.size())
				.mapToObj(i -> Arguments.of(list.get(i), PATH + "actual" + (i + 1) + HEAEDER_SUFFIX, PATH + "expected" + (i + 1) + HEAEDER_SUFFIX));
	}
	
	@Override
	public Stream<Arguments> testWriteResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GeneralParameter createParameter(int conditionSize) {
		return new RTarrestParameter(conditionSize);
	}

	@Override
	public BEDlikeResultFileWriter createTestInstance(
			String outputFileName,
			GeneralParameter parameter) {

		return new BED6rtArrestResultFormat("testRT", parameter).createWriter(outputFileName);
	}

}
