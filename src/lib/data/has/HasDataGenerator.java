package lib.data.has;

import lib.data.AbstractData;
import lib.data.generator.DataGenerator;

public interface HasDataGenerator<T extends AbstractData> {

	DataGenerator<T> getDataGenerator();
	
}
