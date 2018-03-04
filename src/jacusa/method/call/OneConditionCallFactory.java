package jacusa.method.call;

import jacusa.cli.parameters.CallParameter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactory;
import jacusa.filter.factory.distance.CombinedDistanceFilterFactory;
import jacusa.filter.factory.distance.INDEL_DistanceFilterFactory;
import jacusa.filter.factory.distance.ReadPositionDistanceFilterFactory;
import jacusa.filter.factory.distance.SpliceSiteDistanceFilterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.data.CallData;

import org.apache.commons.cli.ParseException;

public class OneConditionCallFactory 
extends CallFactory {

	public OneConditionCallFactory() {
		super(new CallParameter(1));
	}

	@Override
	public Map<Character, AbstractFilterFactory<CallData>> getFilterFactories() {
		Map<Character, AbstractFilterFactory<CallData>> c2filterFactory = 
				new HashMap<Character, AbstractFilterFactory<CallData>>();

		final List<AbstractFilterFactory<CallData>> filterFactories = 
				new ArrayList<AbstractFilterFactory<CallData>>(5);

		filterFactories.add(new CombinedDistanceFilterFactory<CallData>());
		filterFactories.add(new INDEL_DistanceFilterFactory<CallData>());
		filterFactories.add(new ReadPositionDistanceFilterFactory<CallData>());
		filterFactories.add(new SpliceSiteDistanceFilterFactory<CallData>());
		filterFactories.add(new MaxAlleleCountFilterFactory<CallData>());

		for (final AbstractFilterFactory<CallData> filterFactory : filterFactories) {
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
