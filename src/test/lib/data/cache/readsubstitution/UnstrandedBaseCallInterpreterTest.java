package test.lib.data.cache.readsubstitution;

import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import lib.data.cache.readsubstitution.BaseCallInterpreter;
import lib.data.has.LibraryType;

class UnstrandedBaseCallInterpreterTest extends AbstractBaseCallInterpreterTest {

	public UnstrandedBaseCallInterpreterTest() {
		super(new BaseCallInterpreter.Builder(LibraryType.UNSTRANDED).build());
	}

	@Override
	Stream<Arguments> testGetReadBase() {
		return Stream.of(
				Arguments.of() );
	}

	@Override
	Stream<Arguments> testGetRefBase() {
		return Stream.of(
				Arguments.of() );
	}

}
