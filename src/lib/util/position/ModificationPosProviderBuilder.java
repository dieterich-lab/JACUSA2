package lib.util.position;

import java.util.ArrayList;
import java.util.List;

import lib.record.Record;
import lib.util.coordinate.CoordinateTranslator;

public class ModificationPosProviderBuilder implements lib.util.Builder<IntervalPosProvider>{

    //TODO: für Modification umschreiben!!!

    // position of the deletion
    private final ModificationPosition pos;
    // length of deletion
    private int length;

    private CoordinateTranslator translator;

    ModificationPosProviderBuilder(
            final int delI, final Record record,
            final CoordinateTranslator translator) {

        // extract corresponding cigar element
        final int cigarElementI 	= record.getDeletion().get(delI);
        final CigarDetail cigarElement = record
                .getCigarDetail().get(cigarElementI);

        // prepare to create Position
        final AlignedPosition alignedPos 	= cigarElement.getPosition();
        final int refPos 					= alignedPos.getRefPos();
        final int readPos 					= alignedPos.getReadPos();
        final int winPos					= translator.ref2winPos(refPos);

        pos 	= new DeletedPosition(refPos, readPos, winPos, record);
        length 	= cigarElement.getCigarElement().getLength();

        this.translator = translator;
    }

    // make sure to run this last
    public DeletionPosProviderBuilder adjustWindowPos() {
        length = PositionProvider.adjustWindowPos(pos, length, translator);
        return this;
    }

    @Override
    public IntervalPosProvider build() {
        return new IntervalPosProvider(pos, length);
    }


}
