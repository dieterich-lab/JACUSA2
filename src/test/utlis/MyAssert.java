package test.utlis;

import static org.junit.Assert.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class MyAssert {

	/*
	public static void assertEquals(final BaseCallCount expectedBaseCallCount, final BaseCallCount actualBaseCallCount, final StringBuilder sb) {
		sb.append(expectedBaseCallCount == null ? "empty" : expectedBaseCallCount.toString());
		sb.append(" != ");
		sb.append(actualBaseCallCount == null ? "empty" : actualBaseCallCount.toString());
		
		final boolean result = expectedBaseCallCount.equals(actualBaseCallCount);
		assertTrue(result, sb.toString());
	}
	*/

	public static <S> void assertCopy(final S o1, final S o2, final String s) {
		assertNotSame(s + " are the same", o1, o2);
		assertEquals(o1, o2, s + " not equal");
	}
	
}
