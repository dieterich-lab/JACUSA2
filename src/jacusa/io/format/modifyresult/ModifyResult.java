package jacusa.io.format.modifyresult;

import lib.data.result.Result;

public interface ModifyResult {

	/**
	 * 
	 * @return
	 */
    String getID();
   
    /**
     * 
     * @return
     */
    String getDesc();
    
    /**
     * 
     * @param result
     */
    void modify(Result result);
    
}
