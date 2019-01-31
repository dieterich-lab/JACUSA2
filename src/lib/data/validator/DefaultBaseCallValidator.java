package lib.data.validator;

import htsjdk.samtools.util.SequenceUtil;
import lib.util.position.Position;

public class DefaultBaseCallValidator implements Validator {

	@Override
	public boolean isValid(Position pos) {
		return SequenceUtil.isValidBase(pos.getReadBaseCall().getByte());
	}

}
