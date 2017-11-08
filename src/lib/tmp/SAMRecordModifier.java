package lib.tmp;

import java.util.Iterator;
import java.util.List;

import lib.util.Coordinate;
import lib.variant.Variant;

import lib.data.builder.SAMRecordWrapper;

public interface SAMRecordModifier extends Iterator<Variant[]> {

	List<List<SAMRecordWrapper>> build(final Coordinate activeWindowCoordinates, 
			final List<Iterator<SAMRecordWrapper>> iterators);

	void addInfo(final SAMRecordWrapper record);
	String getParameterInfo();
	
}
