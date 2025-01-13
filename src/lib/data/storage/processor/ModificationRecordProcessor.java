package lib.data.storage.processor;

import lib.data.storage.Storage;
import lib.record.Record;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.position.AllModificationsPositionProvider;
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
        //store modifications
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
