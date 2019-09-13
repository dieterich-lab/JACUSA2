package lib.data.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

import lib.data.DataType;
import lib.data.Data;
import lib.data.DataContainer;
import lib.data.DefaultDataContainer;
import lib.data.ParallelData;
import lib.data.has.HasParallelData;
import lib.util.Base;
import lib.util.Info;
import lib.util.LibraryType;
import lib.util.Parser;
import lib.util.coordinate.Coordinate;

/**
 * TODO
 */
public interface Result extends HasParallelData, Serializable {
	
	public static int TOTAL = -1;
	
	Info getResultInfo();
	Info getResultInfo(int valueIndex);
	
	Info getFilterInfo();
	Info getFilterInfo(int valueIndex);

	void setFiltered(boolean isFiltered);
	boolean isFiltered();

	SortedSet<Integer> getValuesIndex();
	int getValueSize();
	
	double getStat();
	double getStat(int valueIndex);

public static class ResultBuilder implements lib.util.Builder<Result> {
		
		private final Coordinate coordinate;
		private final List<LibraryType> libraryTypes;
		private final Base referenceBase;
		
		private final List<List<DataContainer.AbstractBuilder>> builders;

		public ResultBuilder(final Coordinate coordinate, final List<LibraryType> libraryTypes) {
			this(coordinate, libraryTypes, Base.N);
		}
		
		public ResultBuilder(
				final Coordinate coordinate, 
				final List<LibraryType> libraryTypes,
				final Base referenceBase) {
			
			this.coordinate 	= coordinate;
			this.libraryTypes 	= libraryTypes;
			this.referenceBase 	= referenceBase;
			
			final int conditions = libraryTypes.size();
			builders = new ArrayList<>(conditions);
			for (int condI = 0; condI < conditions; ++condI) {
				final List<DataContainer.AbstractBuilder> replicates = 
						new ArrayList<>();
				builders.add(replicates);
			}
		}
		
		public <T extends Data<T>> ResultBuilder with(
				final int condition, final int replicate,
				final String s, final Parser<T> parser, 
				final DataType<T> dataType) {

			final List<DataContainer.AbstractBuilder> replicateBuilders = 
					builders.get(condition);
			
			if (replicateBuilders.size() <= replicate) {
				replicateBuilders.add(
						new DefaultDataContainer.Builder(
								coordinate, 
								libraryTypes.get(condition))
						.withReferenceBase(referenceBase) );
			}
			final DataContainer.AbstractBuilder dataTypeBuilder = 
					replicateBuilders.get(replicate);
			
			final T data = parser.parse(s);
			dataTypeBuilder.with(dataType, data);
			
			return this;
		}
		
		private int getConditions() {
			return builders.size();
		}
		
		@Override
		public Result build() {
			final List<Integer> replicates = builders.stream()
					.map(l -> l.size())
					.collect(Collectors.toList());
			final ParallelData.Builder pdBuilder = 
					new ParallelData.Builder(getConditions(), replicates);

			for (int condition = 0; condition < getConditions(); ++condition) {
				for (int replicate = 0; replicate < replicates.get(condition); ++replicate) {
					final DataContainer dataContainer = 
							builders.get(condition).get(replicate).build();
					pdBuilder.withReplicate(condition, replicate, dataContainer);
				}
			}
			
			final ParallelData parallelData = pdBuilder.build();  
			return new OneStatResult(Double.NaN, parallelData);
		}
		
	}

}
