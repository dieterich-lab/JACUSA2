package jacusa.io.format.modifyresult;

public abstract class AbstractResultModifier implements ResultModifier {

    private final String id;
    private final String desc;

    public AbstractResultModifier(final String id, final String desc){
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
