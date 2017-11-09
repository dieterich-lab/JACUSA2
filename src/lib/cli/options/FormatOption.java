package lib.cli.options;

import jacusa.io.format.AbstractOutputFormat;

import java.util.Map;

import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class FormatOption<T extends AbstractData> 
extends AbstractACOption {

	private AbstractParameter<T> parameters;
	private Map<Character, AbstractOutputFormat<T>> formats;

	public FormatOption(final AbstractParameter<T> parameters, final Map<Character, AbstractOutputFormat<T>> formats) {
		super("f", "output-format");
		this.parameters = parameters;
		this.formats = formats;
	}

	@Override
	public Option getOption() {
		StringBuffer sb = new StringBuffer();

		for (char c : formats.keySet()) {
			AbstractOutputFormat<T> format = formats.get(c);
			if (format.getC() == parameters.getFormat().getC()) {
				sb.append("<*>");
			} else {
				sb.append("< >");
			}
			sb.append(" " + c);
			sb.append(": ");
			sb.append(format.getDesc());
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
				if (! formats.containsKey(c)) {
					throw new IllegalArgumentException("Unknown output format: " + c);
				}
				parameters.setFormat(formats.get(c));
			}
		}
	}

}