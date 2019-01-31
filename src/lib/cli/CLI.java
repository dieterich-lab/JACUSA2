package lib.cli;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lib.cli.options.AbstractACOption;
import lib.cli.options.HelpOption;
import lib.cli.options.ShowVersionOption;
import lib.util.AbstractMethod;
import lib.util.AbstractTool;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

public class CLI {

	private final List<AbstractMethod.AbstractFactory> methodFactories;
	private AbstractMethod method;
	private boolean printExtendedHelp;
	
	/**
	 * 
	 */
	public CLI(final List<AbstractMethod.AbstractFactory> methodFactories) {
		this.methodFactories = methodFactories;
		printExtendedHelp = false;
	}

	public List<AbstractMethod.AbstractFactory> getMethodFactories() {
		return Collections.unmodifiableList(methodFactories);
	}
	
	private void printVersion(final String[] args) {
		// check if version should be printed
		final ShowVersionOption showVersion = new ShowVersionOption();
		final HelpOption showHelp = new HelpOption(this);

		final Options options = new Options();
		options.addOption(showVersion.getOption(false));
		options.addOption(showHelp.getOption(false));

		// parse arguments
		final CommandLineParser parser = new DefaultParser();
		try {
			final CommandLine line = parser.parse(options, args);
			if (line.hasOption(showVersion.getOpt())) {
				showVersion.process(line);
				System.exit(0);
			}
			if (line.hasOption(showHelp.getOpt())) {
				showHelp.process(line);
				printToolUsage();
				System.exit(0);
			}
		} catch (ParseException e) {
			// ignore
		} catch (Exception e) {
			// ignore
		}
	}

	private void conditionalExtendedHelp(final String[] args) {
		// check if extended help should be printed
		final HelpOption showHelp = new HelpOption(this);
		final Options options = new Options();
		options.addOption(showHelp.getOption(false));
		// parse arguments
		final CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(options, args);
			if (line.hasOption(showHelp.getOpt())) {
				showHelp.process(line);
				processMethodFactoryACOptions(printExtendedHelp, false,
						new ArrayList<AbstractACOption>(), new Options());
				printMethodFactoryUsage();
				System.exit(0);
			}
		} catch (ParseException e) {
			// ignore
		} catch (Exception e) {
			// ignore
		}
	}
	
	private boolean contains(final String name) {
		for (final AbstractMethod.AbstractFactory methodFactory : methodFactories) {
			if (methodFactory.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	private AbstractMethod.AbstractFactory getMethodFactory(final String name) {
		for (final AbstractMethod.AbstractFactory methodFactory : methodFactories) {
			if (methodFactory.getName().equals(name)) {
				return methodFactory;
			}
		}
		return null;
	}
	
	// try to find the correct number of conditions
	// all provided args need to be correctly parsed
	private int getConditions(
			AbstractMethod.AbstractFactory methodFactory,
			final String[] args,
			final String[] processedArgs) {
		// parse arguments
		final CommandLineParser parser = new DefaultParser();
		
		// container for options and parsed line
		List<AbstractACOption> acOptions = new ArrayList<AbstractACOption>();
		Options options = new Options(); 
		CommandLine line = null;
		int conditions = 0;

		// try to find the correct number of conditions
		// all provided args need to be correctly parsed
		do {
			conditions++;
			options = new Options();
			acOptions.clear();

			final AbstractMethod.AbstractFactory tmpMethodFactory = 
					methodFactory.createFactory(conditions);
			if (tmpMethodFactory == null) {
				continue;
			}
			method = tmpMethodFactory.createMethod();
			processMethodFactoryACOptions(printExtendedHelp, true, acOptions, options);

			final String s = new String();
			try {
				line = parser.parse(options, processedArgs);
			} catch (ParseException e) {
				s.length();
				// e.printStackTrace();
				// ignore
			}
			s.length();
		} while (conditions < args.length && (line == null || line.getArgs().length != conditions));
		
		// if not all args could be parsed STOP
		if (line == null || line.getArgs().length != conditions) {
			return -1;
		}

		return conditions;
	}
	
	/**
	 * 
	 * @param args
	 * @return
	 */
	public boolean processArgs(String[] args) {
		if (args.length == 0) {
			printToolUsage();
			System.exit(0);
		} else if (args.length > 0 && ! contains(args[0].toLowerCase())) {
			printVersion(args);
			AbstractTool.getLogger().addError("Unknown method: " + args[0]);
			System.exit(1);
		}
		final AbstractMethod.AbstractFactory methodFactory = getMethodFactory(args[0].toLowerCase());
		method = methodFactory.createMethod();
		if (args.length == 1) {
			processMethodFactoryACOptions(printExtendedHelp, false, 
					new ArrayList<AbstractACOption>(), new Options());
			method.printUsage(printExtendedHelp);
			System.exit(0);
		}
		conditionalExtendedHelp(args);
				
		// copy arguments while ignoring the first array element
		String[] processedArgs = new String[args.length - 1];
		System.arraycopy(args, 1, processedArgs, 0, args.length - 1);

		// parse arguments
		final CommandLineParser parser = new DefaultParser();
		
		// container for options and parsed line
		List<AbstractACOption> acOptions = new ArrayList<AbstractACOption>();
		Options options = new Options(); 
		CommandLine line = null;

		final int conditions = getConditions(methodFactory, args, processedArgs);
		if (conditions < 1) {
			method.printUsage(false);
			System.exit(1);
		}
		processMethodFactoryACOptions(printExtendedHelp, true, acOptions, options);

		try {
			line = parser.parse(options, processedArgs);
		} catch (ParseException e) {
			e.printStackTrace();
			method.printUsage(printExtendedHelp);
			return false;
		}
		
		try {
			// the remainder of line.getArgs should be files 
			method.parseArgs(line.getArgs());
			for (AbstractACOption acOption : acOptions) {
				if (acOption.getOpt() != null && line.hasOption(acOption.getOpt()) ||
						acOption.getLongOpt() != null && line.hasOption(acOption.getLongOpt())) {
					acOption.process(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// methodFactory.printUsage();
			return false;
		}

		if (! method.checkState() ) {
			System.exit(0);
		}

		try {
			method.initCoordinateProvider();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * 
	 */
	public void printToolUsage() {
		final StringBuilder sb = new StringBuilder();
		
		final String jar = "JACUSA2";
		
		sb.append("usage: ");
		sb.append(jar);
		sb.append(" <METHOD> <METHOD-OPTIONs> <BAMs>");
		sb.append('\n');
		sb.append("  ");
		sb.append("METHOD");
		sb.append('\t');
		sb.append("DESCRIPTION");
		sb.append('\n');
		
		for (final AbstractMethod.AbstractFactory methodFactory : methodFactories) {
			sb.append("  ");
			sb.append(methodFactory.getName());
			sb.append('\t');
			sb.append(methodFactory.getDescription());
			sb.append('\n');
		}

		sb.append("Version: " + AbstractTool.getLogger().getTool().getVersion() + "\n");
		System.out.print(sb.toString());
	}

	public void printMethodFactoryUsage() {
		method.printUsage(printExtendedHelp);
	}

	private void processMethodFactoryACOptions(final boolean printExtendedHelp, final boolean includeHidden,
			final List<AbstractACOption> acOptions, final Options options) {
		// init method factory (populate: parameters)
		method.initACOptions();
		
		acOptions.addAll(method.getACOptions());
		for (AbstractACOption acOption : acOptions) {
			if (includeHidden || ! acOption.isHidden()) {
				options.addOption(acOption.getOption(printExtendedHelp));
			}
		}
	}
	
	public void setPrintExtendedHelp() {
		printExtendedHelp = true;
	}
	
	public boolean printExtendedHelp() {
		return printExtendedHelp;
	}
	
	public AbstractMethod getMethodFactory() {
		return method;
	}

	public final Map<String, AbstractMethod.AbstractFactory> getName2methodFactory() {
		return getMethodFactories().stream()
				.collect(Collectors.toMap(
								AbstractMethod.AbstractFactory::getName,
								Function.identity()) );
	}
	
}
