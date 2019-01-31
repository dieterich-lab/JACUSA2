package lib.cli.options.filter.has;

public interface HasHomopolymerMethod {

	HomopolymerMethod getHomopolymerMethod();
	void setHomopolymerMethod(HomopolymerMethod method);

	enum HomopolymerMethod {
		REFERENCE, READ;
	}
	
}
