package test.lib.data.cache.readsubstitution;

import lib.util.Base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import htsjdk.samtools.SAMRecord;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.readsubstitution.BaseCallInterpreter;

abstract class AbstractBaseCallInterpreterTest {

	private BaseCallInterpreter testInstance;

	public AbstractBaseCallInterpreterTest(final BaseCallInterpreter testInstance) {
		this.testInstance = testInstance;
	}
	
	abstract Stream<Arguments> testGetReadBase();
	abstract Stream<Arguments> testGetRefBase();
	
	@ParameterizedTest(name = "For Read {0}, and readPosition {1} the expected base is {3}")
	@MethodSource("testGetReadBase")
	void testGetReadBase(
			SAMRecord record, int readPosition, 
			Base expected) {
	
		final SAMRecordWrapper recordWrapper = new SAMRecordWrapper(record);
		final Base actual = testInstance.getReadBase(recordWrapper, readPosition);
		assertEquals(expected, actual);
	}

	@ParameterizedTest(name = "For Read {0}, and readPosition {1} the expected base is {3}")
	@MethodSource("testGetRefBase")
	void testGetRefBase(
			SAMRecord record, int referencePosition, 
			Base expected) {
		
		final SAMRecordWrapper recordWrapper = new SAMRecordWrapper(record);
		final Base actual = testInstance.getRefBase(recordWrapper, referencePosition);
		assertEquals(expected, actual);
	}
	
}
