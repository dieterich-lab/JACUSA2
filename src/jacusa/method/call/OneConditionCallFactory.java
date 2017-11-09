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

import lib.data.basecall.PileupData;
import lib.worker.WorkerDispatcher;

import org.apache.commons.cli.ParseException;

public class OneConditionCallFactory 
extends CallFactory {

	public OneConditionCallFactory() {
		super(1);
	}
	
	@Override
	public WorkerDispatcher<PileupData> getWorkerDispatcher() {
		if (instance == null) {
			instance = new WorkerDispatcher<PileupData>(this);
		}

		return instance;
	}

	@Override
	public Map<Character, AbstractFilterFactory<PileupData>> getFilterFactories() {
		Map<Character, AbstractFilterFactory<PileupData>> c2filterFactory = 
				new HashMap<Character, AbstractFilterFactory<PileupData>>();

		final List<AbstractFilterFactory<PileupData>> filterFactories = 
				new ArrayList<AbstractFilterFactory<PileupData>>(5);
		
		filterFactories.add(new CombinedDistanceFilterFactory<PileupData>(getParameter()));
		filterFactories.add(new INDEL_DistanceFilterFactory<PileupData>(getParameter()));
		filterFactories.add(new ReadPositionDistanceFilterFactory<PileupData>(getParameter()));
		filterFactories.add(new SpliceSiteDistanceFilterFactory<PileupData>(getParameter()));
		filterFactories.add(new MaxAlleleCountFilterFactory<PileupData>());

		for (final AbstractFilterFactory<PileupData> filterFactory : filterFactories) {
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
