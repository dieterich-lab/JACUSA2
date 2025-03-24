package lib.stat.dirmult.options;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import lib.cli.options.AbstractProcessingOption;
import lib.stat.dirmult.DirMultParameter;

public class SubsampleRunsOptions extends AbstractProcessingOption {

	private final DirMultParameter dirMultParameter;
	
	public SubsampleRunsOptions(final DirMultParameter dirMultParameter) {
		super("subsampleRuns", "subsampleRuns");
		
		this.dirMultParameter = dirMultParameter;
	}

	@Override
	public void process(CommandLine cmd) throws Exception {
		final int subsampleRuns = Integer.parseInt(cmd.getOptionValue(getLongOpt()));
		if (subsampleRuns < 0) {
			throw new IllegalArgumentException(getLongOpt() + " must be > 0");
		}
		dirMultParameter.setSubampleRuns(subsampleRuns);
	}

	@Override
	public Option getOption(boolean printExtendedHelp) {
		return Option.builder()
				.longOpt("subsampleRuns")
				.hasArg(true)
				.desc("Number of subsampling runs. Default: " + dirMultParameter.getSubsampleRuns())
				.build();
	}

}
