package lib.io.format.bed;

import lib.data.result.Result;

/**
 * TODO add documentation
 */
public interface DataAdder {
	
	void addHeader(StringBuilder sb, int condI, int replicateI);
	void addData(StringBuilder sb, int valueIndex, int condI, int replicateI, Result result);
	
}
