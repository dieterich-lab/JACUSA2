package test.lib.data.count.basecallquality;

import java.util.Map;

import lib.data.count.basecallquality.BaseCallQualityCount;
import lib.data.count.basecallquality.MapBaseCallQualityCount;
import lib.util.Base;

public class MapBaseCallQualitCountTest extends BaseCallQualityCountTest {

	@Override
	protected BaseCallQualityCount createBaseCallQualityCount(final Map<Base, Map<Byte, Integer>> base2qual2count) {
		return new MapBaseCallQualityCount(base2qual2count);
	}

}
