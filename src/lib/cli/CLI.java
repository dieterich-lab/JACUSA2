package lib.cli;

import java.util.List;
import java.util.Map;

import lib.cli.options.AbstractACOption;
import lib.method.AbstractMethodFactory;
import lib.util.AbstractTool;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

public class CLI {

	private final Map<String, AbstractMethodFactory<?, ?>> methodFactories;
	private AbstractMethodFactory<?, ?> methodFactory;

	/**
	 * 
	 */
	public CLI(final Map<String, AbstractMethodFactory<?, ?>> methodFactories) {
		this.methodFactories = methodFactories;
	}

	/**
	 * 
	 * @param args
	 * @return
	 */
	public boolean processArgs(String[] args) {
		if (args.length == 0) {
			printUsage();
			System.exit(0);
		} else if (args.length > 0 && ! methodFactories.containsKey(args[0].toLowerCase())) {
			printUsage();
			System.exit(0);
		}
		methodFactory = methodFactories.get(args[0].toLowerCase());
		
		if (args.length == 1) {
			// init method factory (populate: parameters)
			methodFactory.initACOptions();
			
			final List<AbstractACOption> acOptions = methodFactory.getACOptions();
			final Options options = new Options();
			for (AbstractACOption acOption : acOptions) {
				if (! acOption.isHidden()) {
					options.addOption(acOption.getOption());
				}
			}
			
			methodFactory.printUsage();
			System.exit(0);
		}
		
		// copy arguments while ignoring the first array element
		String[] processedArgs = new String[args.length - 1];
		System.arraycopy(args, 1, processedArgs, 0, args.length - 1);

		// parse arguments
		final CommandLineParser parser = new DefaultParser();
		
		// container for options and parsed line
		List<AbstractACOption> acOptions;
		Options options; 
		CommandLine line = null;
		int conditions = 0;

		// try to find the correct number of conditions
		// all provided args need to be correctly parsed
		do {
			conditions++;

			methodFactory.initGeneralParameter(conditions);
			// init method factory (populate: parameters)
			methodFactory.initACOptions();

			acOptions = methodFactory.getACOptions();
			options = new Options();
	
			for (AbstractACOption acOption : acOptions) {
				options.addOption(acOption.getOption());
			}
		
			try {
				line = parser.parse(options, processedArgs);
			} catch (ParseException e) {
				e.printStackTrace();
				methodFactory.printUsage();
				return false;
			}
		} while (conditions < args.length && line.getArgs().length != conditions);
		
		// if not all args could be parsed STOP
		if (line.getArgs().length != conditions) {
			methodFactory.printUsage();
			return false;
		}

		try {
			// the remainder of line.getArgs should be files 
			methodFactory.parseArgs(line.getArgs());
			for (AbstractACOption acOption : acOptions) {
				acOption.process(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// methodFactory.printUsage();
			return false;
		}

		if (! methodFactory.checkState() ) {
			System.exit(0);
		}
		
		try {
			methodFactory.initCoordinateProvider();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*
		// check stranded and VCF chosen
		if (methodFactory.getParameters().getFormat().getC() == VCFcall.CHAR) {
			boolean error = false;
			for (final ConditionParameters<?> cp : methodFactory.getParameters().getConditionParameters()) {
				if (cp.getLibraryType() != LIBRARY_TYPE.UNSTRANDED) {
					error = true;
				}
			}
			
			if (error) {
				System.err.println("ERROR: Output format VCF does not support stranded Pileup Builder!");
				System.err.println("ERROR: Change output format or use unstranded Pileup Builder (-P U,U)!");
				System.exit(0);
			}
		}
		*/
		
		return true;
	}

	/**
	 * 
	 */
	public void printUsage() {
		final StringBuilder sb = new StringBuilder();
		
		for (final AbstractMethodFactory<?, ?> methodFactory : methodFactories.values()) {
			sb.append("  ");
			sb.append(methodFactory.getName());
			sb.append("\t\t");
			sb.append(methodFactory.getDescription());
			sb.append('\n');
		}

		sb.append("Version: " + AbstractTool.getLogger().getTool().getVersion() + "\n");
		System.err.print(sb.toString());
	}

	public AbstractMethodFactory<?, ?> getMethodFactory() {
		return methodFactory;
	}

}