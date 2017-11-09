package jacusa.method.call;

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

import lib.data.BaseQualData;
import lib.worker.WorkerDispatcher;

import org.apache.commons.cli.ParseException;

public class OneConditionCallFactory 
extends CallFactory {

	public OneConditionCallFactory() {
		super(1);
	}
	
	@Override
	public WorkerDispatcher<BaseQualData> getWorkerDispatcher() {
		if (instance == null) {
			instance = new WorkerDispatcher<BaseQualData>(this);
		}

		return instance;
	}

	@Override
	public Map<Character, AbstractFilterFactory<BaseQualData>> getFilterFactories() {
		Map<Character, AbstractFilterFactory<BaseQualData>> c2filterFactory = 
				new HashMap<Character, AbstractFilterFactory<BaseQualData>>();

		final List<AbstractFilterFactory<BaseQualData>> filterFactories = 
				new ArrayList<AbstractFilterFactory<BaseQualData>>(5);
		
		filterFactories.add(new CombinedDistanceFilterFactory<BaseQualData>(getParameters()));
		filterFactories.add(new INDEL_DistanceFilterFactory<BaseQualData>(getParameters()));
		filterFactories.add(new ReadPositionDistanceFilterFactory<BaseQualData>(getParameters()));
		filterFactories.add(new SpliceSiteDistanceFilterFactory<BaseQualData>(getParameters()));
		filterFactories.add(new MaxAlleleCountFilterFactory<BaseQualData>());

		for (final AbstractFilterFactory<BaseQualData> filterFactory : filterFactories) {
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
