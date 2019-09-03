package lib.cli.options.filter;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
import lib.cli.options.AbstractACOption;
import lib.cli.options.filter.has.HasApply2reads;
import lib.io.InputOutput;

/**
 * Class implements CLI option that enables to chose read type: 
 * arrest, through, or all.
 * This is useful in the context of artefact filtering for rt-arrest and lrt-arrest
 * methods. 
 */
public class Apply2readsOption extends AbstractACOption {

	private final HasApply2reads hasApply2reads;
	
	public Apply2readsOption(final HasApply2reads hasApply2reads) {
		super(null, "reads");
		this.hasApply2reads = hasApply2reads;
	}

	/**
	 * Tested in @set test.lib.cli.options.filter.Apply2readsOptionTest
	 */
	@Override
	public void process(CommandLine line) throws Exception {
		final String optionValue = line.getOptionValue(getLongOpt());
		final Set<RT_READS> apply2reads = new HashSet<>(2);
		
		final String[] options = optionValue.toUpperCase().split(Character.toString(InputOutput.AND));
		for (final String option : options) {
			final RT_READS tmpOption = RT_READS.valueOf(option.toUpperCase());
			if (tmpOption == null) {
				throw new IllegalArgumentException("Invalid argument: " + line);						
			}
			apply2reads.add(tmpOption);
		}
		hasApply2reads.getApply2Reads().clear();
		hasApply2reads.getApply2Reads().addAll(apply2reads);
	}

	@Override
	public Option getOption(boolean printExtendedHelp) {
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (final RT_READS r : hasApply2reads.getApply2Reads()) {
			if (! first) {
				sb.append(InputOutput.AND);
				first = false;
			}
			sb.append(r.toString());
		}

		return Option.builder()
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg()
				.desc(
						"Apply filter to base calls from reads: " +
						"ARREST or THROUGH or ARREST&THROUGH. Default: " + sb.toString())
				.build();
	}
	
}
