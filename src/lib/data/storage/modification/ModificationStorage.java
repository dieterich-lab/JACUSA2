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
import java.util.Set;

public class ModificationStorage extends AbstractStorage {


    private final Fetcher<PileupCount> pcFetcher;
    //TODO: hier ne map? oder was genau soll dieser count egtl sein? ist der von vielen Reads an der position?
    // Und was soll in dieser Klasse passieren, in comparison zu den anderen Klassen, insb Record wo ja egtl MM bearbeitet wird?
    // Sollen dann hier die einzelnen Mod Strings aus MM verarbeitet werden oder was anderes?
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
        //TODO: was soll hier überhaupt rein für MM?
        if (winPos2modc[winPos] == null) {
            return;
        }

        //final Set<Base> alleles 		= winPos2bcqc[winPos].getAlleles();
        final PileupCount pileupCount 	= pcFetcher.fetch(dataContainer);
        //pileupCount.getBaseCallQualityCount().add(alleles, winPos2bcqc[winPos]);
        if (coordinate.getStrand() == CoordinateUtil.STRAND.REVERSE) {
            //pileupCount.getBaseCallQualityCount().invert();
        }
    }

    @Override
    public void increment(Position pos) {
        //TODO: was soll hier überhaupt rein für MM?
        final int winPos 	= pos.getWindowPosition();
        final Base base 	= pos.getReadBaseCall();
       //final byte baseQual	= pos.getReadBaseCallQuality();

        if (winPos2modc[winPos] == null) {
            winPos2modc[winPos] = ModificationCount.create();
        }
        //final BaseCallQualityCount base2qual2count = winPos2bcqc[winPos];
        //base2qual2count.increment(base, baseQual);
    }

    @Override
    public void clear() {
        Arrays.fill(winPos2modc, null);
    }

}
