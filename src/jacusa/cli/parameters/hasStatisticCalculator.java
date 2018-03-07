package jacusa.cli.parameters;

import lib.data.AbstractData;

/**
 * 
 * @author Michael Piechotta
 *
 */
public interface hasStatisticCalculator<T extends AbstractData> {

	StatisticParameter<T> getStatisticParameters();

}
