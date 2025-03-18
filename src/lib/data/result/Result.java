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
import lib.util.ExtendedInfo;
import lib.util.LibraryType;
import lib.util.Parser;
import lib.util.coordinate.Coordinate;

/**
 * TODO add documentation
 */
public interface Result extends HasParallelData, Serializable {
	
	public static int TOTAL = -1; // FIXME use this to refer to main result
	
	ExtendedInfo getResultInfo();
	ExtendedInfo getResultInfo(int valueIndex);
	
	ExtendedInfo getFilterInfo();
	ExtendedInfo getFilterInfo(int valueIndex);

	void setFiltered(boolean isFiltered);
	boolean isFiltered();

	SortedSet<Integer> getValueIndexes();
	int getValueSize();
	
	double getScore();
	double getScore(int valueIndex);
	
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
			for (int conditionIndex = 0; conditionIndex < conditions; ++conditionIndex) {
				final List<DataContainer.AbstractBuilder> replicates = new ArrayList<>();
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

			for (int conditionIndex = 0; conditionIndex < getConditions(); ++conditionIndex) {
				for (int replicateIndex = 0; replicateIndex < replicates.get(conditionIndex); ++replicateIndex) {
					final DataContainer dataContainer = 
							builders.get(conditionIndex).get(replicateIndex).build();
					pdBuilder.withReplicate(conditionIndex, replicateIndex, dataContainer);
				}
			}
			
			final ParallelData parallelData = pdBuilder.build();
			final ExtendedInfo info = new ExtendedInfo(parallelData.getReplicates());
			return new OneStatResult(Double.NaN, parallelData, info);
		}
		
	}

}
