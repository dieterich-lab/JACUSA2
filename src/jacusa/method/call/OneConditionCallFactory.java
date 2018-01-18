package jacusa.method.call;

import jacusa.cli.parameters.CallParameter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.CombinedDistanceFilterFactory;
import jacusa.filter.factory.INDEL_DistanceFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactory;
import jacusa.filter.factory.ReadPositionDistanceFilterFactory;
import jacusa.filter.factory.SpliceSiteDistanceFilterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.data.AbstractData;
import lib.data.BaseCallData;
import lib.data.generator.BaseCallDataGenerator;
import lib.data.generator.DataGenerator;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasPileupCount;
import lib.data.has.hasReferenceBase;

import org.apache.commons.cli.ParseException;

public class OneConditionCallFactory<T extends AbstractData & hasPileupCount & hasBaseCallCount & hasReferenceBase> 
extends CallFactory<T> {

	public OneConditionCallFactory(final DataGenerator<T> dataGenerator) {
		super(new CallParameter<T>(1), dataGenerator);
	}

	@Override
	public Map<Character, AbstractFilterFactory<T>> getFilterFactories() {
		Map<Character, AbstractFilterFactory<T>> c2filterFactory = 
				new HashMap<Character, AbstractFilterFactory<T>>();

		final List<AbstractFilterFactory<T>> filterFactories = 
				new ArrayList<AbstractFilterFactory<T>>(5);
		
		final DataGenerator<BaseCallData> dataGenerator = new BaseCallDataGenerator();
		filterFactories.add(new CombinedDistanceFilterFactory<T, BaseCallData>(dataGenerator));
		filterFactories.add(new INDEL_DistanceFilterFactory<T, BaseCallData>(dataGenerator));
		filterFactories.add(new ReadPositionDistanceFilterFactory<T, BaseCallData>(dataGenerator));
		filterFactories.add(new SpliceSiteDistanceFilterFactory<T, BaseCallData>(dataGenerator));
		filterFactories.add(new MaxAlleleCountFilterFactory<T>());

		for (final AbstractFilterFactory<T> filterFactory : filterFactories) {
			c2filterFactory.put(filterFactory.getC(), filterFactory);
		}

		return c2filterFactory;
	}

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length != 1) {
			throw new ParseException("BAM File is not provided!");
		}
		return super.parseArgs(args);
	}
	
}
