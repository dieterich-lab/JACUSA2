package lib.cli.options.filter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import jacusa.io.FileType;
import lib.cli.options.AbstractACOption;
import lib.cli.options.filter.has.HasFileType;

public class FileTypeOption extends AbstractACOption {

	private final HasFileType hasFileType;
	
	public FileTypeOption(final HasFileType hasFileType) {
		super(null, "type");
		this.hasFileType = hasFileType;
	}

	/**
	 * Tested in @see test.lib.cli.options.filter.FileTypeOptionTest
	 */
	@Override
	public void process(CommandLine line) throws Exception {
		final String tmpFileTypeStr = line.getOptionValue(getLongOpt());
		final FileType tmpFileType 	= FileType.valueOf(tmpFileTypeStr);
		if (tmpFileType == null) {
			throw new IllegalArgumentException(
					"Option " + getLongOpt() + " = " + tmpFileTypeStr + " could not be parsed");
		}
		hasFileType.setFileType(tmpFileType);
	}

	@Override
	public Option getOption(boolean printExtendedHelp) {
		final StringBuilder sb = new StringBuilder();
		
		for (final FileType fileType : FileType.values()) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(fileType.getName());
		}

		return Option.builder()
			.longOpt(getLongOpt())
			.argName(getLongOpt().toUpperCase())
			.required()
			.hasArg()
			.desc("File type: " + sb.toString())
			.build();

	}

}
