package lib.cli.options;

import lib.cli.parameter.AbstractParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class FilterModusOption extends AbstractACOption {

	final private AbstractParameter<?, ?> parameter;
	
	public FilterModusOption(final AbstractParameter<?, ?> parameter) {
		super("s", "separate");
		this.parameter = parameter;
	}
	
	@Override
	public Option getOption() {
		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.hasArg(false)
		        .desc("Put feature-filtered results in to a separate file (= RESULT-FILE" + 
		        		AbstractParameter.FILE_SUFFIX + ")")
		        .build();
	}
	
	@Override
	public void process(final CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
			parameter.setSeparate(true);
	    }
	}

}