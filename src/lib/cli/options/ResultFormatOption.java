package lib.cli.options;


import java.util.Map;

import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.result.Result;
import lib.io.AbstractResultFormat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class ResultFormatOption<T extends AbstractData, R extends Result<T>> 
extends AbstractACOption {

	private AbstractParameter<T, R> parameters;
	private Map<Character, AbstractResultFormat<T, R>> resultFormats;

	public ResultFormatOption(final AbstractParameter<T, R> parameter, final Map<Character, AbstractResultFormat<T, R>> resultFormats) {
		super("f", "output-format");
		this.parameters = parameter;
		this.resultFormats = resultFormats;
	}

	@Override
	public Option getOption() {
		StringBuffer sb = new StringBuffer();

		for (char c : resultFormats.keySet()) {
			AbstractResultFormat<T, ?> resultFormat = resultFormats.get(c);
			if (resultFormat.getC() == parameters.getResultFormat().getC()) {
				sb.append("<*>");
			} else {
				sb.append("< >");
			}
			sb.append(" " + c);
			sb.append(": ");
			sb.append(resultFormat.getDesc());
			sb.append("\n");
		}
		
		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc("Choose output format:\n" + sb.toString())
				.build(); 
	}

	@Override
	public void process(final CommandLine line) throws IllegalArgumentException {
		if (line.hasOption(getOpt())) {
			final String s = line.getOptionValue(getOpt());
			for (int i = 0; i < s.length(); ++i) {
				final char c = s.charAt(i);
				if (! resultFormats.containsKey(c)) {
					throw new IllegalArgumentException("Unknown output format: " + c);
				}
				parameters.setResultFormat(resultFormats.get(c));
			}
		}
	}

}