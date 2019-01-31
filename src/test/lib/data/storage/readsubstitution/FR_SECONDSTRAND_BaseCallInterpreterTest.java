package test.lib.data.storage.readsubstitution;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import lib.data.storage.readsubstitution.BaseCallInterpreter;
import lib.util.Base;
import lib.util.LibraryType;

class FR_SECONDSTRAND_BaseCallInterpreterTest extends AbstractBaseCallInterpreterTest {
	
	public FR_SECONDSTRAND_BaseCallInterpreterTest() {
		super(BaseCallInterpreter.build(LibraryType.FR_SECONDSTRAND));
	}

	@Override
	Stream<Arguments> testGetReadBase() {
		// ACGAACGT
		final List<Arguments> arguments = new ArrayList<Arguments>();
		arguments.add(createReadArgs(1, true, "2M", "AG", 0, Base.T));
		arguments.add(createReadArgs(1, false, "2M", "AG", 0, Base.A));
		
		arguments.add(createReadArgs(1, true, "2M", "AG", 1, Base.C));
		arguments.add(createReadArgs(1, false, "2M", "AG", 1, Base.G));

		return arguments.stream();
	}

	@Override
	Stream<Arguments> testGetRefBase() {
		// ACGAACGT
		final List<Arguments> arguments = new ArrayList<Arguments>();
		arguments.add(createRefArgs(1, true, "2M", "AG", 1, Base.T));
		arguments.add(createRefArgs(1, false, "2M", "AG", 1, Base.A));
		
		arguments.add(createRefArgs(1, true, "2M", "AG", 2, Base.G));
		arguments.add(createRefArgs(1, false, "2M", "AG", 2, Base.C));
		return arguments.stream();
	}

}
