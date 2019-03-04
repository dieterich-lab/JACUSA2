package test.lib.data.storage.arrest;

import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import lib.data.storage.arrest.LocationInterpreter;
import lib.data.storage.arrest.RF_FIRSTSTRAND_LocationInterpreter;

class RF_FIRSTSTRAND_LocationInterpreterTest implements LocationInterpreterTest {

	@Override
	public Stream<Arguments> testGetThroughPositionProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<Arguments> testGetArrestPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocationInterpreter createTestInstance() {
		return new RF_FIRSTSTRAND_LocationInterpreter();
	}

}
