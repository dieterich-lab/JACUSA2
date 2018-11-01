package test.jacusa.filter.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactory;

@DisplayName("Test CLI parser of MaxAlleleCountFilterFactoryFactory")
public class MaxAlleleCountFilterFactoryTest extends AbstractMaxAlleleCountFilterFactoryTest {

	private MaxAlleleCountFilterFactory testInstance;
	
	@BeforeEach
	public void setUp() {
		testInstance = new MaxAlleleCountFilterFactory(null);
	}

	@Override
	protected int getMaxAlleles() {
		return testInstance.getMaxAlleles();
	}
	
	@Override
	protected AbstractFilterFactory getTestInstance() {
		return testInstance;
	}

}
