package lib.data.builder;

import jacusa.pileup.iterator.variant.Variant;

import java.util.Iterator;

import lib.cli.parameters.AbstractParameters;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.util.Coordinate;

import htsjdk.samtools.SamReader;

public class WindowedIterator<T extends AbstractData> 
implements Iterator<ParallelData<T>> {

	private Coordinate window;
	private Variant<T> filter;
	private AbstractParameters<T> parameters;
	
	private ConditionContainer<T> conditionContainer;
	private ParallelData<T> parallelDataContainer;

	public WindowedIterator(final Coordinate window, final Variant<T> filter, 
			final SamReader[][] readers, final AbstractParameters<T> parameters) {
		this.window = window;
		this.filter	= filter;
		this.parameters = parameters;

		conditionContainer = new ConditionContainer<T>(window, readers, parameters);
		parallelDataContainer = new ParallelData<T>(parameters.getMethodFactory());
	}
	
	@Override
	public boolean hasNext() {
		while (conditionContainer.hasNext()) {
			final Coordinate reference = new Coordinate(conditionContainer.getReferenceAdvancer().getCurrentCoordinate());

			parallelDataContainer.setCoordinate(reference);
			parallelDataContainer.setData(conditionContainer.getData(reference));

			if (filter.isValid(parallelDataContainer)) {
				return true;
			} else {
				parallelDataContainer.reset();
				conditionContainer.advance();
			}				
		}

		return false;
	}

	public ParallelData<T> next() {
		// create copy
		final ParallelData<T> parallelData = new ParallelData<T>(parallelDataContainer);

		// advance to the next position
		conditionContainer.advance();

		// reset container
		parallelDataContainer.reset();

		return parallelData;
	}

	protected ParallelData<T> getParallelPileupData() {
		return parallelDataContainer;
	}

	public Coordinate getWindow() {
		return window;
	}

	public ConditionContainer<T> getConditionContainer() {
		return conditionContainer;
	}

	public AbstractParameters<T> getParameters() {
		return parameters;
	}
	
	@Override
	public void remove() { 
		// not needed
	}
	
}
