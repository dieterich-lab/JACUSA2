package lib.data.builder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import htsjdk.samtools.SamReader;

import lib.cli.parameters.AbstractParameters;
import lib.data.AbstractData;
import lib.location.CoordinateAdvancer;
import lib.location.CoordinateContainer;
import lib.location.StrandedCoordinateAdvancer;
import lib.location.UnstrandedCoordinateAdvancer;
import lib.util.Coordinate;
import lib.util.Coordinate.STRAND;

public class ConditionContainer<T extends AbstractData> {

	private Coordinate window;
	private CoordinateAdvancer referenceAdvancer;
	private Map<Integer, ReplicateContainer<T>> container;
	private AbstractParameters<T> parameters;

	public ConditionContainer(final Coordinate window, SamReader[][] readers, final AbstractParameters<T> parameters) {
		this.window 	= window;
		this.parameters = parameters;

		container 			= initContainer(window, readers, parameters);
		referenceAdvancer 	= createReferenceAdvancer(window, parameters.getConditionsSize());
		adjustTarget(referenceAdvancer.getCurrentCoordinate(), window.getEnd());
		for (ReplicateContainer<T> replicateContainer : container.values()) {
			replicateContainer
			.getCoordinateContainer()
			.adjustCoordinate(referenceAdvancer.getCurrentCoordinate());
		}
	}

	public ReplicateContainer<T> getReplicatContainer(final int conditionIndex) {
		return container.get(conditionIndex);
	}

	public T[][] getData(final Coordinate coordinate) {
		final int conditions = parameters.getConditionsSize();

		final T[][] data = parameters.getMethodFactory().createContainer(conditions);
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			data[conditionIndex] = getReplicatContainer(conditionIndex).getData(coordinate);
		}

		return data;
	}

	public int getAlleleCount(final Coordinate coordinate) {
		final int conditions = parameters.getConditionsSize();
		final Set<Integer> alleles = new HashSet<Integer>(4);

		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			alleles.addAll(getReplicatContainer(conditionIndex).getAlleles(coordinate));
		}

		return alleles.size();
	}

	public int getAlleleCount(final int conditionIndex, final Coordinate coordinate) {
		return getReplicatContainer(conditionIndex).getAlleles(coordinate).size();
	}

	public boolean advance() {
		referenceAdvancer.advance();

		if (checkReferenceWithinWindow()) {
			for (int conditionIndex = 0; conditionIndex < container.size(); conditionIndex++) {
				getCoordinateContainer(conditionIndex).adjustCoordinate(referenceAdvancer.getCurrentCoordinate());
			}

			return true;
		}

		return false;
	}

	public boolean hasNext() {
		while (checkReferenceWithinWindow()) {
			if (! isCovered(referenceAdvancer.getCurrentCoordinate())) {
				// TODO make it faster
				
				if (! advance()) {
					return false;
				}
			} else {
				return true;
			}
		}

		return false;
	}

	public CoordinateAdvancer getReferenceAdvancer() {
		return referenceAdvancer;
	}

	public boolean checkReferenceWithinWindow() {
		final int position = referenceAdvancer.getCurrentCoordinate().getPosition();
		return position >= window.getStart() && position <= window.getEnd(); 		
	}
	
	/**
	 * Set target to next position <= maxPosition
	 * @param target
	 * @param maxPosition
	 */
	private void adjustTarget(final Coordinate target, final int maxPosition) {
		int position = maxPosition;
		for (final ReplicateContainer<T> replicateContainer : container.values()) {
			final int newPosition = replicateContainer.getNextPosition(target);
			position = Math.min(position, newPosition);
		}

		target.setPosition(position);
	}

	private boolean isCovered(final Coordinate coordinate) {
		for (final ReplicateContainer<T> replicateContainer : container.values()) {
			if (! replicateContainer.isCovered(coordinate)) {
				return false;
			}
		}

		return true;
	}

	private Map<Integer, ReplicateContainer<T>> initContainer(final Coordinate window, 
			final SamReader[][] readers, 
			final AbstractParameters<T> parameters) {
		final Map<Integer, ReplicateContainer<T>> container = new HashMap<Integer, ReplicateContainer<T>>(parameters.getConditionsSize());

		for (int conditionIndex = 0; conditionIndex < parameters.getConditionsSize(); conditionIndex++) {
			final ReplicateContainer<T> replicateContainer = new ReplicateContainer<T>(window, conditionIndex, readers[conditionIndex], parameters);
			container.put(conditionIndex, replicateContainer);
		}

		return container;
	}

	private CoordinateAdvancer createReferenceAdvancer(final Coordinate window, final int conditions) {
		final Coordinate coordinate = new Coordinate(window.getContig(), window.getStart(), window.getStrand());
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			if (getReplicatContainer(conditionIndex).isStranded()) {
				coordinate.setStrand(STRAND.FORWARD);
				return new StrandedCoordinateAdvancer(coordinate);
			}
		}

		return new UnstrandedCoordinateAdvancer(coordinate);
	}

	private CoordinateContainer getCoordinateContainer(final int conditionIndex) {
		return container.get(conditionIndex).getCoordinateContainer();
	}

	/*
	 * 			// check that all conditions have some coverage position
			for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
				if (! hasNext(conditionIndex)) {
					return false;
				}
			}

			int check = 0;
			for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
				final int refPos = coordinateContainer.getCoordinate(CONDITON_INDEX).getStart();
				final int condPos = coordinateContainer.getCoordinate(conditionIndex).getStart();
	
				int compare = 0;
				if (refPos < condPos) {
					compare = -1;
				} else if (refPos > condPos) {
					compare = 1;
				}

				switch (compare) {

				case -1:
					// adjust actualPosition; instead of iterating jump to specific position
					if (! adjustCurrentGenomicPosition(CONDITON_INDEX, condPos)) {
						return false;
					}
					
					break;
	
				case 0:
					if (isCovered(coordinateContainer.getCoordinate(conditionIndex), conditionDataBuilders.get(conditionIndex))) {
						check++;
					}
					
					break;
	
				case 1:
					// adjust actualPosition; instead of iterating jump to specific position
					if (! adjustCurrentGenomicPosition(conditionIndex, refPos)) {
						return false;					
					}

					break;
				}
			}
			
			*/
	
}