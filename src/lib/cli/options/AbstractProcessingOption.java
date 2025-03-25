package lib.cli.options;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * Represents an CLI option that can added to parameterize a method.
 */
public abstract class AbstractProcessingOption {
	
	// will be shown to the user as an option: -opt
	private String opt; 
	private String longOpt; // will be used to format argument: -opt LONGOPT
	
	// psst, this should be a hidden flag...
	private boolean hide;
	
	public AbstractProcessingOption(
			final String opt,
			final String longOpt) {
		this.opt 		= opt;
		this.longOpt 	= longOpt;
	}
	
	/**
	 * Processes a parsed CommandLine.
	 * @param cmd the CommandLine line to be used for processing
	 * @throws Exception
	 */
	public abstract void process(CommandLine cmd) throws Exception;
	
	/**
	 * Returns an instance of Option that can be used with APACHE commons CLI 
	 * @param printExtendedHelp boolean, indicates if extended and details description should be provided
	 * @return an instance of Option that describes this ACOption
	 */
	public abstract Option getOption(boolean printExtendedHelp);
	
	public boolean isHidden() {
		return hide;
	}

	protected void hide() {
		hide = true;
	}
	
	public String getOpt() {
		return opt;
	}
	
	public String getLongOpt() {
		return longOpt;
	}

}
