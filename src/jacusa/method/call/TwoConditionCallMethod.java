package jacusa.method.call;

import org.apache.commons.cli.ParseException;

import jacusa.cli.parameters.CallParameter;
import lib.data.assembler.factory.CallDataAssemblerFactory;

public class TwoConditionCallMethod 
extends CallMethod {

	protected TwoConditionCallMethod(
			final String name, 
			final CallParameter parameter, 
			final CallDataAssemblerFactory dataAssemblerFactory) {
		
		super(name, parameter, dataAssemblerFactory);
	}

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length != 2) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args);
	}

}
