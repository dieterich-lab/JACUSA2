package jacusa.filter.factory;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Option;

import lib.cli.options.AbstractACOption;
import lib.util.CLIUtil;

/**
 * This factory creates an artefact filter object and registers it.  
 * 
 * @param <T>
 */
public abstract class AbstractFilterFactory implements FilterFactory {

	private final Option option;
	private final List<AbstractACOption> acOptions;
	
	public AbstractFilterFactory(final Option option) {
		this.option = option;
		acOptions 	= new ArrayList<>();
	}

	@Override
	public char getC() {
		return option.getOpt().charAt(0);
	}

	@Override
	public List<AbstractACOption> getACOption() {
		return acOptions;
	}
	
	@Override
	public String getDesc() {
		// HACK
		Option tmp = (Option)option.clone();
		CLIUtil.adjustOption(tmp, getOptions(), tmp.getOpt().length());
		return tmp.getDescription();
	}

	@Override
	public String toString() {
		return Character.toString(getC());
	}
	
}
