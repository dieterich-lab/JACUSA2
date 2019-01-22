package test.jacusa.cli.parameters;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jacusa.cli.parameters.CallParameter;
import jacusa.cli.parameters.HasStatParameter;
import jacusa.cli.parameters.LRTarrestParameter;
import jacusa.cli.parameters.PileupParameter;
import jacusa.cli.parameters.RTarrestParameter;

/**
 * Tests @see jacusa.cli.parameters.HasStatParameter#getStatParameter()
 */
class HasStatParameterTest {

	@ParameterizedTest(name = "{arguments}")
	@MethodSource("testgetStatParameter")
	void testgetStatParameter(HasStatParameter testInstance) {
		assertNotNull(testInstance.getStatParameter());
	}
	
	static Stream<Arguments> testgetStatParameter() {
		return getHasStatParameters().stream()
				.map(Arguments::of);
	}
	
	public static List<HasStatParameter> getHasStatParameters() {
		return Arrays.asList(
				new CallParameter(0),
				new LRTarrestParameter(0),
				new PileupParameter(0),
				new RTarrestParameter(0) );
	}
	
}
