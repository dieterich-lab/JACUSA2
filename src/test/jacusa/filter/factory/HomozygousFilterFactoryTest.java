package test.jacusa.filter.factory;

import org.junit.jupiter.api.DisplayName;

import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.HomozygousFilterFactory;

@DisplayName("Test CLI parser of HomozygousFilterFactory")
public class HomozygousFilterFactoryTest extends AbstractHomozygousFilterFactoryTest {
	
	private HomozygousFilterFactory testInstance;
	
	protected void createTestInstance(final int conditionSize) {
		testInstance = new HomozygousFilterFactory(conditionSize, null);
	}
	
	protected AbstractFilterFactory getTestInstance() {
		return testInstance;
	}
	
	protected int getHomozygousConditionIndex() {
		return testInstance.getHomozygousConditionIndex();
	}
	
}
