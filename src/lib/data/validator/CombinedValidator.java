package lib.data.validator;

import java.util.List;

import lib.util.position.Position;

public class CombinedValidator implements Validator {
		
		private final List<Validator> validators;
		
		public CombinedValidator(final List<Validator> validators) {
			this.validators = validators;
		}
		
		@Override
		public boolean isValid(Position pos) {
			for (final Validator validatos : validators) {
				if (! validatos.isValid(null)) {
					return false;
				}
			}
			return true;
		}

	}
