package lib.data.result;

import java.util.SortedSet;

import lib.cli.options.filter.has.BaseSub;
import lib.data.DataContainer;
import lib.data.IntegerData;
import lib.estimate.MinkaParameter;
import lib.io.InputOutput;
import lib.stat.estimation.provider.InsertionEstCountProvider;

/**
 * TODO add documentation
 */
@Deprecated
public class InsertionCountResult extends AbstractINDELCountResult {

	public static final String SCORE 	= "insertion_score";
	public static final String PVALUE = "insertion_pvalue";
	
	private static final long serialVersionUID = 1L;
	
	public InsertionCountResult(
			final SortedSet<BaseSub> baseSubs, final Result result,
			final MinkaParameter minkaParameter,
			final InsertionEstCountProvider countSampleProvider) {
		
		super(baseSubs, result, minkaParameter,countSampleProvider);
	}
	
	@Override
	void addPValue(Result result, int valueIndex, String value) {
		result.getResultInfo(valueIndex).add(PVALUE, value);
	}
	
	@Override
	void addScore(Result result, int valueIndex, String value) {
		result.getResultInfo(valueIndex).add(SCORE, value);
	}
	
	// TODO why method in result and estCount
	@Override
	IntegerData getCount(DataContainer container) {
		return new IntegerData(container.getPileupCount().getINDELCount().getInsertionCount());
	}
	
	@Override
	String getField() {
		return InputOutput.INSERTION_FIELD;
	}
	
	@Override
	IntegerData getStratifiedCount(DataContainer container, BaseSub baseSub) {
		return container.getBaseSub2InsertionCount().get(baseSub);
	}
	
}
