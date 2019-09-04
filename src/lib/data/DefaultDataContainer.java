package lib.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lib.data.count.BaseSub2BCC;
import lib.data.count.BaseSub2IntData;
import lib.data.count.PileupCount;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.filter.BooleanFilteredData;
import lib.data.storage.lrtarrest.ArrestPos2BCC;
import lib.util.Base;
import lib.util.LibraryType;
import lib.util.Util;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil;

public class DefaultDataContainer implements DataContainer {
	
	private static final long serialVersionUID = 1L;

	private Coordinate coordinate;
	private LibraryType libraryType;
	private Base unstrandedRefBase;
	
	private final Map<DataType<?>, Object> map;
	
	private DefaultDataContainer(final AbstractBuilder builder) {
		coordinate 		= builder.getCoordinate();
		libraryType 	= builder.getLibraryType();
		unstrandedRefBase = builder.getReferenceBase();
		map 			= builder.getMap();
	}
	
	private DefaultDataContainer(DefaultDataContainer template) {
		coordinate 		= template.getCoordinate().copy();
		libraryType 	= template.getLibraryType();
		unstrandedRefBase 	= template.getUnstrandedRefBase();
		map 			= new HashMap<>(Util.noRehashCapacity(template.getDataTypes().size()));
		for (final DataType<?> dataType : template.getDataTypes()) {
			map.put(dataType, template.get(dataType).copy());
		}
	}

	@Override
	public <T extends Data<T>> T get(DataType<T> dataType) {
		if (! contains(dataType)) {
			return null;
		}
		return dataType.getEnclosingClass().cast(map.get(dataType));
	}

	@Override
	public BaseCallCount getBaseCallCount() {
		return get(DataType.BCC);
	}
	
	@Override
	public BaseCallCount getArrestBaseCallCount() {
		return get(DataType.ARREST_BCC);
	}
	
	@Override
	public ArrestPos2BCC getArrestPos2BCC() {
		return get(DataType.AP2BCC);
	}
	
	@Override
	public BaseCallCountFilteredData getArrestPos2BCCFilteredData() {
		return get(DataType.F_BCC);
	}
	
	@Override
	public BaseSub2IntData getBaseSub2Coverage() {
		return get(DataType.BASE_SUBST2COVERAGE);
	}
	
	@Override
	public BaseSub2IntData getBaseSub2DeletionCount() {
		return get(DataType.BASE_SUBST2DELETION_COUNT);
	}
	
	@Override
	public BaseCallCountFilteredData getBCCFilteredData() {
		return get(DataType.F_BCC);
	}
	
	@Override
	public BaseSub2BCC getBaseSub2BCC() {
		return get(DataType.BASE_SUBST2BCC);
	}
	
	@Override
	public BaseSub2BCC getArrestBaseSub2BCC() {
		return get(DataType.ARREST_BASE_SUBST);
	}
	
	@Override
	public BaseSub2BCC getThroughBaseSub2BCC() {
		return get(DataType.THROUGH_BASE_SUBST);
	}
	
	@Override
	public BooleanFilteredData getBooleanFilteredData() {
		return get(DataType.F_BOOLEAN);
	}
	
	@Override
	public PileupCount getPileupCount() {
		return get(DataType.PILEUP_COUNT);
	}
	
	@Override
	public BaseCallCount getThroughBaseCallCount() {
		return get(DataType.THROUGH_BCC);
	}
	
	@Override
	public IntegerData getDeletionCount() {
		return get(DataType.DELETION_COUNT);
	}

	@Override
	public BaseSub2IntData getBaseSub2InsertionCount() {
		return get(DataType.BASE_SUBST2INSERTION_COUNT);
	}

	@Override
	public IntegerData getInsertionCount() {
		return get(DataType.INSERTION_COUNT);
	}
	
	@Override
	public IntegerData getCoverage() {
		return get(DataType.COVERAGE);
	}
	
	@Override
	public <T extends Data<T>> boolean contains(DataType<T> dataType) {
		return map.containsKey(dataType);
	}
	
	@Override
	public DataContainer copy() {
		return new DefaultDataContainer(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof DefaultDataContainer)) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		final DefaultDataContainer container = (DefaultDataContainer)obj;
		return
				unstrandedRefBase.equals(container.unstrandedRefBase) &&
				libraryType.equals(container.libraryType) &&
				coordinate.equals(container.coordinate) && 
				map.equals(container.map);
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash = 31 * hash + unstrandedRefBase.hashCode();
		hash = 31 * hash + libraryType.hashCode();
		hash = 31 * hash + coordinate.hashCode();
		hash = 31 * hash + map.hashCode();
		return hash;
	}
	
	@Override
	public void merge(final DataContainer provider) {
		unstrandedRefBase 	= Base.mergeBase(unstrandedRefBase, provider.getUnstrandedRefBase());
		libraryType 	= LibraryType.mergeLibraryType(libraryType, provider.getLibraryType());
		coordinate 		= CoordinateUtil.mergeCoordinate(coordinate, provider.getCoordinate());
		
		for (final DataType<?> dataType : provider.getDataTypes()) {
			if (! contains(dataType)) {
				map.put(dataType, provider.get(dataType));				
			} else {
				Object o1 = get(dataType);
				Object o2 = provider.get(dataType);
				dataType.merge(o1, o2);			
			}
		}
	}
	
	@Override
	public LibraryType getLibraryType() {
		return libraryType;
	}
	
	@Override
	public Collection<DataType<?>> getDataTypes() {
		return Collections.unmodifiableCollection(map.keySet());
	}

	@Override
	public Coordinate getCoordinate() {
		return coordinate;
	}
	
	@Override
	public Base getUnstrandedRefBase() {
		return unstrandedRefBase;
	}
	
	@Override
	public Base getAutoRefBase() {
		if (! LibraryType.isStranded(libraryType)) {
			return unstrandedRefBase;
		}
		if (coordinate.isReverseStrand()) {
			return unstrandedRefBase.getComplement();
		}
		return unstrandedRefBase;
	}
	
	@Override
	public final String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append("Coordinates: ");
		sb.append(getCoordinate().toString());
		sb.append('\n');

		sb.append("Library type: ");
		sb.append(getLibraryType().toString());
		sb.append('\n');
		
		for (final DataType<?> dataType : getDataTypes()) {
			sb.append(dataType.toString());
			sb.append('\n');
		}

		return sb.toString();
	}

	/*
	 * Factory, Builder, and Parser
	 */

	public static class Builder extends AbstractBuilder {
		
		public Builder(final Coordinate coordinate, final LibraryType libraryType) {
			super(coordinate, libraryType);
		}
		
		@Override
		public DefaultDataContainer build() {
			return new DefaultDataContainer(this);
		}

	}
	
}
