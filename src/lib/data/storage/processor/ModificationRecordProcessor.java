package lib.data.storage.processor;

import lib.data.count.ModificationCount;
import lib.data.storage.Storage;
import lib.record.Record;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.AllDeletionsPositionProvider;
import lib.util.position.Position;
import lib.util.position.PositionProvider;

public class ModificationRecordProcessor implements GeneralRecordProcessor{

    private final CoordinateTranslator translator;

    private final Storage modStorage;

    public ModificationRecordProcessor(
            final CoordinateTranslator translator,
            final Storage modStorage) {

        this.translator	= translator;
        this.modStorage	= modStorage;
    }

    @Override
    public void preProcess() {
        // nothing to be done
    }

    @Override
    public void process(final Record record) {
        // store modifications
        //TODO: brauche ich diesen PositionProvider überhaupt?
        // Weil die Positionen stehen doch hinten im String in dem Teil, den ich grade noch gar nicht bearbeiten soll oder?
        // Also das sind doch die Positionen die in dem Provider behandelt werden oder?
        // Aber wenn ich die Positionen nicht machen soll, was soll dann hier passieren?
        // Weil der Provider wird ja auf jeden Fall aufgerufen, oder ist das nicht notwendig?
        final PositionProvider modPosProvider =
                new AllModificationsPositionProvider(record, translator);
        while (modPosProvider.hasNext()) {
            final Position pos = modPosProvider.next();
            modStorage.increment(pos);
        }
    }

    @Override
    public void postProcess() {
        // nothing to be done
    }

}
