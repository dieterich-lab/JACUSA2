package lib.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

	private final Map<DataType<?>, Object> type2data;

	private DefaultDataContainer(final AbstractDataContainerBuilder builder) {
		coordinate = builder.getCoordinate();
		libraryType = builder.getLibraryType();
		unstrandedRefBase = builder.getReferenceBase();

		type2data = builder.getMap();
	}

	private DefaultDataContainer(DefaultDataContainer template) {
		coordinate = template.getCoordinate().copy();
		libraryType = template.getLibraryType();
		unstrandedRefBase = template.getUnstrandedRefBase();
		type2data = new HashMap<>(Util.noRehashCapacity(template.getDataTypes().size()));
		for (final DataType<?> dataType : template.getDataTypes()) {
			type2data.put(dataType, template.get(dataType).copy());
		}
	}

	@Override
	public <T extends Data<T>> T get(DataType<T> dataType) {
		if (!contains(dataType)) {
			return null;
		}

		// TODO if derived dataType execute code
		return dataType.getEnclosingClass().cast(type2data.get(dataType));
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = 31 * hash + unstrandedRefBase.hashCode();
		hash = 31 * hash + libraryType.hashCode();
		hash = 31 * hash + coordinate.hashCode();
		hash = 31 * hash + type2data.hashCode();
		return hash;
	}

	@Override
	public void merge(final DataContainer provider) {
		unstrandedRefBase = Base.mergeBase(unstrandedRefBase, provider.getUnstrandedRefBase());
		libraryType = LibraryType.mergeLibraryType(libraryType, provider.getLibraryType());
		coordinate = CoordinateUtil.mergeCoordinate(coordinate, provider.getCoordinate());

		for (final DataType<?> dataType : provider.getDataTypes()) {
			if (!contains(dataType)) {
				type2data.put(dataType, provider.get(dataType));
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
		return Collections.unmodifiableCollection(type2data.keySet());
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
		if (libraryType == LibraryType.UNSTRANDED) {
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

	public static class Builder extends AbstractDataContainerBuilder {

		public Builder(final Coordinate coordinate, final LibraryType libraryType) {
			super(coordinate, libraryType);
		}

		@Override
		public DefaultDataContainer build() {
			return new DefaultDataContainer(this);
		}

	}

	@Override
	public DataContainer copy() {
		return new DefaultDataContainer(this);
	}

	@Override
	public <T extends Data<T>> boolean contains(DataType<T> dataType) {
		return type2data.containsKey(dataType);
	}

}
