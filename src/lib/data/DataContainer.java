package lib.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jacusa.filter.factory.FilterFactory;
import lib.cli.parameter.GeneralParameter;
import lib.data.count.BaseSub2BCC;
import lib.data.count.BaseSub2IntData;
import lib.data.count.PileupCount;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.filter.BooleanFilteredData;
import lib.data.has.HasCoordinate;
import lib.data.has.HasLibraryType;
import lib.data.has.HasReferenceBase;
import lib.data.storage.lrtarrest.ArrestPos2BCC;
import lib.util.Base;
import lib.util.LibraryType;
import lib.util.Util;
import lib.util.coordinate.Coordinate;

/**
 * Defines interface for container that holds all data that can be referenced by
 * DataType.
 */
public interface DataContainer 
extends HasCoordinate, HasLibraryType, HasReferenceBase, 
		Data<DataContainer>, 
		Serializable {
	
	<T extends Data<T>> T get(DataType<T> dataType);
	
	/* 
	 * the following methods are provided for convenience
	 * T get(DataType<T> dataType)
	 * is actually
	 * PileupCount get(DataType.PileupCount)
	 */
	
	
	PileupCount getPileupCount();
	
	BaseCallCount getBaseCallCount();
	
	BaseSub2BCC getBaseSub2BCC();
	
	BaseCallCountFilteredData getBCCFilteredData();
	BooleanFilteredData getBooleanFilteredData();
	
	BaseCallCount getArrestBaseCallCount();
	BaseCallCount getThroughBaseCallCount();
	
	BaseSub2BCC getArrestBaseSub2BCC();
	BaseSub2BCC getThroughBaseSub2BCC();
	
	ArrestPos2BCC getArrestPos2BCC();
	BaseCallCountFilteredData getArrestPos2BCCFilteredData();
	
	BaseSub2IntData getBaseSub2Coverage();
	BaseSub2IntData getBaseSub2DeletionCount();
	BaseSub2IntData getBaseSub2InsertionCount();
	
	<T extends Data<T>> boolean contains(DataType<T> dataType);
	Collection<DataType<?>> getDataTypes();

	/*
	 * Factory, Builder, and Parser
	 */
	
	public static interface BuilderFactory {
	
		AbstractBuilder createBuilder(Coordinate coordinate, LibraryType libraryType);

	}
	
	public abstract static class AbstractBuilderFactory implements BuilderFactory {
		
		private final GeneralParameter parameter;
		
		public AbstractBuilderFactory(final GeneralParameter parameter) {
			this.parameter = parameter;
		}
		
		public AbstractBuilder createBuilder(Coordinate coordinate, LibraryType libraryType) {
			final AbstractBuilder builder = new DefaultDataContainer.Builder(coordinate, libraryType);
			addRequired(builder);
			if (parameter != null && parameter.getFilterConfig().hasFiters()) {
				addFilters(builder);
				initFilterDataTypes(builder);
			}
			return builder;
		}
		
		protected <T extends Data<T>> void guardedAdd(final AbstractBuilder builder, final DataType<T> dataType) {
			if (builder.contains(dataType)) {
				return;
			}
			builder.with(
					dataType,
					dataType.newInstance());
		}
		
		protected <T extends Data<T>> void add(final AbstractBuilder builder, final DataType<T> dataType) {
			builder.with(
					dataType,
					dataType.newInstance());
		}
		
		/* TODO remove never used
		protected void addBaseSub2bcc(final AbstractBuilder builder, final DataType<BaseSub2BCC> dataType) {
			add(builder, dataType);
			final BaseSub2BCC bsc = builder.get(dataType);
			for (final BaseSub baseSub : parameter.getReadTags()) {
				bsc.set(baseSub, BaseCallCount.create());
			}
		}
		
		protected void addBaseSub2int(final AbstractBuilder builder, final DataType<BaseSub2IntData> dataType) {
			if (builder.contains(dataType)) {
				return;
			}
			add(builder, dataType);
			final BaseSub2IntData bsc = builder.get(dataType);
			for (final BaseSub baseSub : parameter.getReadTags()) {
				bsc.set(baseSub, new IntegerData());
			}
		}
		*/
		
		protected abstract void addRequired(final AbstractBuilder builder);
		protected abstract void addFilters(final AbstractBuilder builder);
		
		private void initFilterDataTypes(final AbstractBuilder builder) {
			for (final FilterFactory filterFactory : parameter.getFilterConfig().getFilterFactories()) {
				filterFactory.initDataContainer(builder);
			}
		}
		
	}
	
	public static class DefaultBuilderFactory extends AbstractBuilderFactory {
		
		public DefaultBuilderFactory() {
			super(null);
		}
	
		@Override
		protected void addRequired(final AbstractBuilder builder) {
			add(builder, DataType.PILEUP_COUNT);
			add(builder, DataType.BCC);
			add(builder, DataType.ARREST_BCC);
			add(builder, DataType.THROUGH_BCC);
			add(builder, DataType.AP2BCC);
			
			add(builder, DataType.BASE_SUBST2BCC);
			add(builder, DataType.BASE_SUBST2DELETION_COUNT);
			add(builder, DataType.BASE_SUBST2INSERTION_COUNT);
			add(builder, DataType.BASE_SUBST2COVERAGE);
			
			add(builder, DataType.ARREST_BASE_SUBST);
			add(builder, DataType.THROUGH_BASE_SUBST);
		}
		
		@Override
		protected void addFilters(final AbstractBuilder builder) {
			add(builder, DataType.F_BCC);
			add(builder, DataType.F_BOOLEAN);
		}

	}

	public abstract static class AbstractBuilder
	implements lib.util.Builder<DataContainer> {
		
		private final Coordinate coordinate;
		private final LibraryType libraryType;
		private Base referenceBase;
		
		private final Map<DataType<?>, Object> map;
		
		protected AbstractBuilder(final Coordinate coordinate, final LibraryType libraryType) {
			this.coordinate = coordinate;
			this.libraryType = libraryType;
			referenceBase = Base.N;
			
			map = new HashMap<>(Util.noRehashCapacity(20));
		}
		
		public AbstractBuilder withReferenceBase(final Base referenceBase) {
			this.referenceBase = referenceBase;
			return this;
		}

		public <T extends Data<T>> boolean contains(final DataType<T> dataType) {
			return map.containsKey(dataType);
		}
		
		public <T extends Data<T>> T get(final DataType<T> dataType) {
			if (! contains(dataType)) {
				return null;
			}
			return dataType.getEnclosingClass().cast(map.get(dataType));
		}
		
		public <T extends Data<T>> AbstractBuilder guardedWith(final DataType<T> dataType) {
			if(! contains(dataType)) {
				return with(dataType);
			}
			return this;
		}
		
		public <T extends Data<T>> AbstractBuilder with(final DataType<T> dataType) {
			if (map.containsKey(dataType)) {
				throw new IllegalArgumentException("Duplicate dataType: " + dataType); 
			}
			map.put(dataType, dataType.newInstance());
			return this;
		}
		
		public <T extends Data<T>> AbstractBuilder guardedWith(final DataType<T> dataType, T data) {
			if(! contains(dataType)) {
				return with(dataType, data);
			}
			return this;
		}
		
		public <T extends Data<T>> AbstractBuilder with(final DataType<T> dataType, T data) {
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
