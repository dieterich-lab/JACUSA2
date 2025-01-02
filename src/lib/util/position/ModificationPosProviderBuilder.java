package lib.util.position;

import lib.record.Record;
import lib.util.coordinate.CoordinateTranslator;

public class ModificationPosProviderBuilder implements lib.util.Builder<IntervalPosProvider>{

    // position of the modification
    private final ModPosition pos;
    // length of modification
    private int length;

    private CoordinateTranslator translator;

    ModificationPosProviderBuilder(
            final int modPos, final Record record,
            final CoordinateTranslator translator) {

        final int readPos 					= modPos;
        final int refPos 					= record.getSAMRecord().getReferencePositionAtReadPosition(readPos);
        final int winPos					= translator.ref2winPos(refPos);

        //TODO: 0 abfangen -> wenn bei ref eine 0 rauskommt, ist es eine insertion und darf in modifications nicht weiter behandelt werden

        pos 	= new ModPosition(refPos, readPos, winPos, record);

        this.translator = translator;
    }

    // make sure to run this last
    public ModificationPosProviderBuilder adjustWindowPos() {
        length = PositionProvider.adjustWindowPos(pos, 1, translator);
        return this;
    }

    public ModPosition getModPos(){
        return pos;
    }

    @Override
    public IntervalPosProvider build() {
        return new IntervalPosProvider(pos, length);
    }


}
