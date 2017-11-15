package lib.data.validator;

import java.util.List;

import lib.data.AbstractData;

public class AggregatedReplicateDataValidator<T extends AbstractData> implements
		ReplicateDataValidator<T> {

	private final int minCount;
	private final double minRatio;
	
	private List<DataValidator<T>> dataValidators;
	
	
	public AggregatedReplicateDataValidator(final double minRatio, final List<DataValidator<T>> dataValidators) {
		this(0, minRatio, dataValidators);
	}
	
	public AggregatedReplicateDataValidator(final int minCount, final List<DataValidator<T>> dataValidators) {
		this(minCount, 0.0, dataValidators);
	}
	
	public AggregatedReplicateDataValidator(final int minCount, final double minRatio, final List<DataValidator<T>> dataValidators) {
		this.minCount = minCount;
		this.minRatio = minRatio;
		this.dataValidators = dataValidators;
	}
	
	@Override
	public boolean isValid(final T[] replicateData) {
		int total = 0;
		int validCounter = 0;
		double ratio = 0.0;
		for (final T data : replicateData) {
			int check = 0;
			for (final DataValidator<T> dataValidator : dataValidators) {
				if (dataValidator.isValid(data)) {
					check++;
				}
			}
			if (check == dataValidators.size()) {
				validCounter++;
			}
			ratio = (double)validCounter / (double)total;
			if (validCounter >= minCount && ratio >= minRatio) {
				return true;
			}
		}
		return validCounter >= minCount && ratio >= minRatio;
	}
	
}
