package jacusa.method.rtarrest;

import java.util.Arrays;

import jacusa.cli.parameters.RTarrestParameter;
import lib.data.DataType;
import lib.data.IntegerData;
import lib.data.DataContainer.AbstractDataContainerBuilder;
import lib.data.DataContainer.AbstractDataContainerBuilderFactory;
import lib.data.count.BaseSub2BaseCallCount;
import lib.data.count.BaseSub2Integer;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Aggregate;
import lib.data.filter.FilteredBaseCallCount;
import lib.data.filter.FilteredBoolean;

/*
 * Factory
 */
public class RTarrestDataContainerBuilderFactory extends AbstractDataContainerBuilderFactory {

	private final RTarrestParameter parameter;

	public DataType<BaseCallCount> arrestBccDt;
	public DataType<BaseCallCount> throughBccDt;
	public DataType<BaseCallCount> bccDt;

	public DataType<IntegerData> readsDt;
	public DataType<IntegerData> coverageDt;

	public DataType<IntegerData> deletionsDt;
	public DataType<IntegerData> insertionsDt;

	public DataType<FilteredBaseCallCount> filteredBccDt;
	public DataType<FilteredBoolean> filteredBooleanDt;

	public DataType<BaseSub2BaseCallCount> bs2bccDt;

	public DataType<BaseSub2Integer> bs2deletionsDt;
	public DataType<BaseSub2Integer> bs2insertionsDt;
	public DataType<BaseSub2Integer> bs2readsDt;
	public DataType<BaseSub2Integer> bs2coverageDt;

	public RTarrestDataContainerBuilderFactory(final RTarrestParameter parameter) {
		super(parameter);

		this.parameter = parameter;
	}

	@Override
	protected void addRequired(final AbstractDataContainerBuilder builder) {
		arrestBccDt = add(builder, DataType.create("arrest", BaseCallCount.class));
		throughBccDt = add(builder, DataType.create("through", BaseCallCount.class));
		bccDt = add(builder, DataType.create("default", BaseCallCount.class,
				new Aggregate<BaseCallCount>(BaseCallCount.class, Arrays.asList(arrestBccDt, throughBccDt))));

		if (!parameter.getReadTags().isEmpty()) {
			bs2bccDt = add(builder, DataType.create("default", BaseSub2BaseCallCount.class));
			bs2readsDt = add(builder, DataType.create("reads", BaseSub2Integer.class));
			bs2coverageDt = add(builder, DataType.create("coverage", BaseSub2Integer.class));

			if (parameter.showDeletionCount()) {
				bs2deletionsDt = add(builder, DataType.create("deletions", BaseSub2Integer.class));
			}
			if (parameter.showInsertionCount()) {
				bs2insertionsDt = add(builder, DataType.create("insertions", BaseSub2Integer.class));
			}
		}
		if (parameter.showDeletionCount()) {
			deletionsDt = add(builder, DataType.create("deletions", IntegerData.class));
		}
		if (parameter.showInsertionCount()) {
			insertionsDt = add(builder, DataType.create("insertions", IntegerData.class));
		}
	}

	@Override
	protected void addFilters(final AbstractDataContainerBuilder builder) {
		filteredBccDt = add(builder, DataType.create("default", FilteredBaseCallCount.class));
		filteredBooleanDt = add(builder, DataType.create("default", FilteredBoolean.class));
	}

}