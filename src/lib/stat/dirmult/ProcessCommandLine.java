package lib.stat.dirmult;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import lib.cli.options.AbstractProcessingOption;
import lib.util.CLIUtil;

public class ProcessCommandLine {

	public static final String REMOVE = "___REMOVE___";
	
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
	
	public Options getNestedOptions() {
		final Options newOptions = new Options();
		for (final Option option : options.getOptions()) {
			
			
			// add magic string to identify main option
			// create main option
			Option newOption = Option.builder(option.getLongOpt())
					.desc(descriptions)
					.build();
			
			newOptions.addOption(newOption);
		}
		
		return newOptions;
	}
	
	// final String[] args = line.split(Character.toString(InputOutput.WITHIN_FIELD_SEP));
	public void process(final String[] args) {
		if (options.getOptions().isEmpty() || args == null || args.length == 0 || parser == null) {
			return;
		}
		
		try {
			final CommandLine cmd  = parser.parse(options, args);
			for (final AbstractProcessingOption processingOption : processingOptions) {
				if (cmd.hasOption(processingOption.getLongOpt())) {
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

	public static String[] addDash(final String[] args) {
		final String[] newArgs = new String[args.length];
		for (int i = 0; i < args.length; ++i) {
			newArgs[i] = "--" + args[i];
		}
		return newArgs;
	}
	
	public class Builder {

		private final List<AbstractProcessingOption> processingOptions = new ArrayList<AbstractProcessingOption>();
		private final CommandLineParser parser;
		
		public Builder(final CommandLineParser parser) {
			this.parser = parser;
		}
		
		public Builder addProcessingOption(final AbstractProcessingOption processingOption) {
			processingOptions.add(processingOption);
			
			return this;
		}
		
		public ProcessCommandLine build() {
			return new ProcessCommandLine(parser, processingOptions);
		}
		
	}
	
}
