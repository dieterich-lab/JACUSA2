package jacusa.method.lrtarrest;

import jacusa.cli.parameters.LRTarrestParameter;
import lib.data.DataType;
import lib.data.IntegerData;
import lib.data.DataContainer.AbstractDataContainerBuilder;
import lib.data.DataContainer.AbstractDataContainerBuilderFactory;
import lib.data.count.BaseSub2BaseCallCount;
import lib.data.count.BaseSub2Integer;
import lib.data.count.PileupCount;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Pileup2BaseCallCount;
import lib.data.filter.FilteredBaseCallCount;
import lib.data.filter.FilteredBoolean;
import lib.data.storage.lrtarrest.ArrestPosition2BaseCallCount;

/*
 * Factory
 */
public class LRTarrestDataContainerBuilderFactory extends AbstractDataContainerBuilderFactory {

	private final LRTarrestParameter parameter;

	public DataType<PileupCount> pileupCountDt;
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

	public DataType<ArrestPosition2BaseCallCount> ap2bccDt;

	public LRTarrestDataContainerBuilderFactory(final LRTarrestParameter parameter) {
		super(parameter);

		this.parameter = parameter;
	}

	@Override
	protected void addRequired(final AbstractDataContainerBuilder builder) {
		pileupCountDt = add(builder, DataType.create("default", PileupCount.class));
		ap2bccDt = add(builder, DataType.create("default", ArrestPosition2BaseCallCount.class));
		bccDt = add(builder, DataType.create("default", BaseCallCount.class, new Pileup2BaseCallCount(pileupCountDt)));

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