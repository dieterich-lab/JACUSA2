package lib.cli.options;

import lib.util.AbstractTool;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class ShowVersionOption extends AbstractACOption {

	private boolean printed;
	
	public ShowVersionOption() {
		super("v", "version");
		printed = false;
	}

	@Override
	public Option getOption() {
		return Option.builder(getOpt())
		        .desc("show version")
		        .build();
	}

	public boolean isPrinted() {
		return printed;
	}
	
	@Override
	public void process(final CommandLine line) throws Exception {
		if(line.hasOption(getOpt())) {
			final AbstractTool tool = AbstractTool.getLogger().getTool();
			final StringBuilder sb = new StringBuilder();
			sb.append(tool.getName());
			sb.append(' ');
			sb.append(tool.getVersion());
			System.out.println(sb.toString());
			printed = true;
	    }
	}
	
}