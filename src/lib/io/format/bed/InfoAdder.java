package lib.io.format.bed;

import lib.data.result.Result;

/**
 * DOCUMENT
 */
public interface InfoAdder {
	
	void addHeader(StringBuilder sb);
	void addData(StringBuilder sb, int valueIndex, Result result);
	
}
