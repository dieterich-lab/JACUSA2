package lib.cli.options;

import lib.cli.parameter.GeneralParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class ShowAllSitesOption extends AbstractACOption {

	private final GeneralParameter parameter;
	
	public ShowAllSitesOption(final GeneralParameter parameter) {
		super("A", "show-all");
		this.parameter = parameter;
	}
	
	@Override
	public Option getOption(final boolean printExtendedHelp) {
		return Option.builder(getOpt())
				.hasArg(false)
		        .desc("Show all sites - including sites without variants")
		        .build();
	}
	

	@Override
	public void process(final CommandLine line) throws Exception {
		parameter.showAllSites(true);
	}

}