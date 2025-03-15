package lib.cli.options.filter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import htsjdk.samtools.util.IOUtil;
import lib.cli.options.AbstractOption;
import lib.cli.options.filter.has.HasFileName;

public class FileNameOption extends AbstractOption {

	private final HasFileName hasFileName;
	
	public FileNameOption(final HasFileName hasFileName) {
		super(null, "file");
		this.hasFileName = hasFileName;
	}

	/**
	 * Tested in @see test.lib.cli.options.filter.FileNameOptionTest
	 */
	@Override
	public void process(CommandLine line) throws Exception {
		final String tmpFileName = line.getOptionValue(getLongOpt());
		IOUtil.assertInputIsValid(tmpFileName);
		hasFileName.setFileName(tmpFileName);
	}

	@Override
	public Option getOption(boolean printExtendedHelp) {
		return Option.builder()
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg()
				.required()
				.desc(
						"File that contains sites to be exclude from output. " +
						"Supported file types: see type")
				.build();
	}
	
}
