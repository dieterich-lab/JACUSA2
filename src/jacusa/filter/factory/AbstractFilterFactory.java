package jacusa.filter.factory;

import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import jacusa.filter.AbstractFilter;
import lib.cli.parameter.AbstractConditionParameter;
import lib.data.DataTypeContainer;
import lib.data.DataTypeContainer.AbstractBuilder;
import lib.data.assembler.ConditionContainer;
import lib.data.cache.container.SharedCache;
import lib.data.cache.record.RecordWrapperProcessor;
import lib.util.Util;
import lib.util.coordinate.CoordinateController;

/**
 * This factory creates an artefact filter object and registers it.  
 * 
 * @param <T>
 */
public abstract class AbstractFilterFactory {

	private final Option option;

	public AbstractFilterFactory(final Option option) {
		this.option = option;
	}

	/**
	 * Gets unique char id of this filter.
	 * 
	 * @return unique char id 
	 */
	public char getC() {
		return option.getOpt().charAt(0);
	}

	/**
	 * Gets the description for this filter.
	 * 
	 * @return string that describes this filter
	 */
	public String getDesc() {
		// HACK
		Option tmp = (Option)option.clone();
		Util.adjustOption(tmp, getOptions(), tmp.getOpt().length());
		return tmp.getDescription();
	}

	/**
	 * TODO add comments.
	 * 
	 * @param line
	 */
	public void processCLI(String line) throws MissingOptionException {
		processCLI(Util.processCLI(line, getOptions()));
	}

	protected abstract Set<Option> processCLI(CommandLine cmd) throws MissingOptionException;
	public abstract Options getOptions();

	public abstract void addFilteredData(StringBuilder sb, DataTypeContainer filteredData);
	
	protected abstract AbstractFilter createFilter(
			CoordinateController coordinateController, 
			ConditionContainer conditionContainer);

	/**
	 * TODO add comments.
	 * 
	 * @param conditionParameter
	 * @param baseCallConfig
	 * @param coordinateController
	 * @return
	 */
	public abstract RecordWrapperProcessor createFilterCache(
			final AbstractConditionParameter conditionParameter, final SharedCache sharedCache);
	
	public abstract void inidDataTypeContainer(final AbstractBuilder builder);
	
	/**
	 * TODO add comments.
	 * 
	 * @param coordinateController
	 * @param conditionContainer
	 */
	public final void registerFilter(
			final CoordinateController coordinateController, 
			final ConditionContainer conditionContainer) {

		conditionContainer.getFilterContainer().addFilter(
				createFilter(coordinateController, conditionContainer));
	}
	
}
