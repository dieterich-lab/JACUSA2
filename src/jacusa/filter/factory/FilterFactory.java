package jacusa.filter.factory;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import jacusa.filter.Filter;
import lib.cli.parameter.ConditionParameter;
import lib.data.DataTypeContainer;
import lib.data.DataTypeContainer.AbstractBuilder;
import lib.data.assembler.ConditionContainer;
import lib.data.cache.container.SharedCache;
import lib.data.cache.record.RecordWrapperProcessor;
import lib.util.coordinate.CoordinateController;

public interface FilterFactory {

	/**
	 * Gets unique char id of this filter.
	 * 
	 * @return unique char id 
	 */
	char getC();

	/**
	 * Gets the description for this filter.
	 * 
	 * @return string that describes this filter
	 */
	String getDesc();

	/**
	 * TODO add comments.
	 * 
	 * @param line
	 * @throws ParseException 
	 */
	void processCLI(String line) throws ParseException;

	Options getOptions();

	void addFilteredData(StringBuilder sb, DataTypeContainer filteredData);

	/**
	 * TODO add comments.
	 * 
	 * @param conditionParameter
	 * @param baseCallConfig
	 * @param coordinateController
	 * @return
	 */
	RecordWrapperProcessor createFilterCache(ConditionParameter conditionParameter, SharedCache sharedCache);

	void initDataTypeContainer(AbstractBuilder builder);

	abstract Filter createFilter(
			CoordinateController coordinateController, 
			ConditionContainer conditionContainer);

	
	/**
	 * TODO add comments.
	 * 
	 * @param coordinateController
	 * @param conditionContainer
	 */
	default void registerFilter(
			final CoordinateController coordinateController, 
			final ConditionContainer conditionContainer) {

		conditionContainer.getFilterContainer().addFilter(
				createFilter(coordinateController, conditionContainer));
	}

}