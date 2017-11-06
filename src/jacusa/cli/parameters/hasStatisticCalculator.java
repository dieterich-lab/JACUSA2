package jacusa.cli.parameters;

import jacusa.data.AbstractData;

/**
 * 
 * @author Michael Piechotta
 *
 */
public interface hasStatisticCalculator<T extends AbstractData> {

	StatisticParameters<T> getStatisticParameters();

}
