package lib.cli.options;

import java.util.Map;

import lib.cli.parameter.GeneralParameter;
import lib.io.ResultFormat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class ResultFormatOption 
extends AbstractACOption {

	private final GeneralParameter parameter;
	private final Map<Character, ResultFormat> resultFormats;

	public ResultFormatOption(final GeneralParameter parameter, 
			final Map<Character, ResultFormat> resultFormats) {

		super("f", "output-format");
		this.parameter 		= parameter;
		this.resultFormats 	= resultFormats;
	}

	@Override
	public Option getOption(final boolean printExtendedHelp) {
		StringBuilder sb = new StringBuilder();

		boolean required = true;
		for (char rf_id : resultFormats.keySet()) {
			ResultFormat resultFormat = resultFormats.get(rf_id);
			if (parameter.getResultFormat() != null && 
					resultFormat.getID() == parameter.getResultFormat().getID()) {
				sb.append("<*>");
				required = false;
			} else {
				sb.append("< >");
			}
			sb.append(" " + rf_id);
			sb.append(": ");
			sb.append(resultFormat.getDesc());
			sb.append("\n");
		}
		
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.required(required)
				.desc("Choose output format:\n" + sb.toString())
				.build(); 
	}

	@Override
	public void process(final CommandLine line) throws IllegalArgumentException {
		final String s = line.getOptionValue(getOpt());
		for (int i = 0; i < s.length(); ++i) {
			final char c = s.charAt(i);
			if (! resultFormats.containsKey(c)) {
				throw new IllegalArgumentException("Unknown output format: " + c);
			}
			parameter.setResultFormat(resultFormats.get(c));
		}
	}

}