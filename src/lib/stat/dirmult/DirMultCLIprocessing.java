package lib.stat.dirmult;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

/**
 * TODO
 */
public interface DirMultCLIprocessing {

	Options getOptions();
	void processCLI(final CommandLine cmd);
	
}
