package jacusa.io.format.modifyresult;

import lib.cli.parameter.GeneralParameter;
import lib.data.result.Result;

public interface ResultModifier {

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
    
    /**
     * 
     */
    void registerKeys(GeneralParameter parameter);
}
