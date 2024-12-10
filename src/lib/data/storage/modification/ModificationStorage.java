package lib.data.storage.modification;

import lib.data.DataContainer;
import lib.data.count.ModificationCount;
import lib.data.count.PileupCount;
import lib.data.fetcher.Fetcher;
import lib.data.storage.AbstractStorage;
import lib.data.storage.container.SharedStorage;
import lib.util.Base;
import lib.util.coordinate.Coordinate;
import lib.util.coordinate.CoordinateUtil;
import lib.util.position.Position;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ModificationStorage extends AbstractStorage {


    private final Fetcher<PileupCount> pcFetcher;
    private ModificationCount[] winPos2modc;

    public ModificationStorage(
            final SharedStorage sharedStorage,
            final Fetcher<PileupCount> bcqcFetcher) {
        super(sharedStorage);

        this.pcFetcher 	= bcqcFetcher;

        final int n  	= getCoordinateController().getActiveWindowSize();
        winPos2modc 	= new ModificationCount[n];
    }

    @Override
    public void populate(DataContainer dataContainer, int winPos, Coordinate coordinate) {
        if (winPos2modc[winPos] == null) {
            return;
        }

        final PileupCount pileupCount 	= pcFetcher.fetch(dataContainer);
        //add deep copy of ModificationCount objects to pileup
        ModificationCount modCountCopy = winPos2modc[winPos].copy();
        pileupCount.getModCount().setModCount(modCountCopy.getModCount());
    }

    @Override
    public void increment(Position pos) {
        final int winPos 	= pos.getWindowPosition();
        final Base base 	= pos.getReadBaseCall();
        final List<String> modBases	= pos.getModifiedBases();

        if (winPos2modc[winPos] == null) {
            winPos2modc[winPos] = ModificationCount.create();
        }
        final ModificationCount m = winPos2modc[winPos];

        for(String modBase : modBases){
            m.setModCount(base, modBase);
        }
    }

    @Override
    public void clear() {
        Arrays.fill(winPos2modc, null);
    }

}
