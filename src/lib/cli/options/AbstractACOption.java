package lib.cli.options;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public abstract class AbstractACOption {

	private String opt;
	private String longOpt;

	private boolean hide;
	
	public AbstractACOption(final String opt, final String longOpt) {
		this.opt 		= opt;
		this.longOpt 	= longOpt;
	}

	public abstract void process(CommandLine line) throws Exception;
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
