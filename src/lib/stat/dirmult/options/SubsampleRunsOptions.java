package lib.stat.dirmult.options;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import lib.cli.options.AbstractProcessingOption;
import lib.cli.parameter.GeneralParameter;
import lib.stat.dirmult.DirMultParameter;

public class SubsampleRunsOptions extends AbstractProcessingOption {

	private final GeneralParameter parameter;
	private final DirMultParameter dirMultParameter;
	
	public SubsampleRunsOptions(
			final GeneralParameter parameter,
			final DirMultParameter dirMultParameter) {
		super("subsampleRuns", "subsampleRuns");
		
		this.parameter 			= parameter;
		this.dirMultParameter 	= dirMultParameter;
	}

	@Override
	public void process(CommandLine cmd) throws Exception {
		final int subsampleRuns = Integer.parseInt(cmd.getOptionValue(getLongOpt()));
		if (subsampleRuns < 0) {
			throw new IllegalArgumentException(getLongOpt() + " must be > 0");
		}
		dirMultParameter.setSubampleRuns(subsampleRuns);
		final List<String> keys = new ArrayList<String>();
		keys.add("score_subsampled");
		if (parameter.showInsertionCount() || parameter.showInsertionStartCount()) {
			keys.add("insertion_score_subsampled");
		}
		for (final String key : keys) {
			parameter.registerKey(key);
		}
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
