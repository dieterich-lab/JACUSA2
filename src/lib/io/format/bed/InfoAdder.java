package lib.io.format.bed;

import lib.data.result.Result;

/**
 * TODO add documentation
 */
public interface InfoAdder {
	
	void addHeader(StringBuilder sb);
	void addData(StringBuilder sb, int valueIndex, Result result);
	
}
