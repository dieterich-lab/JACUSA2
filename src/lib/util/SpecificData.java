package lib.util;

import lib.data.DataType;
import lib.data.cache.lrtarrest.Position2baseCallCount;
import lib.data.count.BaseSubstitutionCount;
import lib.data.count.PileupCount;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.ArrestPos2BaseCallCountFilteredData;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.filter.BooleanWrapperFilteredData;

// TODO move to appropriate dir
public interface SpecificData<T extends Copyable<T> & Mergeable<T>> { 
// extends GeneralData<T>, Copyable<T>, Mergeable<T>, Serializable {

	public static final DataType<PileupCount> PILEUP_COUNT = 
			DataType.create("Default", PileupCount.class);
	
	public static final DataType<BaseCallCount> ARREST_BCC = 
			DataType.create("Arrest", BaseCallCount.class);
	
	public static final DataType<BaseCallCount> THROUGH_BCC = 
			DataType.create("Through", BaseCallCount.class);
	
	public static final DataType<Position2baseCallCount> AP2BCC = 
			DataType.create("Default", Position2baseCallCount.class);
	
	public static final DataType<BaseSubstitutionCount> BASE_SUBST = 
			DataType.create("Default", BaseSubstitutionCount.class);

	public static final DataType<BaseCallCountFilteredData> F_BCC = 
			DataType.create("Default", BaseCallCountFilteredData.class);
	
	public static final DataType<BooleanWrapperFilteredData> F_BOOLEAN = 
			DataType.create("Default", BooleanWrapperFilteredData.class);
	
	public static final DataType<ArrestPos2BaseCallCountFilteredData> F_AP2BCC = 
			DataType.create("Default", ArrestPos2BaseCallCountFilteredData.class);

}
