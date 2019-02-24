package lib.data.storage;

import java.util.ArrayList;
import java.util.List;

import lib.data.validator.Validator;
import lib.util.position.Position;
import lib.util.position.PositionProvider;

public class PositionProcessor {
	
	private final List<Validator> validators;
	private final List<Storage> storages;
	
	public PositionProcessor() {
		this.validators = new ArrayList<>();
		this.storages	= new ArrayList<>();
	}
	
	public PositionProcessor(final List<Validator> validators, final List<Storage> storages) {
		this();
		this.validators.addAll(validators);
		this.storages.addAll(storages);
	}

	public PositionProcessor(final List<Storage> storages) {
		this();
		this.storages.addAll(storages);
	}
	
	public PositionProcessor(final Storage storage) {
		this();
		storages.add(storage);
	}
	
	public PositionProcessor(final List<Validator> validators, final Storage storage) {
		this();
		this.validators.addAll(validators);
		this.storages.add(storage);
	}
	
	public void addValidator(final Validator validator) {
		validators.add(validator);
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
