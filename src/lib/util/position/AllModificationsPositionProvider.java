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

        final Map<Integer,List<Record.ModificationDetail>> modValues = record.getMMValues();
        final List<PositionProvider> positionProviders = new ArrayList<>(modValues.size());

        for (Map.Entry<Integer,List<Record.ModificationDetail>> modEntry : modValues.entrySet()) {
            ModificationPosProviderBuilder modPosProvBuild = new ModificationPosProviderBuilder(modEntry.getKey(), record, translator);
            //if position on reference is 0, that signifies an inserted base, not a modification -> not in modification count, so not in positions that are collected here
            if(modPosProvBuild.getModPos().getReferencePosition()!=0){
                positionProviders.add(
                        modPosProvBuild
                                .adjustWindowPos()
                                .build());
            }
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
