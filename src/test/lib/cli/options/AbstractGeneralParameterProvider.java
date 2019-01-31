package test.lib.cli.options;

import org.junit.jupiter.api.BeforeEach;

import lib.cli.parameter.GeneralParameter;

abstract class AbstractGeneralParameterProvider {

	private final int conditionSize;
	
	private GeneralParameter parameter;
	
	public AbstractGeneralParameterProvider(final int conditionSize) {
		this.conditionSize = conditionSize;
	}
	
	public AbstractGeneralParameterProvider() {
		this(2);
	}
	
	@BeforeEach
	void beforeEach() {
		parameter = new GeneralParameter(conditionSize);
	}

	GeneralParameter getGeneralParamter() {
		return parameter;
	}
	
}
