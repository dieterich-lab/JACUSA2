package lib.data.count.basecall;

public abstract class BaseCallCountFactory<T extends BaseCallCount> {

	public abstract T create();

}
