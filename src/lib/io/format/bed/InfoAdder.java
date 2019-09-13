package lib.io.format.bed;

import lib.data.result.Result;

/**
 * TODO
 */
public interface InfoAdder {
	
	void addHeader(StringBuilder sb);
	void addData(StringBuilder sb, int valueIndex, Result result);
	
}
