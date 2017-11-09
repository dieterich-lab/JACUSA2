package lib.data.builder;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameters;
import lib.data.AbstractData;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class AbstractDataBuilderFactory<T extends AbstractData>
implements hasLibraryType {

	final private LIBRARY_TYPE libraryType;
	
	public AbstractDataBuilderFactory(final LIBRARY_TYPE libraryType) {
		this.libraryType = libraryType;
	}
	
	/**
	 * 
	 * @param windowCoordinates
	 * @param reader
	 * @param conditionParameter
	 * @param parameters
	 * @return
	 */
	public abstract AbstractDataBuilder<T> newInstance(
			final AbstractConditionParameter<T> conditionParameter,
			final AbstractParameters<T> parameters);

	/**
	 * 
	 * @return
	 */
	final public boolean isStranded() {
		return libraryType != LIBRARY_TYPE.UNSTRANDED;
	}

	@Override
	final public LIBRARY_TYPE getLibraryType() {
		return libraryType;
	}
	
}
