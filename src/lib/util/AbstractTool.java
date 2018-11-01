package lib.util;

import java.io.PrintStream;
import java.util.List;

import lib.cli.CLI;
import lib.method.AbstractMethod;
import lib.worker.WorkerDispatcher;

public abstract class AbstractTool {

	public static final String CALL_PREFIX = "JACUSA2 Version: ";
	
	private final String name;
	private final String version;
	
	private final String[] args;

	// command line interface
	private final CLI cli;
	private WorkerDispatcher workerDispatcher;
	
	private int comparisons;
	
	private static Logger logger;

	protected AbstractTool(
			final String name, final String version, 
			final String[] args,
			final List<AbstractMethod.AbstractFactory> factories) {
		this.name 		= name;
		this.version 	= version;
		this.args 		= args;

		cli = new CLI(factories);
		
		comparisons = 0;
		
		final PrintStream ps = System.err;
		logger = new Logger(ps, this);
		/*
		try {
			//PrintStream ps = new PrintStream(new File("JACAUSA2.log"));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		*/
	}

	public void run() throws Exception {
		// parse CLI
		if (! cli.processArgs(args)) {
			System.exit(1);
		}

		// prolog printed in logger
		getLogger().addProlog(getProlog());
		
		// instantiate chosen method
		final AbstractMethod methodFactory = cli.getMethodFactory();
				
		// run the method...
		workerDispatcher = methodFactory.getWorkerDispatcherInstance();
		comparisons = workerDispatcher.run();

		getLogger().addEpilog(getEpilog());
	}

	protected abstract String getEpilog();

	protected String getProlog() {
		final StringBuilder sb = new StringBuilder();
		final String lineSep = "--------------------------------------------------------------------------------";
		
		sb.append(lineSep);
		sb.append('\n');
		sb.append(getCall());
		sb.append('\n');
		sb.append(lineSep);
		return sb.toString();
	}
	
	public String getCall() {
		final StringBuilder sb = new StringBuilder();
		sb.append(CALL_PREFIX);
		sb.append(version);
		for(final String arg : args) {
			sb.append(" " + arg);
		}
		return sb.toString();
	}
	
	public String getName() {
		return name;
	}
	
	public String getVersion() {
		return version;
	}
	
	public static Logger getLogger() {
		return logger;
	}

	public CLI getCLI() {
		return cli;
	}

	public int getComparisons() {
		return comparisons;
	}

	public WorkerDispatcher getWorkerDispatcher() {
		return workerDispatcher;
	}

}
