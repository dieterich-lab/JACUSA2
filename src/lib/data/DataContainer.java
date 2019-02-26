package lib.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jacusa.JACUSA;
import jacusa.filter.factory.FilterFactory;
import lib.cli.options.filter.has.HasReadSubstitution.BaseSubstitution;
import lib.cli.parameter.GeneralParameter;
import lib.data.count.BaseSubstitution2BaseCallCount;
import lib.data.count.BaseSubstitution2IntegerData;
import lib.data.count.PileupCount;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.BaseCallCountFilteredData;
import lib.data.filter.BooleanFilteredData;
import lib.data.has.HasCoordinate;
import lib.data.has.HasLibraryType;
import lib.data.has.HasReferenceBase;
import lib.data.storage.lrtarrest.ArrestPosition2baseCallCount;
import lib.util.Base;
import lib.util.LibraryType;
import lib.util.Util;
import lib.util.coordinate.Coordinate;

public interface DataContainer 
extends HasCoordinate, HasLibraryType, HasReferenceBase, 
		Data<DataContainer>, 
		Serializable {

	<T extends Data<T>> T get(DataType<T> dataType);
	
	PileupCount getPileupCount();
	
	BaseCallCount getBaseCallCount();
	
	BaseSubstitution2BaseCallCount getBaseSubstitutionCount();
	BaseCallCountFilteredData getBaseCallCountFilteredData();
	BooleanFilteredData getBooleanFilteredData();
	
	BaseCallCount getArrestBaseCallCount();
	BaseCallCount getThroughBaseCallCount();
	
	BaseSubstitution2BaseCallCount getArrestBaseSubstitutionCount();
	BaseSubstitution2BaseCallCount getThroughBaseSubstitutionCount();
	
	ArrestPosition2baseCallCount getArrestPos2BaseCallCount();
	BaseCallCountFilteredData getArrestPos2BaseCallCountFilteredData();
	
	IntegerData getDeletionCount();
	IntegerData getCoverage();
	
	<T extends Data<T>> boolean contains(DataType<T> dataType);
	Collection<DataType<?>> getDataTypes();

	/*
	 * Factory, Builder, and Parser
	 */
	
	public static interface BuilderFactory {
	
		AbstractBuilder createBuilder(Coordinate coordinate, LibraryType libraryType);

	}
	
	public static abstract class AbstractBuilderFactory implements BuilderFactory {
		
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
		
		protected <T extends Data<T>> void add(final AbstractBuilder builder, final DataType<T> dataType) {
			builder.with(
					dataType,
					dataType.newInstance());
		}
		
		protected void addBaseSubstitution(final AbstractBuilder builder, final DataType<BaseSubstitution2BaseCallCount> dataType) {
			add(builder, dataType);
			final BaseSubstitution2BaseCallCount bsc = builder.get(dataType);
			for (final BaseSubstitution baseSub : parameter.getReadSubstitutions()) {
				bsc.set(baseSub, JACUSA.BCC_FACTORY.create());
			}
		}
		
		protected void addDeletionCount(final AbstractBuilder builder, final DataType<BaseSubstitution2IntegerData> dataType) {
			add(builder, dataType);
			final BaseSubstitution2IntegerData bsc = builder.get(dataType);
			for (final BaseSubstitution baseSub : parameter.getReadSubstitutions()) {
				bsc.set(baseSub, new IntegerData());
			}
		}
		
		protected void addCoverage(final AbstractBuilder builder, final DataType<BaseSubstitution2IntegerData> dataType) {
			add(builder, dataType);
			final BaseSubstitution2IntegerData bsc = builder.get(dataType);
			for (final BaseSubstitution baseSub : parameter.getReadSubstitutions()) {
				bsc.set(baseSub, new IntegerData());
			}
		}
		
		protected abstract void addRequired(final AbstractBuilder builder);
		protected abstract void addFilters(final AbstractBuilder builder);
		
		private void initFilterDataTypes(final AbstractBuilder builder) {
			for (final FilterFactory filterFactory : parameter.getFilterConfig().getFilterFactories()) {
				filterFactory.initDataContainer(builder);
			}
		}
		
	}
	
	// FIXME add all available automatically
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
			
			add(builder, DataType.ARREST_BASE_SUBST);
			add(builder, DataType.THROUGH_BASE_SUBST);
			add(builder, DataType.DELETION_COUNT);
		}
		
		@Override
		protected void addFilters(final AbstractBuilder builder) {
			add(builder, DataType.F_BCC);
			add(builder, DataType.F_BOOLEAN);
		}

	}

	public static abstract class AbstractBuilder
	implements lib.util.Builder<DataContainer> {
		
		private final Coordinate coordinate;
		private final LibraryType libraryType;
		private Base referenceBase;
		
		private final Map<DataType<?>, Object> map;
		
		protected AbstractBuilder(final Coordinate coordinate, final LibraryType libraryType) {
			this.coordinate = coordinate;
			this.libraryType = libraryType;
			referenceBase = Base.N;
			
			map = new HashMap<DataType<?>, Object>(Util.noRehashCapacity(20));
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
		
		public <T extends Data<T>> AbstractBuilder with(final DataType<T> dataType) {
			if (map.containsKey(dataType)) {
				throw new IllegalArgumentException("Duplicate dataType: " + dataType); 
			}
			map.put(dataType, dataType.newInstance());
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

	/*
	public static abstract class AbstractParser 
	implements lib.util.Parser<DataContainer> {

		public static final char FIELD_SEP = '\t';
	
		private final char fieldSep;
				
		private final Coordinate.AbstractParser coordinateParser;
		
		protected AbstractParser() {
			this(FIELD_SEP, new OneCoordinate.Parser());
		}
		
		protected AbstractParser(final char fieldSep, final Coordinate.AbstractParser coordinateParser) {
			this.fieldSep = fieldSep;
			this.coordinateParser = coordinateParser;
		}

		public final String wrap(DataContainer dataContainer) {
			final List<String> e = Arrays.asList(
					wrapBase(dataContainer.getReferenceBase()),
					wrapLibraryType(dataContainer.getLibraryType()),
					wrapCoordinate(dataContainer.getCoordinate()) );
			wrapSpecific(dataContainer, e);
			return StringUtil.join(Character.toString(fieldSep), e);
		}
		
		public final DataContainer parse(String s) {
			final String[] cols = s.split(Character.toString(fieldSep));
			return parseSpecific(
					new DefaultBuilderFactory()
						.createBuilder(parseCoordinate(cols[0]), parseLibraryType(cols[1]))
						.withReferenceBase(parseBase(cols[2])),
					Arrays.copyOfRange(cols, 3, cols.length));
		}
		
		protected abstract DataContainer parseSpecific(AbstractBuilder builder, String[] cols);
		protected abstract void wrapSpecific(DataContainer data, List<String> e);
		
		protected final String wrapBase(final Base base) {
			return base.toString();
		}
		
		protected final String wrapLibraryType(final LibraryType libraryType) {
			return libraryType.toString();
		}

		protected final String wrapCoordinate(final Coordinate coordinate) {
			return coordinateParser.wrap(coordinate);
		}

		protected final Base parseBase(final String s) {
			if (s == null || s.length() != 1) {
				throw new IllegalArgumentException("s cannot be parsed to Base: " + s);
			}
			return Base.valueOf(s.charAt(0));
		}

		protected final LibraryType parseLibraryType(final String s) {
			if (s == null) {
				throw new IllegalArgumentException("s cannot be null");
			}
			final LibraryType l = LibraryType.valueOf(s);
			if (l == null) {
				throw new IllegalStateException("s cannot be parsed to LIBRARY_TYPE: " + s);
			}
			return l;
		}
		
		protected final Coordinate parseCoordinate(final String s) {
			return coordinateParser.parse(s);
		}
		
	}
	*/
	
}
