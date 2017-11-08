package jacusa.cli.parameters;

import lib.data.AbstractData;

/**
 * 
 * @author Michael Piechotta
 *
 */
public interface hasStatisticCalculator<T extends AbstractData> {

	StatisticParameters<T> getStatisticParameters();

}
