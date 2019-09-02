package lib.cli;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
			CommandLine line = parser.parse(options, args, false);
			if (line.hasOption(showHelp.getOpt())) {
				showHelp.process(line);
				method.initACOptions();
				printMethodFactoryUsage();
				System.exit(0);
			}
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
	// TODO
	private void generateLatex() {
		final Map<String, Map<String, List<AbstractACOption>>> opt2method2acOption = new HashMap<String, Map<String,List<AbstractACOption>>>();
		
		final List<String> methods = new ArrayList<String>();
		
		// collect into Map...
		for (final AbstractMethod.AbstractFactory methodFactory : methodFactories) {
			final String name = methodFactory.getName();
			methods.add(name);
			// TODO change number of conditions?
			final AbstractMethod method = methodFactory.createMethod();
			method.initACOptions();
			
			final List<AbstractACOption> acOptions = method.getACOptions();
			for (final AbstractACOption acOption : acOptions) {
				final String opt = acOption.getOpt();
				
				if (! opt2method2acOption.containsKey(opt)) {
					opt2method2acOption.put(opt, new HashMap<String, List<AbstractACOption>>());
				}
				final Map<String, List<AbstractACOption>> method2acOption = opt2method2acOption.get(opt);
				if (! method2acOption.containsKey(name)) {
					method2acOption.put(name, new ArrayList<AbstractACOption>());
				}
				final List<AbstractACOption> tmpAcOptions = method2acOption.get(name);
				tmpAcOptions.add(acOption);
			}
		}
		
		// output
		for (final String opt : opt2method2acOption.keySet()) {
			final String fileName = opt + ".tex";
			final File file = new File(fileName);
		
			try {
				final BufferedWriter bw = new BufferedWriter(new FileWriter(file));
				final StringBuilder sb = new StringBuilder();
			
				final Map<String, List<AbstractACOption>> method2acOption = opt2method2acOption.get(opt);
				for (final String method : methods) {
					if (! method2acOption.containsKey(method)) {
						continue;
					}
					for (final AbstractACOption acOption : method2acOption.get(method)) {
						sb
						.append('-').append(escape(acOption.getOpt()))
						.append(" & ")
						.append(escape(acOption.getOption(false).getDescription()))
						.append(" & ")
						.append(escape(method))
						.append(" \\\\\n");
					}
				}
				bw.write("\\begin{tabular}{p{.175\\textwidth}p{.7\\textwidth}c}\n");
				bw.write(sb.toString());
				bw.write("\\end{tabular}\n");
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String escape(final String s) {
		return s.replaceAll("(_|\\$|\\^|#)", "\\\\$1");
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
			if (args[0].equals("generate-latex")) {
				generateLatex();
				System.exit(0);				
			}
			
			printVersion(args);
			AbstractTool.getLogger().addError("Unknown method: " + args[0]);
			System.exit(1);
		}
		final AbstractMethod.AbstractFactory methodFactory = getMethodFactory(args[0].toLowerCase());
		method = methodFactory.createMethod();
		if (args.length == 1) {
			method.initACOptions();
			method.printUsage(printExtendedHelp);
			System.exit(0);
		}
	
		// copy arguments while ignoring the first array element
		String[] processedArgs = new String[args.length - 1];
		System.arraycopy(args, 1, processedArgs, 0, args.length - 1);
		
		conditionalExtendedHelp(processedArgs);
		
		// parse arguments
		final CommandLineParser parser = new DefaultParser();
		
		// container for options and parsed line
		List<AbstractACOption> acOptions = new ArrayList<AbstractACOption>();
		Options options = new Options(); 
		CommandLine line = null;

		final int conditions = methodFactory.getConditions();
		final AbstractMethod.AbstractFactory tmpMethodFactory = 
				methodFactory.createFactory(conditions);
		if (tmpMethodFactory == null) {
			throw new IllegalArgumentException("Illegal number of conditions");
		}
		method = tmpMethodFactory.createMethod();
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

		sb.append("Version:\t" + AbstractTool.getLogger().getTool().getVersion() + "\n");
		sb.append("Libraries:\t " + AbstractTool.getLogger().getTool().getLibraries() + "\n");
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
