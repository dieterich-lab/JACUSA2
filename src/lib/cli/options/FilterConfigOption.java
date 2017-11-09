package lib.cli.options;

import jacusa.filter.factory.AbstractFilterFactory;

import java.util.Map;

import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class FilterConfigOption<T extends AbstractData> extends AbstractACOption {

	final private AbstractParameter<T> parameters;

	private static final char OR = ',';
	//private static char AND = '&'; // Future Feature add logic

	final private Map<Character, AbstractFilterFactory<T>> filterFactories;

	public FilterConfigOption(final AbstractParameter<T> parameters, 
			final Map<Character, AbstractFilterFactory<T>> filterFactories) {
		super("a", "pileup-filter");
		this.parameters = parameters;

		this.filterFactories = filterFactories;
	}

	@Override
	public Option getOption() {
		StringBuffer sb = new StringBuffer();

		for (final char c : filterFactories.keySet()) {
			final AbstractFilterFactory<T> pileupFilterFactory = filterFactories.get(c);
			sb.append(pileupFilterFactory.getC());
			sb.append(" | ");
			sb.append(pileupFilterFactory.getDesc());
			sb.append("\n");
		}

		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc(
					"chain of " + getLongOpt().toUpperCase() + " to apply to pileups:\n" + sb.toString() + 
					"\nSeparate multiple " + getLongOpt().toUpperCase() + " with '" + OR + "' (e.g.: D,I)")
				.build(); 
	}

	@Override
	public void process(final CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
			final String s = line.getOptionValue(getOpt());
			final String[] t = s.split(Character.toString(OR));

			for (final String a : t) {
				char c = a.charAt(0);
				if (! filterFactories.containsKey(c)) {
					throw new IllegalArgumentException("Unknown SAM processing: " + c);
				}
				AbstractFilterFactory<T> filterFactory = filterFactories.get(c);
				if (a.length() > 1) {
					filterFactory.processCLI(a);
				}
				parameters.getFilterConfig().addFactory(filterFactory);
			}
		}
	}

}