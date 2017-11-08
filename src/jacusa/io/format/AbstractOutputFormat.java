package jacusa.io.format;

import java.util.List;

import lib.cli.parameters.JACUSAConditionParameters;
import lib.data.AbstractData;
import lib.data.Result;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class AbstractOutputFormat<T extends AbstractData> {

	private char c;
	private String desc;
	
	public AbstractOutputFormat(final char c, final String desc) {
		this.c = c;
		this.desc = desc;
	}

	public final char getC() {
		return c;
	}

	public final String getDesc() {
		return desc;
	}

	// Header is empty by default
	// override to change
	public String getHeader(final List<JACUSAConditionParameters<T>> conditions) {
		return null;
	}

	public abstract String convert2String(final Result<T> result);

}
