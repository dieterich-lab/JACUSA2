package jacusa.cli.parameters;

import jacusa.JACUSA;
import jacusa.cli.options.AbstractACOption;
import jacusa.cli.options.DebugModusOption;
import jacusa.data.AbstractData;
import jacusa.io.format.VCFcall;
import jacusa.method.AbstractMethodFactory;
import jacusa.pileup.builder.hasLibraryType.LIBRARY_TYPE;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

public class CLI {

	// singleton
	private static CLI CLI;

	private Map<String, AbstractMethodFactory<?>> methodFactories;
	private AbstractMethodFactory<?> methodFactory;

	/**
	 * 
	 */
	private CLI() {
		this.methodFactories = new HashMap<String, AbstractMethodFactory<?>>();
	}

	public static CLI getSingleton() {
		if (CLI == null) {
			CLI = new CLI();
		}

		return CLI;
	}
	
	/**
	 * 
	 * @param methodFactories
	 */
	public void setMethodFactories(Map<String, AbstractMethodFactory<? extends AbstractData>> methodFactories) {
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
			
			Set<AbstractACOption> acOptions = methodFactory.getACOptions();
			Options options = new Options();
			for (AbstractACOption acoption : acOptions) {
				options.addOption(acoption.getOption());
			}
			
			methodFactory.printUsage();
			System.exit(0);
		}
		methodFactory.initParameters(getFilenames(args).length);
		// init method factory (populate: parameters)
		methodFactory.initACOptions();
		
		Set<AbstractACOption> acOptions = methodFactory.getACOptions();
		Options options = new Options();
		
		for (AbstractACOption acoption : acOptions) {
			options.addOption(acoption.getOption());
		}
		
		
		// copy arguments while ignoring the first array element
		String[] processedArgs = new String[args.length - 1];
		System.arraycopy(args, 1, processedArgs, 0, args.length - 1);

		// parse arguments
		final CommandLineParser parser = new PosixParser();
		try {
			// create hidden debug option
			AbstractACOption debugACOption = new DebugModusOption(methodFactory.getParameters());
			options.addOption(debugACOption.getOption());

			final CommandLine line = parser.parse(options, processedArgs);
			methodFactory.parseArgs(line.getArgs());

			for (AbstractACOption acOption : acOptions) {
				acOption.process(line);
			}
			// parse hidden debug option
			debugACOption.process(line);
		} catch (Exception e) {
			e.printStackTrace();
			methodFactory.printUsage();
			return false;
		}

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
		
		return true;
	}

	private String[] getFilenames(final String[] args) {
		for (int i = args.length - 1; i >= 0; --i) {
			if (args[i].startsWith("-")) {
				return Arrays.copyOfRange(args, i + 2, args.length);
			}
		}
		
		return args;
	}

	/**
	 * 
	 */
	public void printUsage() {
		final StringBuilder sb = new StringBuilder();
		
		for (final AbstractMethodFactory<?> methodFactory : methodFactories.values()) {
			sb.append("  ");
			sb.append(methodFactory.getName());
			sb.append("\t\t");
			sb.append(methodFactory.getDescription());
			sb.append('\n');
		}

		sb.append("Version: " + JACUSA.VERSION + "\n");
		System.err.print(sb.toString());
	}

	public AbstractMethodFactory<?> getMethodFactory() {
		return methodFactory;
	}

}