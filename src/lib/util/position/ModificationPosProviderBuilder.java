package lib.util.position;

import java.util.ArrayList;
import java.util.List;

import lib.record.Record;
import lib.util.coordinate.CoordinateTranslator;

public class ModificationPosProviderBuilder implements lib.util.Builder<IntervalPosProvider>{

    // position of the modification
    private final ModPosition pos;
    // length of deletion
    private int length;

    private CoordinateTranslator translator;

    ModificationPosProviderBuilder(
            final int modPos, final Record record,
            final CoordinateTranslator translator) {

        final int readPos 					= modPos;
        final int refPos 					= record.getSAMRecord().getReferencePositionAtReadPosition(readPos);
        final int winPos					= translator.ref2winPos(refPos);

        pos 	= new ModPosition(refPos, readPos, winPos, record);

        this.translator = translator;
    }

    // make sure to run this last
    public ModificationPosProviderBuilder adjustWindowPos() {
        //TODO: length 1, weil ja mod immer nur 1 lang?
        length = PositionProvider.adjustWindowPos(pos, 1, translator);
        return this;
    }

    @Override
    public IntervalPosProvider build() {
        return new IntervalPosProvider(pos, length);
    }


}
