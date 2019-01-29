package lib.io.codec;

import htsjdk.tribble.Feature;
import lib.data.result.Result;
import lib.util.coordinate.Coordinate;

public class ResultFeature implements Feature {

	private final Result result;
	private final Coordinate coordinate;
	
	public ResultFeature(final Result result) {
		this.result = result;
		coordinate = result.getParellelData().getCoordinate();
	}
	
	@Override
	public String getContig() {
		return coordinate.getContig();
	}

	@Override
	public int getStart() {
		return coordinate.getStart();
	}

	@Override
	public int getEnd() {
		return coordinate.getEnd();
	}
	
	public Result getResult() {
		return result;
	}
	
}
