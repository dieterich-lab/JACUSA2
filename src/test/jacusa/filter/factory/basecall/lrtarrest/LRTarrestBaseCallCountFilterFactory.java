package test.jacusa.filter.factory.basecall.lrtarrest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jacusa.filter.factory.basecall.AbstractBaseCallCountFilterFactory;
import jacusa.filter.factory.basecall.CombinedFilterFactory;
import jacusa.filter.factory.basecall.INDEL_FilterFactory;
import jacusa.filter.factory.basecall.ReadPositionDistanceFilterFactory;
import jacusa.filter.factory.basecall.SpliceSiteFilterFactory;
import jacusa.filter.factory.basecall.lrtarrest.AbstractLRTarrestBaseCallCountFilterFactory;
import lib.data.DataType;
import lib.data.cache.fetcher.DefaultFilteredDataFetcher;
import test.utlis.CLIUtils;

/**
 * Tests @see jacusa.filter.factory.basecall.AbstractBaseCallCountFilterFactory
 */
@TestInstance(Lifecycle.PER_CLASS)
class LRTBaseCallCountFilterFactoryTest {

	private final String distance_LONG_OPT;
	private final String minRatio_LONG_OPT;
	
	public LRTBaseCallCountFilterFactoryTest() {
		distance_LONG_OPT = AbstractBaseCallCountFilterFactory.getDistanceOptionBuilder(-1).build().getLongOpt();
		minRatio_LONG_OPT = AbstractBaseCallCountFilterFactory.getMinRatioOptionBuilder(-1.0d).build().getLongOpt();
	}
	
	@ParameterizedTest(name = "Use factory: {0} and parse line: {1}")
	@MethodSource("testProcessCLICommandLine")
	void testProcessCLICommandLine(
			AbstractLRTarrestBaseCallCountFilterFactory testInstance,
			String line, 
			int expectedDistance, double expectedMinRatio) throws ParseException {
		
		testInstance.processCLI(line);
		
		final int actualDistance = testInstance.getFilterDistance();
		assertEquals(expectedDistance, actualDistance);
		
		final double actualMinRatio = testInstance.getFilterMinRatio();
		assertEquals(expectedMinRatio, actualMinRatio);
	}

	@Test
	void testProcessCLICommandLineFails() {
		for (final AbstractBaseCallCountFilterFactory testInstance : getFactories()) {
		
			// < 0
			assertThrows(IllegalArgumentException.class,
					() -> {
						final String line = createLine(-1, 0.1);
						testInstance.processCLI(line);								
					});
			// < 0.0
			assertThrows(IllegalArgumentException.class,
					() -> {
						final String line = createLine(1, -0.5);
						testInstance.processCLI(line);								
					});
			// > 1.0
						assertThrows(IllegalArgumentException.class,
								() -> {
									final String line = createLine(1, 1.1);
									testInstance.processCLI(line);								
								});
			
			// wrong 
			assertThrows(IllegalArgumentException.class,
					() -> {
						final String line = createLine("wrong", Double.toString(0.1));
						testInstance.processCLI(line);								
					});
			// wrong 
			assertThrows(IllegalArgumentException.class,
					() -> {
						final String line = createLine(Integer.toString(1), "wrong");
						testInstance.processCLI(line);								
					});
		}
	}
	
	Stream<Arguments> testProcessCLICommandLine() {
		final List<Arguments> arguments = new ArrayList<>();
		
		for (final AbstractBaseCallCountFilterFactory testInstance : getFactories()) {
			arguments.add(createArguments(testInstance, 1, 0.1));
			arguments.add(createArguments(testInstance, 3, 0.5));
		}

		return arguments.stream();
	}
	
	Arguments createArguments(
			final AbstractBaseCallCountFilterFactory testInstance,
			final int distance, final double minRatio) {
		
		return Arguments.of(
				testInstance,
				createLine(distance, minRatio),
				distance, minRatio);
	}
	
	String createLine(final int distance, final double minRatio) {
		return createLine(
				Integer.toString(distance), 
				Double.toString(minRatio));
	}
	
	String createLine(final String distance, final String minRatio) {
		return new StringBuilder()
				.append(CLIUtils.pr(distance_LONG_OPT, distance))
				.append(' ')
				.append(CLIUtils.pr(minRatio_LONG_OPT, minRatio))
				.toString();
	}
	
	List<AbstractBaseCallCountFilterFactory> getFactories() {
		return Arrays.asList(
				new CombinedFilterFactory(
						DataType.BCC.getFetcher(), 
						new DefaultFilteredDataFetcher<>(DataType.F_BCC)),
				
				new INDEL_FilterFactory(
						DataType.BCC.getFetcher(), 
						new DefaultFilteredDataFetcher<>(DataType.F_BCC)),
				
				new ReadPositionDistanceFilterFactory(
						DataType.BCC.getFetcher(), 
						new DefaultFilteredDataFetcher<>(DataType.F_BCC)),
				
				new SpliceSiteFilterFactory(
						DataType.BCC.getFetcher(), 
						new DefaultFilteredDataFetcher<>(DataType.F_BCC)) );
	}
	
}
