package lib.cli.parameters;

import lib.data.AbstractData;
import lib.data.builder.factory.AbstractDataBuilderFactory;
import lib.data.has.hasLibraryType;

public class JACUSAConditionParameters<T extends AbstractData>
extends AbstractConditionParameter<T>
implements hasLibraryType {
	
	private AbstractDataBuilderFactory<T> dataBuilderFactory;
	
	private JACUSAConditionParameters() {
		super();
	}

	// TODO
	public JACUSAConditionParameters(
			final AbstractDataBuilderFactory<T> dataBuilderFactory) {
		this();
		this.dataBuilderFactory = dataBuilderFactory;
	}
	
	

	/**
	 * @return the pileupBuilderFactory
	 */
	public AbstractDataBuilderFactory<T> getDataBuilderFactory() {
		return dataBuilderFactory;
	}

	/**
	 * @param pileupBuilderFactory the pileupBuilderFactory to set
	 */
	public void setPileupBuilderFactory(AbstractDataBuilderFactory<T> pileupBuilderFactory) {
		this.dataBuilderFactory = pileupBuilderFactory;
	}

	/* TODO
	public BaseCallConfig getBaseConfig() {
		return baseConfig;
	}

	public void setBaseConfig(BaseCallConfig baseConfig) {
		this.baseConfig = baseConfig;
	}
	*/

	public LIBRARY_TYPE getLibraryType() {
		return getDataBuilderFactory().getLibraryType();
	}

}
