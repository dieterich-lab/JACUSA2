package lib.cli.parameter;

import jacusa.cli.parameters.CallParameter;

public class CallRegisterInfoKeys implements RegisterInfoKeys {

	private final CallParameter parameter;
	
	public CallRegisterInfoKeys(final CallParameter parameter) {
		this.parameter = parameter;
	}
	
	@Override
	public void registerKeys() {
		parameter.registerKey("NumericallyStable");
	}
	
}
