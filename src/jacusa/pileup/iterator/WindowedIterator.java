package jacusa.pileup.iterator;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.AbstractData;
import jacusa.data.ParallelPileupData;
import jacusa.pileup.iterator.variant.Variant;
import jacusa.util.Coordinate;

import java.util.Iterator;

import net.sf.samtools.SAMFileReader;

public class WindowedIterator<T extends AbstractData> 
implements Iterator<ParallelPileupData<T>> {

	private Coordinate window;
	private Variant<T> filter;
	private AbstractParameters<T> parameters;
	
	private ConditionContainer<T> conditionContainer;
	private ParallelPileupData<T> parallelDataContainer;

	public WindowedIterator(final Coordinate window, final Variant<T> filter, 
			final SAMFileReader[][] readers, final AbstractParameters<T> parameters) {
		this.window = window;
		this.filter	= filter;
		this.parameters = parameters;

		conditionContainer = new ConditionContainer<T>(window, readers, parameters);
		parallelDataContainer = new ParallelPileupData<T>(parameters.getMethodFactory());
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

	public ParallelPileupData<T> next() {
		// create copy
		final ParallelPileupData<T> parallelData = new ParallelPileupData<T>(parallelDataContainer);

		// advance to the next position
		conditionContainer.advance();

		// reset container
		parallelDataContainer.reset();

		return parallelData;
	}

	protected ParallelPileupData<T> getParallelPileupData() {
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
