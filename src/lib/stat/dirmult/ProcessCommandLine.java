package lib.stat.dirmult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import lib.cli.options.AbstractProcessingOption;

/* TODO
 * if (resultFormat.getID() == VCFcallFormat.CHAR) {
					throw new IllegalStateException("VCF output format does not support showAlpha");
				}
 */

public class ProcessCommandLine {

	private final CommandLineParser parser;
	private final List<AbstractProcessingOption> processingOptions;
	private final Options options = new Options();
	
	public ProcessCommandLine() {
		this(null, new ArrayList<AbstractProcessingOption>());
	}
	
	public ProcessCommandLine(final CommandLineParser parser, final List<AbstractProcessingOption> processingOptions) {
		this.parser = parser;
		this.processingOptions = processingOptions;
		
		for (final AbstractProcessingOption processingOption : this.processingOptions) {
			options.addOption(processingOption.getOption(false));
		}
	}
	
	public List<AbstractProcessingOption> getProcessingOptions() {
		return Collections.unmodifiableList(processingOptions);
	}
	
	// dont't change - enforce
	public Options getOptions() {
		return options;
	}
	
	// final String[] args = line.split(Character.toString(InputOutput.WITHIN_FIELD_SEP));
	public void process(final String[] args) {
		if (options.getOptions().isEmpty() || args == null || args.length == 0 || parser == null) {
			return;
		}
		
		try {
			final CommandLine cmd  = parser.parse(options, args);
			for (final AbstractProcessingOption processingOption : processingOptions) {
				if (cmd.getOptionValue(processingOption.getLongOpt()) != null) {
					processingOption.process(cmd);
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

}
