package lib.data.builder;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.BaseQualReadInfoData;
import lib.data.cache.AlignmentCache;
import lib.util.Coordinate;

/**
 * @author Michael Piechotta
 *
 */
public class RTArrestPileupBuilder<T extends BaseQualReadInfoData>
extends AbstractDataBuilder<T> {

	private final AbstractDataBuilder<T> dataBuilder;
	
	// TODO use cache from dataBuilder
	private AlignmentCache cache;
	
	public RTArrestPileupBuilder(final AbstractConditionParameter<T> conditionParameter,
			final AbstractParameter<T> generalParameter, final AbstractDataBuilder<T> dataBuilder, 
			final AlignmentCache cache) {
		super(conditionParameter, generalParameter, dataBuilder.getLibraryType(), cache);
		this.dataBuilder = dataBuilder;
	}
		
	@Override
	public T getData(final Coordinate coordinate) {
		T data = dataBuilder.getData(coordinate);

		data.getReadInfoCount().setStart(cache.getReadStartCount(coordinate));
		data.getReadInfoCount().setEnd(cache.getReadEndCount(coordinate));
		// TODO data.getReadInfoCount().setInner(cache.getCoverage(windowPosition, strand) - 
		//		readStartCount[windowPosition] - 
		//		readEndCount[windowPosition]);

		int arrest = 0;
		int through = 0;

		switch (getLibraryType()) {
		
		case UNSTRANDED:
			arrest 	+= data.getReadInfoCount().getStart();
			arrest 	+= data.getReadInfoCount().getEnd();
			through += data.getReadInfoCount().getInner();
			break;

		case FR_FIRSTSTRAND:
			arrest 	+= data.getReadInfoCount().getEnd();
			through += data.getReadInfoCount().getInner();
			break;

		case FR_SECONDSTRAND:
			arrest 	+= data.getReadInfoCount().getStart();
			through += data.getReadInfoCount().getInner();
			break;				
		}

		data.getReadInfoCount().setArrest(arrest);
		data.getReadInfoCount().setThrough(through);
		
		return data;
	}

	@Override
	public void clearCache() {
		dataBuilder.clearCache();
		super.clearCache();
	}

}