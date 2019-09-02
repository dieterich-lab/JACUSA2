package test.lib.cli.options.condition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import lib.cli.options.condition.AbstractConditionACOption;
import lib.cli.parameter.ConditionParameter;

@TestInstance(Lifecycle.PER_CLASS)
public abstract class AbstractConditionACOptionTest<T> {

	@ParameterizedTest(name = "Parse line: {2}")
	@MethodSource("testProcessGeneral")
	void testProcessGeneral(
			AbstractConditionACOption testInstance,
			List<ConditionParameter> conditionParameters, 
			String line, 
			T expected) throws Exception {
		
		final CommandLineParser parser = new DefaultParser();
		final Options options = new Options();
		options.addOption(testInstance.getOption(false));
		
		final CommandLine cmd = parser.parse(options, line.split(" "));
		process(testInstance, cmd);
		
		for (final ConditionParameter conditionParameter : conditionParameters) {
			final T actual = getActualValue(conditionParameter);
			assertEquals(expected, actual);
		}
	}

	@ParameterizedTest(name = "Parse line: {2}")
	@MethodSource("testProcessIndividual")
	void testProcessIndividual(
			List<AbstractConditionACOption> testInstances,
			List<ConditionParameter> conditionParameters,
			String line, 
			List<T> expected) throws Exception {
		
		final CommandLineParser parser = new DefaultParser();
		final Options options = new Options();
		for (final AbstractConditionACOption testInstance : testInstances) {
			options.addOption(testInstance.getOption(false));
		}
		
		final CommandLine cmd = parser.parse(options, line.split(" "));
		for (final AbstractConditionACOption testInstance : testInstances) {
			process(testInstance, cmd);
		}
		
		for (final ConditionParameter conditionParameter : conditionParameters) {
			final T actual = getActualValue(conditionParameter);
			assertEquals(expected.get(conditionParameter.getConditionIndex() - 1), actual);
		}
	}

	void process(final AbstractConditionACOption testInstance, final CommandLine cmd) throws Exception {
		if (! testInstance.getOpt().isEmpty() && cmd.hasOption(testInstance.getOpt())) {
			testInstance.process(cmd);
		}
	}

	List<AbstractConditionACOption> createTestInstances(final List<ConditionParameter> conditionParameters) {
		return IntStream.rangeClosed(1, conditionParameters.size())
				.mapToObj(i -> createIndividualTestInstance(conditionParameters.get(i - 1)))
				.collect(Collectors.toList());
	}

	protected Arguments createIndividualArguments(
			final T defaultValue, 
			final List<Integer> conditions, 
			final List<T> actualValues) {

		final ArgumentsBuilder builder = new ArgumentsBuilder(conditions.size(), defaultValue);
		for (final int condition : conditions) {
			builder.withCondition(condition, actualValues.get(condition - 1));
		}
		return builder.build();
				
	}
	
	protected Arguments createGeneralArguments(final T defaultValue, final int conditions, final T actualValue) {
		return new ArgumentsBuilder(conditions, defaultValue)
				.withConditions(actualValue)
				.build();
	}

	protected <E extends Throwable> void myAssertOptThrows(
			final Class<E> expectedType,
			final AbstractConditionACOption testInstance,
			final String value) {

		final Options options				= new Options();
		options.addOption(testInstance.getOption(false));
		final CommandLineParser parser		= new DefaultParser();
		final String line 					= createOptLine(testInstance, value);
		Executable executable 				= () -> {
			final CommandLine cmd = parser.parse(options, line.split(" "));
			testInstance.process(cmd); 
			};
		assertThrows(expectedType, executable);
	}
	
	ConditionParameter createConditionParameter(final int condition) {
		return new ConditionParameter(condition);
	}
	
	protected List<ConditionParameter> createConditionParameters(final int conditions) {
		return IntStream.rangeClosed(1, conditions)
			.mapToObj(i -> createConditionParameter(i))
			.collect(Collectors.toList());
	}
	
	String createOptLine(final AbstractConditionACOption testInstance, String value) {
		return createOptLine(testInstance.getOpt(), value);
	}
	
	String createOptLine(final AbstractConditionACOption testInstance, T value) {
		return createOptLine(testInstance.getOpt(), convertString(value));
	}
	
	String createOptLine(final String opt, final String value) {
		if (value.isEmpty()) {
			return " -" + opt;
		}
		return " -" + opt + " " + value;
	}

	protected T getDefaultValue() {
		return getActualValue(new ConditionParameter(-1));
	}
	
	protected abstract Stream<Arguments> testProcessGeneral();
	protected abstract Stream<Arguments> testProcessIndividual();
	
	protected abstract AbstractConditionACOption createGeneralTestInstance(List<ConditionParameter> conditionParameters);
	protected abstract AbstractConditionACOption createIndividualTestInstance(ConditionParameter conditionParameter);
	protected abstract T getActualValue(ConditionParameter conditionParameter);

	protected abstract String convertString(T value);
	
	public class ArgumentsBuilder implements lib.util.Builder<Arguments> {
		
		private final List<ConditionParameter> conditionParameters;
		private final List<T> expected;
		private final List<Integer> conditions;
		
		// make sure we don't mix individual and general build
		private int flag = 0; // 0 => unknown, 1 => individual => 2 general 
		
		public ArgumentsBuilder(final int conditions, final T expected) {
			conditionParameters = createConditionParameters(conditions);
			this.expected 		= new ArrayList<>(Collections.nCopies(conditions, expected));
			this.conditions		= new ArrayList<>();
		}
		
		ArgumentsBuilder withConditions(final T value) {
			assert(flag != 1);
			expected.clear();
			expected.addAll(Collections.nCopies(conditionParameters.size(), value));
			flag = 2;
			return this;
		}
		
		ArgumentsBuilder withCondition(final int condition, final T value) {
			assert(flag != 2);
			assert(condition - 1 < conditionParameters.size());
			expected.set(condition - 1, value);
			conditions.add(condition);
			flag = 1;
			return this;
		}
		
		Arguments createGeneralArguments(
				final List<ConditionParameter> conditionParameters, 
				final T expected) {
			
			final AbstractConditionACOption testInstance = createGeneralTestInstance(conditionParameters);
			final String line = createOptLine(testInstance, expected);
			return Arguments.of(
					testInstance,
					conditionParameters,
					line, 
					expected);
		}
		
		Arguments createIndividualArguments(
				final List<ConditionParameter> conditionParameters,
				final List<Integer> conditions,
				final List<T> expected) {
			
			final List<AbstractConditionACOption> testInstances = 
					createTestInstances(conditionParameters);
			
			final StringBuilder sb = new StringBuilder();
			for (int condition : conditions) {
				sb.append(createOptLine(
						  testInstances.get(condition - 1), 
						  expected.get(condition - 1)) );
			}
			
			return Arguments.of(
					testInstances,
					conditionParameters,
					sb.toString(), 
					expected);
		}
		
		@Override
		public Arguments build() {
			switch (flag) {
			case 0:

			case 1:
				return createIndividualArguments(conditionParameters, conditions, expected);
				
			case 2:
				return createGeneralArguments(conditionParameters, expected.get(0));
				
			default:
				throw new IllegalStateException();
			}
			
		}
		
	}
	
}
