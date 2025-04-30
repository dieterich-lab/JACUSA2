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
import lib.io.InputOutput;

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
	
	public String renderNestedOptions() {
		final StringBuilder sb = new StringBuilder();
		
		int max = 3;
		for (final Option o : options.getOptions()) {
			max = Math.max(max, o.getLongOpt().length());
		}
		
		for (final Option option : options.getOptions()) {
			sb.append("| :" + option.getLongOpt());
			final int size = max - option.getLongOpt().length() + 1;
			if (size > 0) {
				sb.append(" ".repeat(size));
			}
			String s = option.getDescription();
			if (option.isRequired()) {
				s += " (Required)";
			}
			InputOutput.formatStr(sb, s, "|   " + " ".repeat(max), 80);
		}
		
		return sb.toString();
	}
	
	public void processNested(final String[] args) {
		process(addDash(args));
	}
	
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
