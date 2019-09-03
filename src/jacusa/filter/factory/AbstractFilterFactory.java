package jacusa.filter.factory;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Option;

import lib.cli.options.AbstractACOption;
import lib.util.CLIUtil;

/**
 * This factory creates an artefact/false positive variant filter object and 
 * registers it.  
 */
public abstract class AbstractFilterFactory implements FilterFactory {

	private final Option option;
	// holds any potential options for this filter factory
	private final List<AbstractACOption> acOptions;
	
	public AbstractFilterFactory(final Option option) {
		this.option = option;
		acOptions 	= new ArrayList<>();
	}

	@Override
	public char getID() {
		return option.getOpt().charAt(0);
	}

	@Override
	public List<AbstractACOption> getACOption() {
		return acOptions;
	}
	
	@Override
	public String getDesc() {
		// HACK to display description of filters
		// create copy of option
		final Option tmp = (Option)option.clone();
		// and modify it ONLY for display
		CLIUtil.adjustOption(tmp, getOptions(), tmp.getOpt().length());
		return tmp.getDescription();
	}
	
	@Override
	public String toString() {
		return Character.toString(getID());
	}
	
}
