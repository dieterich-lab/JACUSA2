package test.lib.cli.options.filter;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import jacusa.filter.factory.HomopolymerFilterFactory;
import lib.cli.options.AbstractProcessingOption;
import lib.cli.options.filter.HomopolymerMethodOption;
import lib.cli.options.filter.has.HasHomopolymerMethod;
import lib.cli.options.filter.has.HasHomopolymerMethod.HomopolymerMethod;
import test.lib.cli.options.OptionTest;

/**
 * Tests @see lib.cli.options.filter.HomopolymerMethodOption#process(org.apache.commons.cli.CommandLine)
 */
class HomopolymerMethodOptionTest 
implements OptionTest<HomopolymerMethod> {

	private TestHasHomopolymerMethod hasHomopolymerMethod;
	
	@BeforeEach
	void beforeEach() {
		hasHomopolymerMethod = 
				new TestHasHomopolymerMethod(HomopolymerFilterFactory.HOMOPOLYMER_METHOD);
	}
	
	@DisplayName("Check processCLI fails on wrong input")
	@Test
	void testProcessFails() throws Exception {
		// not a method
		myAssertLongOptThrows(IllegalArgumentException.class, "wrong");
	}

	@Override
	public Stream<Arguments> testProcess() {
		return Arrays.asList(HomopolymerMethod.values()).stream()
				.map(m -> createArguments(m));
	}
	
	Arguments createArguments(final HomopolymerMethod method) {
		return Arguments.of(
				createLongOptLine(method.toString()),
				method);
	}

	@Override
	public AbstractProcessingOption createTestInstance() {
		return new HomopolymerMethodOption(hasHomopolymerMethod);
	}
	
	@Override
	public HomopolymerMethod getActualValue() {
		return hasHomopolymerMethod.getHomopolymerMethod();
	}
	
	private class TestHasHomopolymerMethod implements HasHomopolymerMethod {

		private HomopolymerMethod method;
		
		public TestHasHomopolymerMethod(final HomopolymerMethod method) {
			this.method = method;
		}
		
		@Override
		public HomopolymerMethod getHomopolymerMethod() {
			return method;
		}

		@Override
		public void setHomopolymerMethod(final HomopolymerMethod method) {
			this.method = method;
		}
		
	}
	
}
