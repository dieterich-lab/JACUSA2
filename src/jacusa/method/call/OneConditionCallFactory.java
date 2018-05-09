package jacusa.method.call;

import jacusa.cli.parameters.CallParameter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactory;
import jacusa.filter.factory.basecall.CombinedFilterFactory;
import jacusa.filter.factory.basecall.INDEL_FilterFactory;
import jacusa.filter.factory.basecall.ReadPositionDistanceFilterFactory;
import jacusa.filter.factory.basecall.SpliceSiteFilterFactory;

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

		filterFactories.add(new CombinedFilterFactory<CallData>());
		filterFactories.add(new INDEL_FilterFactory<CallData>());
		filterFactories.add(new ReadPositionDistanceFilterFactory<CallData>());
		filterFactories.add(new SpliceSiteFilterFactory<CallData>());
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
