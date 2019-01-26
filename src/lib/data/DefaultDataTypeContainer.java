package lib.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lib.data.cache.lrtarrest.ArrestPosition2baseCallCount;
import lib.data.count.BaseSubstitutionCount;
import lib.data.count.PileupCount;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.ArrestPos2BaseCallCountFilteredData;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.filter.BooleanWrapperFilteredData;
import lib.data.has.LibraryType;
import lib.util.Base;
import lib.util.Data;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil;

public class DefaultDataTypeContainer implements DataTypeContainer {
	
	private static final long serialVersionUID = 1L;

	private Coordinate coordinate;
	private LibraryType libraryType;
	private Base referenceBase;
	
	private final Map<DataType<?>, Object> map;
	
	private DefaultDataTypeContainer(final AbstractBuilder builder) {
		coordinate 		= builder.getCoordinate();
		libraryType 	= builder.getLibraryType();
		referenceBase 	= builder.getReferenceBase();
		map 			= builder.getMap();
	}
	
	private DefaultDataTypeContainer(DefaultDataTypeContainer template) {
		coordinate 		= template.getCoordinate().copy();
		libraryType 	= template.getLibraryType();
		referenceBase 	= template.getReferenceBase();
		map 			= new HashMap<>(template.getDataTypes().size());
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
	public ArrestPosition2baseCallCount getArrestPos2BaseCallCount() {
		return get(DataType.AP2BCC);
	}
	
	@Override
	public ArrestPos2BaseCallCountFilteredData getArrestPos2BaseCallCountFilteredData() {
		return get(DataType.F_AP2BCC);
	}
	
	@Override
	public BaseCallCountFilteredData getBaseCallCountFilteredData() {
		return get(DataType.F_BCC);
	}
	
	@Override
	public BaseSubstitutionCount getBaseSubstitutionCount() {
		return get(DataType.BASE_SUBST);
	}
	
	@Override
	public BaseSubstitutionCount getArrestBaseSubstitutionCount() {
		return get(DataType.ARREST_BASE_SUBST);
	}
	
	@Override
	public BaseSubstitutionCount getThroughBaseSubstitutionCount() {
		return get(DataType.THROUGH_BASE_SUBST);
	}
	
	@Override
	public BooleanWrapperFilteredData getBooleanFilteredData() {
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
	public <T extends Data<T>> boolean contains(DataType<T> dataType) {
		return map.containsKey(dataType);
	}
	
	@Override
	public DataTypeContainer copy() {
		return new DefaultDataTypeContainer(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || ! (obj instanceof DefaultDataTypeContainer)) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		final DefaultDataTypeContainer container = (DefaultDataTypeContainer)obj;
		return
				referenceBase.equals(container.referenceBase) &&
				libraryType.equals(container.libraryType) &&
				coordinate.equals(container.coordinate) && 
				map.equals(container.map);
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash = 31 * hash + referenceBase.hashCode();
		hash = 31 * hash + libraryType.hashCode();
		hash = 31 * hash + coordinate.hashCode();
		hash = 31 * hash + map.hashCode();
		return hash;
	}
	
	@Override
	public void merge(final DataTypeContainer provider) {
		referenceBase 	= Base.mergeBase(referenceBase, provider.getReferenceBase());
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
	public Base getReferenceBase() {
		return referenceBase;
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
			// sb.append(get(dataType).toString());
			// sb.append('\n');
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
		public DefaultDataTypeContainer build() {
			return new DefaultDataTypeContainer(this);
		}

	}
	
}
