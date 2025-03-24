package lib.data.storage.processor;

public interface ExtendedRecordProcessor extends RecordProcessor {

	void preProcess();
	void postProcess();
	
}
