package lib.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jacusa.filter.factory.FilterFactory;
import lib.cli.parameter.GeneralParameter;
import lib.data.has.HasCoordinate;
import lib.data.has.HasLibraryType;
import lib.data.has.HasReferenceBase;
import lib.util.Base;
import lib.util.LibraryType;
import lib.util.Util;
import lib.util.coordinate.Coordinate;

/**
 * Defines interface for container that holds all data that can be referenced by
 * DataType.
 */
public interface DataContainer
		extends HasCoordinate, HasLibraryType, HasReferenceBase, Data<DataContainer>, Serializable {

	<T extends Data<T>> T get(DataType<T> dataType);

	<T extends Data<T>> boolean contains(DataType<T> dataType);

	Collection<DataType<?>> getDataTypes();

	/*
	 * Factory, Builder, and Parser
	 */

	public static interface DataContainerBuilderFactory {

		AbstractDataContainerBuilder createBuilder(Coordinate coordinate, LibraryType libraryType);

	}

	public abstract static class AbstractDataContainerBuilderFactory implements DataContainerBuilderFactory {

		private final GeneralParameter parameter;

		public AbstractDataContainerBuilderFactory(final GeneralParameter parameter) {
			this.parameter = parameter;
		}

		public AbstractDataContainerBuilder createBuilder(final Coordinate coordinate, final LibraryType libraryType) {
			final AbstractDataContainerBuilder builder = new DefaultDataContainer.Builder(coordinate, libraryType);
			addRequired(builder);
			if (parameter != null && parameter.getFilterConfig().hasFiters()) {
				addFilters(builder);
				initFilterDataTypes(builder);
			}
			return builder;
		}

		static public <T extends Data<T>> DataType<T> add(final AbstractDataContainerBuilder builder,
				DataType<T> dataType, T data) {
			builder.with(dataType, data);
			return dataType;
		}

		static public <T extends Data<T>> DataType<T> add(final AbstractDataContainerBuilder builder,
				DataType<T> dataType) {
			builder.with(dataType);
			return dataType;
		}

		protected abstract void addRequired(final AbstractDataContainerBuilder builder);

		protected abstract void addFilters(final AbstractDataContainerBuilder builder);

		private void initFilterDataTypes(final AbstractDataContainerBuilder builder) {
			for (final FilterFactory filterFactory : parameter.getFilterConfig().getFilterFactories()) {
				filterFactory.initDataContainer(builder);
			}
		}

	}

	public abstract static class AbstractDataContainerBuilder implements lib.util.Builder<DataContainer> {

		private final Coordinate coordinate;
		private final LibraryType libraryType;
		private Base referenceBase;

		private final Map<DataType<?>, Object> map;

		protected AbstractDataContainerBuilder(final Coordinate coordinate, final LibraryType libraryType) {
			this.coordinate = coordinate;
			this.libraryType = libraryType;
			referenceBase = Base.N;

			map = new HashMap<>(Util.noRehashCapacity(20));
		}

		public AbstractDataContainerBuilder withReferenceBase(final Base referenceBase) {
			this.referenceBase = referenceBase;
			return this;
		}

		public <T extends Data<T>> boolean contains(final DataType<T> dataType) {
			return map.containsKey(dataType);
		}

		public <T extends Data<T>> T get(final DataType<T> dataType) {
			return dataType.getEnclosingClass().cast(map.get(dataType));
		}

		public <T extends Data<T>> AbstractDataContainerBuilder with(final DataType<T> dataType) {
			if (map.containsKey(dataType)) {
				throw new IllegalArgumentException("Duplicate dataType: " + dataType);
			}
			T data = dataType.newInstance();
			map.put(dataType, data);
			return this;
		}

		public <T extends Data<T>> AbstractDataContainerBuilder with(final DataType<T> dataType, T data) {
			if (map.containsKey(dataType)) {
				throw new IllegalArgumentException("Duplicate dataType: " + dataType);
			}
			map.put(dataType, dataType.getEnclosingClass().cast(data));
			return this;
		}

		protected Coordinate getCoordinate() {
			return coordinate;
		}

		protected LibraryType getLibraryType() {
			return libraryType;
		}

		protected Base getReferenceBase() {
			return referenceBase;
		}

		protected Map<DataType<?>, Object> getMap() {
			return map;
		}

	}

}
