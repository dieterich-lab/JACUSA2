package jacusa.cli.options;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.AbstractData;
import jacusa.io.format.AbstractOutputFormat;

import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class FormatOption<T extends AbstractData> 
extends AbstractACOption {

	private AbstractParameters<T> parameters;
	private Map<Character, AbstractOutputFormat<T>> formats;

	public FormatOption(final AbstractParameters<T> parameters, final Map<Character, AbstractOutputFormat<T>> formats) {
		super("f", "output-format");
		this.parameters = parameters;
		this.formats = formats;
	}

	@SuppressWarnings("static-access")
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
		
		return OptionBuilder.withLongOpt(getLongOpt())
			.withArgName(getLongOpt().toUpperCase())
			.hasArg(true)
			.withDescription("Choose output format:\n" + sb.toString())
			.create(getOpt()); 
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
			String s = line.getOptionValue(getOpt());
			for (int i = 0; i < s.length(); ++i) {
				char c = s.charAt(i);
				if ( !formats.containsKey(c)) {
					throw new IllegalArgumentException("Unknown output format: " + c);
				}
				parameters.setFormat(formats.get(c));
			}
		}
	}

}