package lib.util.position;

import lib.util.coordinate.CoordinateTranslator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lib.record.Record;

public class AllModificationsPositionProvider implements PositionProvider{

    private final CombinedPositionProvider positionProvider;

    public AllModificationsPositionProvider(
            final Record record, final CoordinateTranslator translator) {
        //TODO: für Modification umschreiben!!!
        final Map<Integer,List<Record.ModificationDetail>> modValues = record.getMMValues();
        final int modifications = modValues.size();
        final List<PositionProvider> positionProviders = new ArrayList<>(modifications);

        for (int index = 0; index < modifications; ++index) {
            positionProviders.add(
                    new ModificationPosProviderBuilder(index, record, translator)
                            .adjustWindowPos()
                            .build());
        }
        positionProvider = new CombinedPositionProvider(positionProviders);
    }

    @Override
    public boolean hasNext() {
        return positionProvider.hasNext();
    }

    @Override
    public Position next() {
        return positionProvider.next();
    }

}
