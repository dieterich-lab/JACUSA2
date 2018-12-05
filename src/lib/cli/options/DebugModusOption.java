package lib.cli.options;

import lib.cli.parameter.GeneralParameter;
import lib.method.AbstractMethod;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class DebugModusOption extends AbstractACOption {

	private final GeneralParameter parameter;
	private final AbstractMethod method;
	
	public DebugModusOption(final GeneralParameter parameter,
			final AbstractMethod method) {
		super("x", "debug");
		hide();
		this.parameter = parameter;
		this.method = method;
	}

	@Override
	public Option getOption(final boolean printExtendedHelp) {
		return Option.builder(getOpt())
		        .desc("turn on Debug modus")
		        .build();
	}

	@Override
	public void process(final CommandLine line) throws Exception {
		parameter.setDebug(true);
		method.debug();
	}
	
}