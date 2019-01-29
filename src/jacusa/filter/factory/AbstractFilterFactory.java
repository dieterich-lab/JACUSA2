package jacusa.filter.factory;

import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;

import jacusa.filter.Filter;
import lib.data.assembler.ConditionContainer;
import lib.util.Util;
import lib.util.coordinate.CoordinateController;

/**
 * This factory creates an artefact filter object and registers it.  
 * 
 * @param <T>
 */
public abstract class AbstractFilterFactory implements FilterFactory {

	private final Option option;

	public AbstractFilterFactory(final Option option) {
		this.option = option;
	}

	@Override
	public char getC() {
		return option.getOpt().charAt(0);
	}

	@Override
	public String getDesc() {
		// HACK
		Option tmp = (Option)option.clone();
		Util.adjustOption(tmp, getOptions(), tmp.getOpt().length());
		return tmp.getDescription();
	}

	protected abstract Set<Option> processCLI(CommandLine cmd) throws MissingOptionException;
	
}
