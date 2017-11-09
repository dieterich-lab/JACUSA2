package lib.data.builder.factory;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.builder.AbstractDataBuilder;
import lib.data.has.hasLibraryType;

public abstract class AbstractDataBuilderFactory<T extends AbstractData>
implements hasLibraryType {

	private final LIBRARY_TYPE libraryType;
	
	private AbstractParameter<T> generalParameter;

	public AbstractDataBuilderFactory(final LIBRARY_TYPE libraryType,
			final AbstractParameter<T> generalParameter) {
		this.libraryType = libraryType;
		this.generalParameter = generalParameter;
	}
	
	public AbstractDataBuilderFactory(final LIBRARY_TYPE libraryType) {
		this.libraryType = libraryType;
	}
	
	public abstract AbstractDataBuilder<T> newInstance(final AbstractConditionParameter<T> conditionParameter);

	final public boolean isStranded() {
		return libraryType != LIBRARY_TYPE.UNSTRANDED;
	}

	@Override
	final public LIBRARY_TYPE getLibraryType() {
		return libraryType;
	}
	
	public AbstractParameter<T> getGeneralParameter() {
		return generalParameter;
	}
	
	public void setGeneralParameter(final AbstractParameter<T> generalParameter)  {
		this.generalParameter = generalParameter;
	}
	
}
