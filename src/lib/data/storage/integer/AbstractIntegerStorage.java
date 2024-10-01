package lib.data.storage.integer;

import lib.data.DataContainer;
import lib.data.IntegerData;
import lib.data.fetcher.Fetcher;
import lib.data.storage.AbstractStorage;
import lib.data.storage.container.SharedStorage;
import lib.util.coordinate.Coordinate;

abstract class AbstractIntegerStorage extends AbstractStorage {

	private final Fetcher<IntegerData> fetcher;

	AbstractIntegerStorage(final SharedStorage sharedStorage, final Fetcher<IntegerData> fetcher) {

		super(sharedStorage);
		this.fetcher = fetcher;
	}

	@Override
	public void populate(DataContainer container, int winPos, Coordinate coordinate) {
		if (fetcher == null) {
			return;
		}

		final int count = getCount(winPos);

		final IntegerData i = fetcher.fetch(container);
		/*
		 * TODO remove if (i == null) { int n = 0; n++; }
		 */
		i.add(count);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AbstractIntegerStorage)) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		AbstractIntegerStorage storage = (AbstractIntegerStorage) obj;
		if (!getCoordinateController().getActive().equals(storage.getCoordinateController().getActive())) {
			return false;
		}
		for (int winPos = 0; winPos < getCoordinateController().getActiveWindowSize(); ++winPos) {
			if (getCount(winPos) != storage.getCount(winPos)) {
				return false;

			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return getCoordinateController().getActive().hashCode();
	}

	public abstract int getCount(final int winPos);

}
