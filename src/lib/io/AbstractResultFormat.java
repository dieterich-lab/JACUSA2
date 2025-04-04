package lib.io;

import lib.cli.parameter.GeneralParameter;

public abstract class AbstractResultFormat implements ResultFormat {

	// unique (per method) char that identifies a result format
	private final char c;
	
	// description that shown in help on command line 
	private final String desc;

	private final String methodName;
	
	private final GeneralParameter parameter;
	
	public AbstractResultFormat(
			final char c, final String desc, 
			final String methodName,
			final GeneralParameter parameter) {
		this.c 		= c;
		this.desc 	= desc;
		
		this.methodName = methodName;
		this.parameter 	= parameter;
	}

	@Override
	public final char getID() {
		return c;
	}

	@Override
	public final String getDesc() {
		return desc;
	}

	public String getMethodName() {
		return methodName;
	}
	
	public GeneralParameter getParameter() {
		return parameter;
	}
	
}
