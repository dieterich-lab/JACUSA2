package jacusa.io.format.extensions;

public abstract class AbstractParallelDataToString implements ParallelDataToString {

    private final String id;
    private final String desc;

    public AbstractParallelDataToString(final String id, final String desc){
        this.id = id;
        this.desc = desc;
    };

    @Override
    public String getID() {
    	return id;
    }
    
    @Override
    public String getDesc() {
    	return desc;
    }
    
}
