package lib.cli;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import lib.cli.options.AbstractProcessingOption;
import lib.cli.options.HelpOption;
import lib.cli.options.ShowVersionOption;
import lib.util.AbstractMethod;
import lib.util.AbstractTool;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

public class CLI {

	private final List<AbstractMethod.AbstractFactory> methodFactories;
	private AbstractMethod method;
	private boolean printExtendedHelp;
	
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
			final CommandLine cmdLine = parser.parse(options, args);
			if (cmdLine.hasOption(showVersion.getOpt())) {
				showVersion.process(cmdLine);
				System.exit(0);
			}
			if (cmdLine.hasOption(showHelp.getOpt())) {
				showHelp.process(cmdLine);
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
			CommandLine cmdLine = parser.parse(options, args, false);
			if (cmdLine.hasOption(showHelp.getOpt())) {
				showHelp.process(cmdLine);
				method.initOptions();
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
	private void generateLatex() {
		final Map<String, Map<String, List<AbstractProcessingOption>>> opt2method2acOption = 
				new HashMap<>();
		
		final Set<String> methodNames = new HashSet<>();
		
		// collect into Map...
		for (final AbstractMethod.AbstractFactory methodFactory : methodFactories) {
			for (final int conditions : new int[] {1, 2}) {
				final AbstractMethod.AbstractFactory tmpMethodFactory = methodFactory.createFactory(conditions);
				if (tmpMethodFactory == null) {
					continue;
				}

				
				final AbstractMethod tmpMethod = tmpMethodFactory.createMethod();
				final String methodName = tmpMethod.getName();
				methodNames.add(methodName);
				tmpMethod.initOptions();

				final List<AbstractProcessingOption> options = tmpMethod.getOptions();
				for (final AbstractProcessingOption Option : options) {
					final String opt = Option.getOpt();
					if (! opt2method2acOption.containsKey(opt)) {
						opt2method2acOption.put(opt, new HashMap<String, List<AbstractProcessingOption>>());
					}
					final Map<String, List<AbstractProcessingOption>> method2acOption = opt2method2acOption.get(opt);
					if (! method2acOption.containsKey(methodName)) {
						method2acOption.put(methodName, new ArrayList<AbstractProcessingOption>());
					}
					final List<AbstractProcessingOption> tmpAcOptions = method2acOption.get(methodName);
					tmpAcOptions.add(Option);
				}
			}
		}
		
		// output
		for (final String opt : opt2method2acOption.keySet()) {
			final String fileName = opt + ".tex";
			final File file = new File(fileName);
		
			try {
				final BufferedWriter bw = new BufferedWriter(new FileWriter(file));
				final StringBuilder sb = new StringBuilder();
			
				final Map<String, List<AbstractProcessingOption>> method2acOption = opt2method2acOption.get(opt);
				final List<String> optionStrs = new ArrayList<>();
				final List<String> descStrs = new ArrayList<>();
				final List<String> methodStrs = new ArrayList<>();
				for (final String methodName : methodNames) {
					if (! method2acOption.containsKey(methodName)) {
						continue;
					}
					for (final AbstractProcessingOption acOption : method2acOption.get(methodName)) {
						final Option option = acOption.getOption(false);
						if (option.getArgName() != null) {
							optionStrs.add('-'+ escape(option.getOpt() + ' ' + option.getArgName()));
						} else {
							optionStrs.add('-'+ escape(option.getOpt()));
						}
						descStrs.add(escape(acOption.getOption(false).getDescription()));
						methodStrs.add(escape(methodName));
					}
				}
				deduplicate(optionStrs);
				deduplicate(descStrs);
				for (int i = 0; i < methodStrs.size(); ++i) {
					sb
					.append(optionStrs.get(i))
					.append(" & ")
					.append(descStrs.get(i))
					.append(" & ")
					.append(methodStrs.get(i))
					.append(" \\\\\n");
				}
				bw.write("{\\small\n");
				bw.write("\\begin{tabular}{@{}p{.25\\textwidth}p{.7\\textwidth}l@{}}\n");
				bw.write(sb.toString());
				bw.write("\\end{tabular}\\\\\n");
				bw.write("}\n");
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	private void deduplicate(final List<String> list) {
		int firstI = -1;
		int length = 0;
		String tmp = "";
		for (int i = 0; i < list.size(); ++i) {
			if (firstI == -1) {
				firstI = i;
				length = 1;
				tmp = list.get(i);
			} else if (tmp.equals(list.get(i))){
				++length;
			} else {
				if (length > 1) {
					list.set(firstI, "\\multirow{" + length + "}{=}{" + tmp + "}");
					for (int j = firstI + 1; j < length - 1; ++j) {
						list.set(j, "");
					}
				}
				firstI = i;
				length = 1;
				tmp = list.get(i);
			}
		}
		if (length > 1) {
			list.set(firstI, "\\multirow{" + length + "}{=}{" + tmp + "}");
			for (int j = firstI + 1; j < length; ++j) {
				list.set(j, "");
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
			method.initOptions();
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
		List<AbstractProcessingOption> acOptions = new ArrayList<AbstractProcessingOption>();
		Options options = new Options(); 
		CommandLine line = null;

		final int conditions = methodFactory.getConditions();
		final AbstractMethod.AbstractFactory tmpMethodFactory = 
				methodFactory.createFactory(conditions);
		if (tmpMethodFactory == null) {
			throw new IllegalArgumentException("Illegal number of conditions");
		}
		method = tmpMethodFactory.createMethod();
		processMethodFactoryOptions(printExtendedHelp, true, acOptions, options);
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
			for (AbstractProcessingOption acOption : acOptions) {
				if (acOption.getOpt() != null && line.hasOption(acOption.getOpt()) ||
						acOption.getLongOpt() != null && line.hasOption(acOption.getLongOpt())) {
					acOption.process(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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

	private void processMethodFactoryOptions(final boolean printExtendedHelp, final boolean includeHidden,
			final List<AbstractProcessingOption> acOptions, final Options options) {
		// init method factory (populate: parameters)
		method.initOptions();
		
		acOptions.addAll(method.getOptions());
		for (AbstractProcessingOption acOption : acOptions) {
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
