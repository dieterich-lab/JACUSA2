package lib.data.storage.processor;

public interface RecordExtendedPrePostProcessor extends RecordExtendedProcessor {

	void preProcess();
	void postProcess();
	
}
