package test.lib.data.storage.readsubstitution;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import lib.data.storage.readsubstitution.BaseCallInterpreter;
import lib.util.Base;
import lib.util.LibraryType;

class UnstrandedBaseCallInterpreterTest extends AbstractBaseCallInterpreterTest {
	
	public UnstrandedBaseCallInterpreterTest() {
		super(BaseCallInterpreter.build(LibraryType.UNSTRANDED));
	}

	@Override
	Stream<Arguments> testGetReadBase() {
		// ACGAACGT
		final List<Arguments> arguments = new ArrayList<Arguments>();
		for (final boolean negStrand : new boolean[] { true, false }) {
			arguments.add(createReadArgs(1, negStrand, "2M", "AG", 0, Base.A));
			arguments.add(createReadArgs(1, negStrand, "2M", "AG", 1, Base.G));
		}
		return arguments.stream();
	}

	@Override
	Stream<Arguments> testGetRefBase() {
		// ACGAACGT
		final List<Arguments> arguments = new ArrayList<Arguments>();
		for (final boolean negativeStrand : new boolean[] { true, false }) {
			arguments.add(createRefArgs(1, negativeStrand, "2M", "AG", 1, Base.A));
			arguments.add(createRefArgs(1, negativeStrand, "2M", "AG", 2, Base.C));
		}
		return arguments.stream();
	}

}
