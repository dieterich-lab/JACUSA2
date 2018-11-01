package lib.data.count.basecallquality;

public abstract class BaseCallQualityCountFactory<T extends BaseCallQualityCount> {
	
	public abstract T create();
	
}
