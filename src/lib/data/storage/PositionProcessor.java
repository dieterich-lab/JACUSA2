package lib.data.storage;

import java.util.ArrayList;
import java.util.List;

import lib.data.stroage.Storage;
import lib.data.validator.Validator;
import lib.util.position.Position;
import lib.util.position.PositionProvider;

public class PositionProcessor {
	
	private final List<Validator> validators;
	private final List<Storage> storages;
	
	public PositionProcessor(final List<Validator> validators, final List<Storage> storages) {
		this.validators = validators;
		this.storages	= storages;
	}

	public PositionProcessor(final List<Storage> storages) {
		this(new ArrayList<>(), storages);
	}
	
	public PositionProcessor(final Storage storage) {
		this(new ArrayList<>(), new ArrayList<>());
		storages.add(storage);
	}
	
	public PositionProcessor(final List<Validator> validators, final Storage storage) {
		this(new ArrayList<>(), new ArrayList<>());
		this.storages.add(storage);
	}
	
	public void process(final PositionProvider positionProvider) {
		while (positionProvider.hasNext()) {
			final Position position = positionProvider.next();
			if (checkValidators(position)) {
				processStorages(position);
			}
		}
	}
	
	public boolean checkValidators(final Position position) {
		for (final Validator validator : validators) {
			if (! validator.isValid(position)) {
				return false;
			}
		}
		return true;
	}
	
	public void processStorages(final Position position) {
		for (final Storage storage : storages) {
			storage.increment(position);
		}
	}
	
}
