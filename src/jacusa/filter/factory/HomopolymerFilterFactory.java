package jacusa.filter.factory;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import jacusa.filter.AbstractFilter;
import jacusa.filter.HomopolymerFilter;
import jacusa.filter.cache.HomopolymerFilterCache;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.adder.AbstractDataAdder;
import lib.data.builder.ConditionContainer;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.filter.HasBooleanFilterData;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateController;

public class HomopolymerFilterFactory<T extends AbstractData & HasBaseCallCount & HasReferenceBase & HasBooleanFilterData> 
extends AbstractDataFilterFactory<T> {

	// default length of consecutive identical base call for
	// a homopolymer
	private static final int MIN_HOMOPOLYMER_LENGTH = 7;
	// chosen length of homopolymer
	private int length;
		
	public HomopolymerFilterFactory() {
		super('Y', 
				"Filter wrong variant calls within homopolymers.");
		length = MIN_HOMOPOLYMER_LENGTH;
	}

	@Override
	protected Options getOptions() {
		final Options options = new Options();
		
		options.addOption(Option.builder("length")
				.hasArg(true)
				.desc("Default: " + MIN_HOMOPOLYMER_LENGTH)
				.build());
		
		return options;
	}
	
	@Override
	public void processCLI(final CommandLine cmd) {
		// format Y:length
		for (final Option option : cmd.getOptions()) {
			final String opt = option.getOpt();
			switch (opt) {
			case "length":
				final int length = Integer.valueOf(cmd.getOptionValue(opt));
				if (length < 0) {
					throw new IllegalArgumentException("Invalid length: " + opt);
				}
				this.length = length;
				break;
				
			default:
				break;
			}
		}
	}

	@Override
	public RecordWrapperDataCache<T> createFilterCache(
			AbstractConditionParameter<T> conditionParameter,
			CoordinateController coordinateController) {
		
		return new HomopolymerFilterCache<T>(getC(), MIN_HOMOPOLYMER_LENGTH, coordinateController);
	}
	
	@Override
	public AbstractFilter<T> createFilter(final CoordinateController coordinateController, 
			final ConditionContainer<T> conditionContainer) {

		final AbstractParameter<T, ?> parameter = conditionContainer.getParameter(); 
		return new HomopolymerFilter<T>(getC(), length, parameter);
	}

	@Override
	public void addFilteredData(StringBuilder sb, T data) {
		if (data.getBooleanFilterData().get(getC())) {
			sb.append('1');
		} else {
			sb.append('0');
		}
	}
	
	// TODO
	public class Homopolymer extends AbstractDataAdder<T> {

		private final Map<Integer, Boolean> isHomopolymer;
		
		public Homopolymer(final CoordinateController coordinateController) {
			super(coordinateController);

			isHomopolymer				= new HashMap<Integer, Boolean>(coordinateController.getActiveWindowSize() / 2);
		}
		
		public void clear() {
			isHomopolymer.clear();
		}
		
		public void addData(T data, final Coordinate coordinate) {
			final int referencePosition = coordinate.getPosition();
			
			if (! isHomopolymer.containsKey(referencePosition)) {
				checkHomopolymer(coordinate, length);
			}
			data.getBooleanFilterData().add(getC(), isHomopolymer.get(referencePosition));
		}
		
		private void checkHomopolymer(final Coordinate coordinate, final int minLength) {
			/* TODO
			final ReferenceProvider referenceProvider = getCoordinateController().getReferenceProvider();
			final Coordinate tmp = new Coordinate(coordinate);
			 */
		}
		
	}
	
}
