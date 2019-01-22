package lib.data.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

import lib.data.DataType;
import lib.data.DataTypeContainer;
import lib.data.DefaultDataTypeContainer;
import lib.data.ParallelData;
import lib.data.has.HasParallelData;
import lib.data.has.LibraryType;
import lib.util.Base;
import lib.util.Data;
import lib.util.Info;
import lib.util.Parser;
import lib.util.coordinate.Coordinate;

public interface Result 
extends HasParallelData, Serializable {
	
	Info getResultInfo();
	Info getResultInfo(int valueIndex);
	
	Info getFilterInfo();
	Info getFilterInfo(int valueIndex);

	void setFiltered(boolean isFiltered);
	boolean isFiltered();

	SortedSet<Integer> getValueIndex();
	int getValueSize();
	
	double getStat();
	double getStat(int valueIndex);

public static class ResultBuilder implements lib.util.Builder<Result> {
		
		private final Coordinate coordinate;
		private final List<LibraryType> libraryTypes;
		private final Base referenceBase;
		
		private final List<List<DataTypeContainer.AbstractBuilder>> builders;

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
			builders = new ArrayList<List<DataTypeContainer.AbstractBuilder>>(conditions);
			for (int conditionIndex = 0; conditionIndex < conditions; ++conditionIndex) {
				final List<DataTypeContainer.AbstractBuilder> replicates = 
						new ArrayList<DataTypeContainer.AbstractBuilder>();
				builders.add(replicates);
			}
		}
		
		public <T extends Data<T>> ResultBuilder with(
				final int condition, final int replicate,
				final String s, final Parser<T> parser, 
				final DataType<T> dataType) {

			final List<DataTypeContainer.AbstractBuilder> replicateBuilders = 
					builders.get(condition);
			
			if (replicateBuilders.size() <= replicate) {
				replicateBuilders.add(
						new DefaultDataTypeContainer.Builder(
								coordinate, 
								libraryTypes.get(condition))
						.withReferenceBase(referenceBase) );
			}
			final DataTypeContainer.AbstractBuilder dataTypeBuilder = 
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
					final DataTypeContainer dataTypeContainer = 
							builders.get(condition).get(replicate).build();
					pdBuilder.withReplicate(condition, replicate, dataTypeContainer);
				}
			}
			
			final ParallelData parallelData = pdBuilder.build();  
			return new OneStatResult(Double.NaN, parallelData);
		}
		
	}

}
