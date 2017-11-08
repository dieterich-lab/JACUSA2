package lib.tmp;

import java.util.ArrayList;
import java.util.List;

import lib.data.builder.SAMRecordWrapper;

public class OverlappingRecordWrapperContainer {

	private List<SAMRecordWrapper> left;
	private List<SAMRecordWrapper> right;
	
	public OverlappingRecordWrapperContainer() {
		final int n = 100;
		left = new ArrayList<SAMRecordWrapper>(n);
		right = new ArrayList<SAMRecordWrapper>(n);
	}
	
	public List<SAMRecordWrapper> getLeft() {
		return left;
	}
	
	public List<SAMRecordWrapper> getRight() {
		return right;
	}

	public void clear() {
		left.clear();
		right.clear();
	}

}
